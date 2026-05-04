@file:Suppress("PrivatePropertyName")

package miwu.android.wrapper.curtain

import android.content.Context
import miwu.android.icon.generated.icon.AndroidIcons
import miwu.android.view.binding.icon
import miwu.android.view.binding.onClick
import miwu.android.view.binding.title
import miwu.android.view.binding.updateState
import miwu.android.wrapper.base.MiwuLayoutWrapper
import miwu.android.wrapper.curtain.CurtainLayoutWrapper.CurtainState.Closing
import miwu.android.wrapper.curtain.CurtainLayoutWrapper.CurtainState.Opening
import miwu.android.wrapper.curtain.CurtainLayoutWrapper.CurtainState.Stop
import miwu.annotation.Wrapper
import miwu.layout.CurtainLayout
import miwu.spec.MiotSpec.Property
import miwu.spec.MiotSpec.Service
import miwu.support.MiwuWidget
import miwu.android.databinding.MiotWidgetListButtonBinding as ButtonBinding

@Wrapper(CurtainLayout::class)
class CurtainLayoutWrapper(context: Context, widget: MiwuWidget<Int>) :
    MiwuLayoutWrapper<Int>(context, widget) {

    private val Open = valueListOf("Open")
    private val Close = valueListOf("Close")
    private val Pause = valueListOf("Pause")
    private val stateListeners = mutableSetOf<(state: CurtainState) -> Unit>()
    private var currentState: CurtainState = Stop
        set(value) {
            if (currentState == value) return
            field = value
            stateListeners.forEach { it.invoke(value) }
        }

    override fun initWrapper() {
        registerProperty(Service.Curtain, Property.Status) { property, value ->
            val name = property.valueListOf(value as Int).description
            val state = when (name) {
                "Opening" -> Opening
                "Closing" -> Closing
                else -> Stop
            }
            currentState = state
        }
        view(ButtonBinding::inflate) {
            title = Open.descriptionTranslation
            icon(AndroidIcons.CurtainOpen)
            onClick {
                if (currentState == Opening) {
                    updateValue(Pause)
                } else {
                    updateValue(Open)
                }
            }
            onStateChanged { state ->
                updateState(state == Opening)
            }
        }
        view(ButtonBinding::inflate) {
            title = Close.descriptionTranslation
            icon(AndroidIcons.CurtainClose)
            onClick {
                if (currentState == Closing) {
                    updateValue(Pause)
                } else {
                    updateValue(Close)
                }
            }
            onStateChanged { state ->
                updateState(state == Closing)
            }
        }
    }

    // 窗帘的属性由其他东西定义, 绑定的属性只能写不能读
    override fun onUpdateValue(value: Int) = Unit

    @WrapperFun
    private fun onStateChanged(block: (state: CurtainState) -> Unit) {
        stateListeners.add(block)
    }

    private sealed interface CurtainState {
        data object Opening : CurtainState
        data object Closing : CurtainState
        data object Stop : CurtainState
    }
}