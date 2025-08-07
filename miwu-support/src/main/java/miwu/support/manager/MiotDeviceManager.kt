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
import miwu.support.api.Controller
import miwu.support.base.MiwuWidget
import miwu.icon.Icons
import miwu.support.layout.MiwuLayout
import miwu.support.manager.callback.DeviceManagerCallback
import miwu.widget.generated.device.DeviceRegistry
import miwu.widget.generated.widget.ServiceRegistry
import miwu.miot.MiotClient
import miwu.miot.MiotManager
import miwu.miot.att.get.GetAtt
import miwu.miot.model.att.DeviceAtt
import miwu.miot.model.miot.MiotDevice
import miwu.support.translate.TranslateHelper
import miwu.support.urn.Urn
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass

/**
 * @author Sky233
 * @param miot 用于调用 MiotClient 接口
 * @param manager 用于调用 MiotManager 接口
 * @param device 设备详情
 * @param cache 用于缓存设备属性
 * @param dispatcher 用于更新 UI 数据的线程
 * @param callback 用于回调设备初始化状态
 * */
class MiotDeviceManager(
    val miot: MiotClient,
    val manager: MiotManager,
    val device: MiotDevice,
    val icons: Icons,
    val cache: Cache,
    val translateHelper: TranslateHelper,
    val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    val callback: DeviceManagerCallback? = null
) : Controller {
    private val refreshInterval = 1000L
    private val updatingInterval = 500L
    private val widgetHolders = CopyOnWriteArrayList<WidgetHolder>()
    private val supportWidget = mutableSetOf<Class<MiwuWidget<*>>>()
    private val deviceUrn = device.specType!!
    private var isOutdated = false

    val layout = MiwuLayout()
    val job = Job()
    val scope = CoroutineScope(job)


    fun initDevice() {
        val device = DeviceRegistry.registry[Urn.parseFrom(deviceUrn).name] ?: return
        val widgetAnnotations =
            device.annotations.firstOrNull { it is Widgets } as? Widgets ?: return
        supportWidget += (widgetAnnotations.widgets as Array<KClass<MiwuWidget<*>>>).map { it.java }
    }

    fun init() {
        scope.launch {
            initDevice()
            initWidgets()
            withContext(dispatcher) { callback?.onDeviceInitiated() }
            run()
        }
    }

    fun run() {
        scope.launch {
            while (true) {
                forEach()
                delay(refreshInterval)
            }
        }
    }

    private suspend fun initWidgets() {
        val att = getAtt() ?: return
        val languageMap = getLanguageMap() ?: return

        cache.putSpecAtt(deviceUrn, att)
        cache.putSpecAtt(deviceUrn, att)

        att.initVariable()
        att.convertLanguage(languageMap)

        val services = att.services

        for (service in services) {
            val properties = service.properties ?: continue

            for (property in properties) {
                val widgetClass = getWidgetClass(service.type, property.type) ?: continue

                if (widgetClass !in supportWidget) continue

                fun MiwuWidget<*>.config() = this.apply {
                    _siid = service.iid
                    _piid = property.iid
                    _aiid = property.iid
                    _desc = property.description
                    _valueOriginUnit = property.unit ?: "null"
                    _descTranslation = property.descriptionTranslation
                    property.valueList?.let {
                        _valueList.addAll(it)
                    }
                }

                if (widgetClass.hasValueList() || property.valueList?.isNotEmpty() ?: false) {
                    val pointTo = widgetClass.getPointTo()
                    when(pointTo){
                        ValueList::class -> {
                            property.valueList?.forEach {
                                val widget = widgetClass.createWidget().config()
                                widget._desc = it.description
                                widget._descTranslation = it.descriptionTranslation
                                widget.setDefaultValue(it.value)
                                addWidget(widget, widgetClass)
                            }
                        }
                        MiwuWidget::class -> {
                            property.valueList?.forEach {
                                val widget = (pointTo.java as Class<MiwuWidget<*>>).createWidget().config()
                                widget._desc = it.description
                                widget._descTranslation = it.descriptionTranslation
                                widget.setDefaultValue(it.value)
                                addWidget(widget, widgetClass)
                            }
                        }
                    }
                } else {
                    val widget = widgetClass.createWidget().config()
                    property.valueRange?.let {
                        widget.setValueRange(it[0], it[1], it[2])
                    }
                    addWidget(widget, widgetClass)
                }
            }

            // TODO actions
        }
    }


    override fun onUpdateValue(siid: Int, piid: Int, value: Any) {
        scope.launch {
            isOutdated = true
            for (i in widgetHolders) {
                val widget = i.widget
                if (widget.siid == siid && widget.piid == piid) widget.updateValue(value)
            }
            miot.Device.set(device, arrayOf(siid to piid to value))
        }
    }

    override fun doAction(siid: Int, aiid: Int, input: Any?) {
        scope.launch {
            if (input != null) miot.Device.action(device, siid, aiid, input)
            else miot.Device.action(device, siid, aiid)
        }
    }

    private fun addWidget(widget: MiwuWidget<*>, widgetClass: Class<MiwuWidget<*>>) {
        val widgetPosition = widgetClass.getPosition()

        widget._icons = icons
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
            holder.bind(this)
            widgetHolders.add(holder)
        }
    }

    private suspend fun getAtt() = cache.getSpecAtt(deviceUrn) ?: device.getSpecAtt(manager)

    private suspend fun getLanguageMap() =
        cache.getLanguageMap(deviceUrn) ?: device.getSpecAttLanguageMap(manager)

    private fun getWidgetClass(serviceType: String, propertyType: String): Class<MiwuWidget<*>>? {
        val map = ServiceRegistry.registry[Urn.parseFrom(serviceType).name] ?: return null
        return map[Urn.parseFrom(propertyType).name] as? Class<MiwuWidget<*>>
    }


    private fun Class<MiwuWidget<*>>.hasValueList() = annotations.find { it is ValueList } != null

    private fun Class<MiwuWidget<*>>.getPointTo() = (annotations.find { it is ValueList } as ValueList).pointTo

    private fun Class<MiwuWidget<*>>.getPosition() =
        annotations.find { it is Body || it is Footer || it is Header || it is SubHeader || it is SubFooter }

    private fun Class<MiwuWidget<*>>.createWidget() = this.getDeclaredConstructor().newInstance()

    private suspend fun forEach() = withContext(Dispatchers.IO) {
        val attList = arrayListOf<GetAtt>()
        for (i in widgetHolders) {
            val widget = i.widget
            if (widget.piid == -1) continue
            attList.add(widget.siid to widget.piid)
        }
        if (attList.isEmpty()) return@withContext
        runCatching {
            miot.Device.get(device, attList.toTypedArray())
        }.onSuccess {
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

    private class WidgetHolder(val widget: MiwuWidget<*>) {
        private var _controller: ControllerWrapper? = null

        fun bind(controller: Controller) {
            _controller = ControllerWrapper(controller)
            widget.bind(_controller!!)
        }

        fun unbind(controller: Controller? = null) {
            _controller = null
            widget.unbind(_controller)
        }

    }

    class ControllerWrapper(private val controller: Controller) : Controller {

        override fun onUpdateValue(siid: Int, piid: Int, value: Any) {
            controller.onUpdateValue(siid, piid, value)
        }

        override fun doAction(siid: Int, aiid: Int, input: Any?) {
            controller.doAction(siid, aiid, input)
        }

    }
}

