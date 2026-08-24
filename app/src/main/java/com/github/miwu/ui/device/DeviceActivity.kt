package com.github.miwu.ui.device

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.github.miwu.ui.device.DeviceViewModel.Event.DeviceInitiated
import com.github.miwu.utils.Logger
import kndroidx.activity.ViewActivityX
import kndroidx.extension.start
import kndroidx.extension.toast
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import miwu.android.R
import miwu.android.wrapper.base.ViewMiwuWrapper
import miwu.miot.model.miot.MiotDevice
import miwu.support.MiwuWidget
import miwu.support.MiwuWrapper
import miwu.support.generated.wrapper.WrapperRegistry
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.github.miwu.databinding.ActivityDeviceBinding as Binding

class DeviceActivity : ViewActivityX<Binding>(Binding::inflate) {
    override val viewModel: DeviceViewModel by viewModel()
    private val logger = Logger()
    private val marginBottom by lazy { resources.getDimensionPixelSize(R.dimen.device_miwu_layout_margin_bottom) }
    private val wrapperList = arrayListOf<ViewMiwuWrapper<*>>()

    override fun init() {
        if (viewModel.device == null || viewModel.manager == null) {
            "设备不可用，请返回后重试".toast()
            finish()
            return
        }
        with(viewModel) {
            event.receiveAsFlow()
                .onEach(::onEvent)
                .launchIn(lifecycleScope)
            printDeviceInfo()
            manager?.init()
        }
    }

    override fun onDestroy() {
        viewModel.manager?.stop()
        super.onDestroy()
    }

    fun onEvent(event: DeviceViewModel.Event) {
        when (event) {
            DeviceInitiated -> {
                initDeviceLayout()
                if (wrapperList.isNotEmpty()) {
                    binding.placeholder.isVisible = false
                }
                wrapperList.forEach(MiwuWrapper<*>::init)
            }
        }
    }

    fun onStarButtonClick() {
        viewModel.addFavorite()
    }

    private inline fun <reified T : ViewGroup> T.addWidget(
        widget: MiwuWidget<*>,
        onWrapperCreated: T.(ViewMiwuWrapper<*>) -> Unit = { addWrapper(it) }
    ) = createWrapper(widget)
        ?.also { logger.debug("Widget found: {}", widget) }
        ?.also { isVisible = true }
        ?.also { onWrapperCreated(it) }
        ?.also(wrapperList::add)

    private fun ViewGroup.addWrapper(wrapper: ViewMiwuWrapper<*>) =
        wrapper.view
            .apply { layoutParams = createLayoutParams() }
            .let(::addView)

    private fun createLayoutParams() =
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, marginBottom)
        }

    private fun initDeviceLayout() {
        with(viewModel.manager?.layout ?: return) {
            with(binding) {
                Header {
                    header.addWidget(it)
                }
                HeaderPanel {
                    headerPanel.addWidget(it)
                }
                ControlPanel {
                    controlPanel.addWidget(it)
                }
                Body {
                    body.addWidget(it) { add(it.view) }
                }
                FooterPanel {
                    footerPanel.addWidget(it)
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

    private fun createWrapper(miotWidget: MiwuWidget<*>): ViewMiwuWrapper<*>? =
        WrapperRegistry.create(this, miotWidget)

    companion object {
        fun Context.startDeviceActivity(device: MiotDevice) {
            start<DeviceActivity> {
                putExtra("did", device.did)
            }
        }
    }
}
