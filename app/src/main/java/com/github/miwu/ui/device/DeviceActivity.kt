package com.github.miwu.ui.device

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.github.miwu.MainApplication.Companion.gson
import com.github.miwu.logic.repository.AppRepository
import kndroidx.activity.ViewActivityX
import kndroidx.extension.start
import kotlinx.coroutines.Dispatchers
import miwu.android.R
import miwu.android.icon.generated.icon.AndroidIcons
import miwu.android.translate.AndroidTranslateHelper
import miwu.android.wrapper.base.BaseMiwuWrapper
import miwu.android.wrapper.base.MiwuWrapper
import miwu.miot.MiotManager
import miwu.miot.model.att.SpecAtt
import miwu.miot.model.miot.MiotDevice
import miwu.miot.utils.to
import miwu.support.api.Cache
import miwu.support.base.MiwuWidget
import miwu.support.layout.on
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
    private val appRepository: AppRepository by inject()
    private val miotManager: MiotManager by inject()
    private val wrapperList = arrayListOf<BaseMiwuWrapper<*>>()
    private val manager by lazy {
        MiotDeviceManager(
            appRepository.miotClient,
            miotManager,
            device,
            AndroidIcons,
            AndroidCache(this),
            AndroidTranslateHelper,
            Dispatchers.Main,
            this
        )
    }


    private fun ViewGroup.addWrapper(wrapper: BaseMiwuWrapper<*>) {
        addView(wrapper.view.apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(
                    0,
                    0,
                    0,
                    context.resources.getDimensionPixelSize(R.dimen.device_miwu_layout_margin_bottom)
                )
            }
        })
    }

    override fun onDeviceInitiated() {
        on(manager.layout) {
            Header { widget ->
                binding.header.let { viewGroup ->
                    viewGroup.visibility = View.VISIBLE
                    createWrapper(widget)?.let {
                        wrapperList.add(it)
                        viewGroup.addWrapper(it)
                    }
                }
            }
            SubHeader { widget ->
                binding.subHeader.let { viewGroup ->
                    viewGroup.visibility = View.VISIBLE
                    createWrapper(widget)?.let {
                        wrapperList.add(it)
                        viewGroup.addWrapper(it)
                    }
                }
            }
            Body { widget ->
                binding.body.let { viewGroup ->
                    viewGroup.visibility = View.VISIBLE
                    createWrapper(widget)?.let {
                        wrapperList.add(it)
                        viewGroup.add(it.view)
                    }
                }
            }
            SubFooter { widget ->
                binding.subFooter.let { viewGroup ->
                    viewGroup.visibility = View.VISIBLE
                    createWrapper(widget)?.let {
                        wrapperList.add(it)
                        viewGroup.addWrapper(it)
                    }
                }
            }
            Footer { widget ->
                binding.footer.let { viewGroup ->
                    viewGroup.visibility = View.VISIBLE
                    createWrapper(widget)?.let {
                        wrapperList.add(it)
                        viewGroup.addWrapper(it)
                    }
                }
            }
            Unknown {

            }
        }
        wrapperList.forEach {
            it.init()
        }
    }

    fun onAddButtonClick() {

    }

    fun onStarButtonClick() {

    }

    @Suppress("UNCHECKED_CAST")
    private fun createWrapper(miotWidget: MiwuWidget<*>): BaseMiwuWrapper<*>? {
        val wrapperClass =
            WrapperRegistry.registry[miotWidget::class.java] as? Class<out BaseMiwuWrapper<*>>
                ?: return null
        return wrapperClass.getDeclaredConstructor(
            Context::class.java,
            MiwuWidget::class.java,
        ).newInstance(this, miotWidget)
    }

    override fun init() {
        manager.init()
    }


    override fun onDestroy() {
        super.onDestroy()
    }


    companion object {
        fun Context.startDeviceActivity(device: MiotDevice) {
            start<DeviceActivity> {
                putExtra("device", gson.toJson(device))
            }
        }
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
            file.writeText(gson.toJson(specAtt))
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
            file.writeText(gson.toJson(map))
        }
    }

}
