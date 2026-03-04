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
import miwu.miot.model.att.SpecAtt
import miwu.miot.model.att.SpecAtt.Service
import miwu.miot.model.att.SpecAtt.Service.Property
import miwu.miot.model.miot.MiotDevice
import miwu.miot.provider.MiotSpecAttrProvider
import miwu.support.base.MiwuPanel
import miwu.widget.generated.widget.ActionRegistry
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass

typealias MiwuWidgetClass = Class<MiwuWidget<*>>
typealias MiwuPanelClass = Class<MiwuPanel<*>>

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
    private val deviceUrn = device.specType ?: ""
    private var isOutdated = false
    private val job = Job()
    private val scope = CoroutineScope(job)

    override val layout = MiwuLayout()

    override fun init() {
        scope.launch {
            initDevice()
            initWidgets()
            withContext(dispatcher) { callback?.onDeviceInitiated() }
            runRefreshLoop()
        }
    }

    override fun stop() {
        job.cancel()
        layout.clear()
        widgetHolders.forEach(WidgetHolder::recycler)
        widgetHolders.clear()
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
                if ((widget.siid == siid && widget.aiid == aiid) || widget.isMultiAttribute) widget.onActionCallback(
                    siid, aiid, result
                )
            }
        }
    }

    private fun initDevice() {
        val device = DeviceRegistry.registry[Urn.parseFrom(deviceUrn).name] ?: return
        val widgetAnnotations =
            device.annotations.firstOrNull { it is Widgets } as? Widgets ?: return
        supportWidget += (widgetAnnotations.widgets as Array<KClass<MiwuWidget<*>>>).map { it.java }
    }

    private fun runRefreshLoop() {
        scope.launch {
            while (true) {
                forEach()
                delay(refreshInterval)
            }
        }
    }

    private fun MiwuWidget<*>.setSpecAttr(att: SpecAtt) =
        apply {
            if (isMultiAttribute) field.miotSpecAtt = att
        }

    private fun MiwuWidget<*>.configureWith(
        service: Service,
        property: Property
    ) = apply {
        with(field) {
            siid = service.iid
            piid = property.iid
            propertyName = Urn.parseFrom(property.type).name
            serviceName = Urn.parseFrom(service.type).name
            desc = property.description
            valueOriginUnit = property.unit ?: ""
            serviceDesc = service.description
            serviceDescTranslation = service.descriptionTranslation
            descTranslation = property.descriptionTranslation
            allowWrite = "write" in property.access
            allowRead = "read" in property.access
            allowNotify = "notify" in property.access
            property.valueList?.also { valueList.addAll(it) }
        }
    }

    private fun MiwuWidget<*>.configureWith(
        service: Service,
        action: Service.Action,
    ) = apply {
        with(field) {
            siid = service.iid
            aiid = action.iid
            actionName = Urn.parseFrom(action.type).name
            serviceName = Urn.parseFrom(service.type).name
            serviceDesc = service.description
            serviceDescTranslation = service.descriptionTranslation
            desc = action.description
            descTranslation = action.descriptionTranslation
        }
    }

    private fun createWidget(
        widgetClass: MiwuWidgetClass,
        att: SpecAtt,
        service: Service,
        property: Property,
        value: Property.Value
    ) {
        widgetClass.createWidget()
            .configureWith(service, property)
            .setSpecAttr(att)
            .also { widget ->
                with(widget.field) {
                    desc = value.description
                    descTranslation = value.descriptionTranslation
                    serviceDesc = service.description
                    serviceDescTranslation = service.descriptionTranslation
                    setDefaultValue(value.value)
                }
            }
            .let { addWidget(it, widgetClass) }
    }

    private fun createWidget(
        widgetClass: MiwuWidgetClass,
        att: SpecAtt,
        service: Service,
        property: Property,
        repeat: Int = 0
    ) {
        when {
            widgetClass.isPanel() -> {
                widgetClass.createWidget()
                    .configureWith(service, property)
                    .setSpecAttr(att)
                    .let { it as? MiwuPanel<*> }
                    ?.also(MiwuPanel<*>::onCreateWidget)
                    ?.let(MiwuPanel<*>::list)
                    ?.forEach { (clazz, widget) ->
                        addWidget(widget, clazz)
                    }
            }

            widgetClass.hasValueList() -> {
                when (val pointTo = widgetClass.getPointTo()) {
                    ValueList::class -> {
                        property.valueList?.forEach { value ->
                            createWidget(widgetClass, att, service, property, value)
                        }
                    }

                    else -> {
                        if (repeat > 3) return // 这里加入递归上限
                        runCatching {
                            pointTo as KClass<MiwuWidget<*>>
                            createWidget(pointTo.java, att, service, property, repeat + 1)
                        }
                    }
                }
            }

            else -> {
                widgetClass.createWidget()
                    .configureWith(service, property)
                    .setSpecAttr(att)
                    .also { widget ->
                        property.valueRange?.let {
                            widget.field.setValueRange(it[0], it[1], it[2])
                        }
                    }
                    .let { addWidget(it, widgetClass) }
            }
        }
    }

    private fun createWidget(
        widgetClass: MiwuWidgetClass,
        att: SpecAtt,
        service: Service,
        action: Service.Action,
    ) {
        widgetClass.createWidget()
            .configureWith(service, action)
            .setSpecAttr(att)
            .let { widget -> addWidget(widget, widgetClass) }
    }


    private suspend fun initWidgets() {
        val att = getAtt().getOrElse {
            callback?.onDeviceInitiatedFailure(it)
            return
        }

        callback?.onDeviceAttLoaded(att)
        cache.putSpecAtt(deviceUrn, att)

        att.initVariable()
        getLanguageMap().getOrNull()?.let(att::convertLanguage)

        att.services.forEach service@{ service ->
            service.properties?.forEach property@{ property ->
                getPropertyWidgetClass(service.type, property.type)
                    ?.takeIf(supportWidget::contains)
                    ?.also { createWidget(it, att, service, property) }
            }

            service.actions?.forEach action@{ action ->
                getActionWidgetClass(service.type, action.type)
                    ?.takeIf(supportWidget::contains)
                    ?.let { createWidget(it, att, service, action) }
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

    private suspend fun getAtt(): Result<SpecAtt> = runCatching {
        cache.getSpecAtt(deviceUrn) ?: device.getSpecAtt(specAttrProvider).getOrThrow()
    }

    private suspend fun getLanguageMap(): Result<Map<String, String>> = runCatching {
        cache.getLanguageMap(deviceUrn) ?: device.getSpecAttLanguageMap(specAttrProvider)
            .getOrThrow()
    }

    private fun getPropertyWidgetClass(
        serviceType: String, propertyType: String
    ): MiwuWidgetClass? =
        runCatching {
            PropertyRegistry.registry[key(serviceType, propertyType)] as? MiwuWidgetClass
        }.getOrNull()

    private fun getActionWidgetClass(
        serviceType: String, actionType: String
    ): MiwuWidgetClass? =
        runCatching {
            ActionRegistry.registry[key(serviceType, actionType)] as? MiwuWidgetClass
        }.getOrNull()

    /**
     * 构造键值对
     *
     * @param serviceType 服务类型
     * @param itemType 属性类型 propertyType, actionType
     * @return 键值对
     *
     * @see [Urn]
     */

    private fun key(
        serviceType: String,
        itemType: String
    ): Pair<String, String> =
        Urn.parseFrom(serviceType).name to Urn.parseFrom(itemType).name

    private fun MiwuWidgetClass.isPanel() = isAssignableFrom(MiwuPanel::class.java)

    private fun MiwuWidgetClass.hasValueList() =
        annotations.find { it is ValueList } != null

    private fun MiwuWidgetClass.getPointTo() =
        annotations.find { it is ValueList }.let {
            return@let when (it) {
                is ValueList -> it.pointTo
                else -> ValueList::class
            }
        }

    private fun MiwuWidgetClass.getPosition() =
        annotations.find { it is Body || it is Footer || it is Header || it is SubHeader || it is SubFooter }

    private fun MiwuWidgetClass.createWidget() =
        this.getDeclaredConstructor().newInstance()

    private suspend fun forEach() =
        withContext(Dispatchers.IO) {
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

    private inner class WidgetHolder(
        val widget: MiwuWidget<*>
    ) {
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

