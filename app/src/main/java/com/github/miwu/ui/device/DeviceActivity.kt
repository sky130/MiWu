package com.github.miwu.ui.device

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.github.miwu.utils.Logger
import com.github.miwu.utils.MiotDeviceClient
import kndroidx.activity.ViewActivityX
import kndroidx.extension.start
import kotlinx.coroutines.Dispatchers
import miwu.android.R
import miwu.android.icon.generated.icon.AndroidIcons
import miwu.android.translate.AndroidTranslateHelper
import miwu.android.wrapper.base.ViewMiwuWrapper
import miwu.miot.kmp.utils.json
import miwu.miot.kmp.utils.to
import miwu.miot.model.MiotUser
import miwu.miot.model.att.SpecAtt
import miwu.miot.model.miot.MiotDevice
import miwu.miot.provider.MiotSpecAttrProvider
import miwu.support.api.Cache
import miwu.support.base.MiwuWidget
import miwu.support.manager.MiotDeviceManager
import miwu.support.manager.callback.DeviceManagerCallback
import miwu.widget.generated.wrapper.WrapperRegistry
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import com.github.miwu.databinding.ActivityDeviceBinding as Binding

class DeviceActivity : ViewActivityX<Binding>(Binding::inflate), DeviceManagerCallback {
    override val viewModel: DeviceViewModel by viewModel()
    private val device by lazy { intent.getStringExtra("device")!!.to<MiotDevice>() }
    private val user by lazy { intent.getStringExtra("user")!!.to<MiotUser>() }
    private val logger = Logger()
    private val miotDeviceClient by lazy { MiotDeviceClient(user) }
    private val specAttrProvider: MiotSpecAttrProvider by inject()
    private val wrapperList = arrayListOf<ViewMiwuWrapper<*>>()
    private val manager by lazy {
        MiotDeviceManager(
            miotDeviceClient,
            specAttrProvider,
            device,
            AndroidIcons,
            AndroidCache(this),
            AndroidTranslateHelper,
            Dispatchers.Main,
            this
        )
    }

    private fun ViewGroup.addWrapper(wrapper: ViewMiwuWrapper<*>) {
        addView(wrapper.view.apply {
            val bottom =
                context.resources.getDimensionPixelSize(R.dimen.device_miwu_layout_margin_bottom)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, bottom) }
        })
    }

    private inline fun <reified T : ViewGroup> T.addWidget(
        widget: MiwuWidget<*>,
        add: T.(ViewMiwuWrapper<*>) -> Unit = { addWrapper(it) }
    ) {
        logger.debug("Widget found: {}", widget)
        createWrapper(widget)?.let {
            visibility = View.VISIBLE
            wrapperList.add(it)
            add(it)
        }
    }

    override fun onDeviceInitiated() {
        initDeviceLayout()
        wrapperList.forEach { it.init() }
    }

    override fun onDeviceAttLoaded(specAtt: SpecAtt) {
        logger.info("Device {}, spec att: {}", device.name, specAtt)
    }

    private fun initDeviceLayout() {
        with(manager.layout) {
            with(binding) {
                Header {
                    header.addWidget(it)
                }
                SubHeader {
                    subHeader.addWidget(it)
                }
                Body {
                    body.addWidget(it) { add(it.view) }
                }
                SubFooter {
                    subFooter.addWidget(it)
                }
                Footer {
                    footer.addWidget(it)
                }
                Unknown {
                    unknown.addWidget(it)
                }
            }
        }
    }

    fun onAddButtonClick() {

    }

    fun onStarButtonClick() {
        viewModel.addFavorite(device)
    }

    override fun init() {
        with(device) {
            logger.info(
                "Current miot device info: model={}, mac={}, did={}, isOnline={}, specType={}",
                model, mac, did, isOnline, specType,
            )
            logger.debug("Current miot all device info: {}", this)
        }
        manager.init()
    }

    override fun onDestroy() {
        manager.stop()
        super.onDestroy()
    }

    @Suppress("UNCHECKED_CAST")
    private fun createWrapper(miotWidget: MiwuWidget<*>): ViewMiwuWrapper<*>? {
        val wrapperClass =
            WrapperRegistry.registry[miotWidget::class.java] as? Class<out ViewMiwuWrapper<*>>
                ?: return null
        return wrapperClass.getDeclaredConstructor(
            Context::class.java,
            MiwuWidget::class.java,
        ).newInstance(this, miotWidget)
    }

    inner class AndroidCache(val context: Context) : Cache {
        override suspend fun getSpecAtt(urn: String): SpecAtt? {
            try {
                val file = File("${context.cacheDir.absolutePath}/${urn.hashCode()}.att")
                return if (!file.isFile) {
                    return null
                } else {
                    file.readText().to<SpecAtt>()
                }
            } catch (e: Exception) {
                return null
            }
        }

        override suspend fun putSpecAtt(urn: String, specAtt: SpecAtt) {
            val file = File("${context.cacheDir.absolutePath}/${urn.hashCode()}.att")
            file.writeText(json.encodeToString(specAtt))
        }

        override suspend fun getLanguageMap(urn: String): Map<String, String>? {
            try {
                val file = File("${context.cacheDir.absolutePath}/${urn.hashCode()}.map")
                return if (!file.isFile) {
                    return null
                } else {
                    file.readText().to<Map<String, String>>()
                }
            } catch (e: Exception) {
                return null
            }
        }

        override suspend fun putLanguageMap(
            urn: String, map: Map<String, String>
        ) {
            val file = File("${context.cacheDir.absolutePath}/${urn.hashCode()}.map")
            file.writeText(json.encodeToString(map))
        }
    }

    companion object {
        fun Context.startDeviceActivity(device: MiotDevice, user: MiotUser) {
            start<DeviceActivity> {
                putExtra("device", json.encodeToString(device))
                putExtra("user", json.encodeToString(user))
            }
        }
    }
}
