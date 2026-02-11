package com.github.miwu.ui.device

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.github.miwu.ui.device.DeviceViewModel.Event.DeviceInitiated
import com.github.miwu.utils.Logger
import com.github.miwu.utils.MiotDeviceClient
import fr.haan.resultat.Resultat
import kndroidx.activity.ViewActivityX
import kndroidx.extension.start
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
import miwu.support.base.MiwuWidget
import miwu.support.base.MiwuWrapper
import miwu.support.manager.MiotDeviceManager
import miwu.widget.generated.wrapper.WrapperRegistry
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.github.miwu.databinding.ActivityDeviceBinding as Binding

class DeviceActivity : ViewActivityX<Binding>(Binding::inflate) {
    override val viewModel: DeviceViewModel by viewModel()
    private val logger = Logger()
    private val marginBottom by lazy { resources.getDimensionPixelSize(R.dimen.device_miwu_layout_margin_bottom) }
    private val wrapperList = arrayListOf<ViewMiwuWrapper<*>>()

    override fun init() {
        with(viewModel) {
            event.onEach(::onEvent)
                .launchIn(lifecycleScope)
            printDeviceInfo()
            manager.init()
        }
    }

    override fun onDestroy() {
        viewModel.manager.stop()
        super.onDestroy()
    }

    fun onEvent(event: DeviceViewModel.Event) {
        when(event) {
            DeviceInitiated -> {
                initDeviceLayout()
                wrapperList.forEach(MiwuWrapper<*>::init)
            }
        }
    }

    fun onAddButtonClick() {

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
        with(viewModel.manager.layout) {
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

    @Suppress("UNCHECKED_CAST")
    private fun createWrapper(miotWidget: MiwuWidget<*>): ViewMiwuWrapper<*>? =
        WrapperRegistry.registry[miotWidget.javaClass]
            .let { it as? Class<out ViewMiwuWrapper<*>> }
            ?.run { getDeclaredConstructor(Context::class.java, MiwuWidget::class.java) }
            ?.newInstance(this, miotWidget)

    companion object {
        fun Context.startDeviceActivity(device: MiotDevice, user: MiotUser) {
            start<DeviceActivity> {
                putExtra("device", json.encodeToString(device))
                putExtra("user", json.encodeToString(user))
            }
        }
    }
}