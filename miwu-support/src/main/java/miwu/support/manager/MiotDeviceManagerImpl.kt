@file:Suppress("UNCHECKED_CAST")

package miwu.support.manager

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miwu.annotation.ValueList
import miwu.annotation.Widgets
import miwu.annotation.widget.Body
import miwu.annotation.widget.Footer
import miwu.annotation.widget.Header
import miwu.annotation.widget.SubFooter
import miwu.annotation.widget.SubHeader
import miwu.support.api.Cache
import miwu.support.base.MiwuWidget
import miwu.icon.Icons
import miwu.support.layout.MiwuLayout
import miwu.widget.generated.device.DeviceRegistry
import miwu.widget.generated.widget.PropertyRegistry
import miwu.miot.att.get.GetAtt
import miwu.miot.client.MiotDeviceClient
import miwu.support.urn.Urn
import miwu.support.translate.TranslateHelper
import miwu.miot.model.att.DeviceAtt
import miwu.miot.model.miot.MiotDevice
import miwu.miot.provider.MiotSpecAttrProvider
import miwu.widget.generated.widget.ActionRegistry
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass

typealias MiwuWidgetClass = Class<MiwuWidget<*>>

/**
 * @author Sky233
 * @param miot 用于调用 MiotClient 接口
 * @param specAttrProvider 用于调用 MiotManager 接口
 * @param device 设备详情
 * @param cache 用于缓存设备属性
 * @param dispatcher 用于更新 UI 数据的线程
 * @param callback 用于回调设备初始化状态
 * */
class MiotDeviceManagerImpl internal constructor(
    val miot: MiotDeviceClient,
    val specAttrProvider: MiotSpecAttrProvider,
    val device: MiotDevice,
    val icons: Icons,
    val cache: Cache,
    val translateHelper: TranslateHelper,
    val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    val callback: Callback? = null
) : MiotDeviceManager() {
    private val refreshInterval = 1000L
    private val updatingInterval = 500L
    private val widgetHolders = CopyOnWriteArrayList<WidgetHolder>()
    private val supportWidget = mutableSetOf<MiwuWidgetClass>()
    private val deviceUrn = device.specType!!
    private var isOutdated = false
    private val job = Job()
    private val scope = CoroutineScope(job)

    override val layout = MiwuLayout()

    override fun init() {
        scope.launch {
            initDevice()
            initWidgets()
            withContext(dispatcher) { callback?.onDeviceInitiated() }
            run()
        }
    }

    override fun stop() {
        job.cancel()
        layout.clear()
        widgetHolders.forEach(WidgetHolder::recycler)
        widgetHolders.clear()
    }

    private fun initDevice() {
        val device = DeviceRegistry.registry[Urn.parseFrom(deviceUrn).name] ?: return
        val widgetAnnotations =
            device.annotations.firstOrNull { it is Widgets } as? Widgets ?: return
        supportWidget += (widgetAnnotations.widgets as Array<KClass<MiwuWidget<*>>>).map { it.java }
    }

    private fun run() {
        scope.launch {
            while (true) {
                forEach()
                delay(refreshInterval)
            }
        }
    }

    private suspend fun initWidgets() {
        val att = getAtt() ?: return

        callback?.onDeviceAttLoaded(att)
        cache.putSpecAtt(deviceUrn, att)

        att.initVariable()
        getLanguageMap()?.let { att.convertLanguage(it) }

        fun MiwuWidget<*>.setAttr() {
            if (isMultiAttribute) field.miotSpecAtt = att
        }

        val services = att.services

        for (service in services) {
            val properties = service.properties
            val actions = service.actions
            properties?.let { properties ->
                for (property in properties) {
                    val widgetClass =
                        getPropertyWidgetClass(service.type, property.type) ?: continue

                    if (widgetClass !in supportWidget) continue

                    fun MiwuWidget<*>.config() = this.apply {
                        with(field) {
                            siid = service.iid
                            piid = property.iid
                            propertyName = Urn.parseFrom(property.type).name
                            serviceName = Urn.parseFrom(service.type).name
                            desc = property.description
                            valueOriginUnit = property.unit ?: ""
                            descTranslation = property.descriptionTranslation
                            allowWrite = "write" in property.access
                            allowRead = "read" in property.access
                            allowNotify = "notify" in property.access
                            property.valueList?.also { valueList.addAll(it) }
                        }
                        setAttr()
                    }

                    fun load(widgetClass: MiwuWidgetClass) {
                        if (widgetClass.hasValueList()) {
                            when (val pointTo = widgetClass.getPointTo()) {
                                ValueList::class -> {
                                    property.valueList?.forEach {
                                        val widget = widgetClass.createWidget().config()
                                        with(widget.field) {
                                            desc = it.description
                                            descTranslation = it.descriptionTranslation
                                            setDefaultValue(it.value)
                                        }
                                        addWidget(widget, widgetClass)
                                    }
                                }

                                else -> {
                                    runCatching {
                                        pointTo as KClass<MiwuWidget<*>>
                                        load(pointTo.java)
                                    }
                                }
                            }
                        } else {
                            val widget = widgetClass.createWidget().config()
                            property.valueRange?.let {
                                widget.field.setValueRange(it[0], it[1], it[2])
                            }
                            addWidget(widget, widgetClass)
                        }
                    }

                    load(widgetClass)
                }
            }
            actions?.let { actions ->
                for (action in actions) {
                    val widgetClass = getActionWidgetClass(service.type, action.type) ?: continue

                    if (widgetClass !in supportWidget) continue

                    fun MiwuWidget<*>.config() = this.apply {
                        with(field) {
                            siid = service.iid
                            aiid = action.iid
                            actionName = Urn.parseFrom(action.type).name
                            serviceName = Urn.parseFrom(service.type).name
                            desc = action.description
                            descTranslation = action.descriptionTranslation
                        }
                        setAttr()
                    }

                    val widget = widgetClass.createWidget().config()
                    addWidget(widget, widgetClass)
                }
            }
        }
    }

    override fun updateValue(siid: Int, piid: Int, value: Any) {
        scope.launch {
            isOutdated = true
            for (i in widgetHolders) {
                val widget = i.widget
                if (widget.siid == siid && widget.piid == piid) widget.updateValue(value)
                if (widget.isMultiAttribute) widget.updateValue(siid, piid, value)
            }
            miot.set(device, arrayOf(siid to piid to value))
        }
    }

    override fun doAction(siid: Int, aiid: Int, vararg input: Any) {
        scope.launch {
            val result = miot.action(device, siid, aiid, *input).getOrNull() ?: return@launch
            for (i in widgetHolders) {
                val widget = i.widget
                if ((widget.siid == siid && widget.aiid == aiid) || widget.isMultiAttribute)
                    widget.onActionCallback(
                        siid,
                        aiid,
                        result
                    )
            }
        }
    }

    private fun addWidget(widget: MiwuWidget<*>, widgetClass: MiwuWidgetClass) {
        val widgetPosition = widgetClass.getPosition()

        widget.field.icons = icons
        widget.translateHelper = translateHelper

        when (widgetPosition) {
            is Body -> layout.body.add(widget)
            is Footer -> layout.footer.add(widget)
            is Header -> layout.header.add(widget)
            is SubFooter -> layout.subFooter.add(widget)
            is SubHeader -> layout.subHeader.add(widget)
            else -> layout.unknown.add(widget)
        }

        WidgetHolder(widget).let { holder ->
            holder.bind()
            widgetHolders.add(holder)
        }
    }

    private suspend fun getAtt() =
        cache.getSpecAtt(deviceUrn) ?: device.getSpecAtt(specAttrProvider).getOrNull()

    private suspend fun getLanguageMap() =
        cache.getLanguageMap(deviceUrn) ?: device.getSpecAttLanguageMap(specAttrProvider)
            .getOrNull()

    private fun getPropertyWidgetClass(
        serviceType: String, propertyType: String
    ): MiwuWidgetClass? {
        val map =
            PropertyRegistry.registry[Urn.parseFrom(serviceType).name to Urn.parseFrom(propertyType).name]
                ?: return null
        return map as? MiwuWidgetClass
    }

    private fun getActionWidgetClass(
        serviceType: String, propertyType: String
    ): MiwuWidgetClass? {
        val map =
            ActionRegistry.registry[Urn.parseFrom(serviceType).name to Urn.parseFrom(propertyType).name]
                ?: return null
        return map as? MiwuWidgetClass
    }

    private fun MiwuWidgetClass.hasValueList() = annotations.find { it is ValueList } != null

    private fun MiwuWidgetClass.getPointTo() = annotations.find { it is ValueList }.let {
        return@let when (it) {
            is ValueList -> it.pointTo
            else -> ValueList::class
        }
    }

    private fun MiwuWidgetClass.getPosition() =
        annotations.find { it is Body || it is Footer || it is Header || it is SubHeader || it is SubFooter }

    private fun MiwuWidgetClass.createWidget() = this.getDeclaredConstructor().newInstance()

    private suspend fun forEach() = withContext(Dispatchers.IO) {
        val attList = arrayListOf<GetAtt>()
        for (i in widgetHolders) {
            val widget = i.widget
            if (!widget.allowRead) continue
            if (widget.piid == -1) continue
            attList.add(widget.siid to widget.piid)
        }
        if (attList.isEmpty()) return@withContext
        miot.get(device, attList.toTypedArray()).onSuccess {
            update(it.result ?: return@onSuccess)
        }
    }

    private suspend fun update(list: ArrayList<DeviceAtt.Att>) = withContext(dispatcher) {
        for (holder in widgetHolders) {
            val widget = holder.widget
            var cacheAtt: DeviceAtt.Att? = null
            for (att in list) {
                if (att.siid != widget.siid || att.piid != widget.piid) continue
                if (isOutdated) return@withContext { isOutdated = false }.invoke()
                widget.updateValue(att.value)
                cacheAtt = att
            }
            cacheAtt?.let { list.remove(it) }
        }
    }

    private inner class WidgetHolder(val widget: MiwuWidget<*>) {
        fun bind() {
            widget.bind(this@MiotDeviceManagerImpl)
        }

        fun recycler() {
            widget.recycler()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> MiwuWidget<T>.updateValue(value: Any?) {
        runCatching {
            val t = value as T
            onValueChange(t)
        }.recoverCatching {
            val t = value.toString() as T
            onValueChange(t)
        }
    }

    private fun MiwuWidget<*>.updateValue(siid: Int, piid: Int, value: Any?) {
        if (siid to piid !in iidList) return
        runCatching {
            onValueChange(siid, piid, value!!)
        }.recoverCatching {
            onValueChange(siid, piid, value.toString() as Any)
        }
    }
}

