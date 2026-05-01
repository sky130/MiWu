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
import miwu.miot.model.att.SpecAtt
import miwu.layout.CurtainLayout
import miwu.spec.MiotSpec.Property
import miwu.spec.MiotSpec.Service
import miwu.support.MiwuWidget
import miwu.android.databinding.MiotWidgetListButtonBinding as ButtonBinding

@Wrapper(CurtainLayout::class)
class CurtainLayoutWrapper(context: Context, widget: MiwuWidget<Int>) :
    MiwuLayoutWrapper<Int>(context, widget) {

    private val Open get() = valueList[0]
    private val Close get() = valueList[1]
    private val Pause get() = valueList[2]
    private val stateListeners = mutableSetOf<(state: CurtainState) -> Unit>()
    private var currentState: CurtainState = Stop


    override fun initWrapper() {
        registerProperty(Service.Curtain, Property.Status) { property, value ->
            val name = property.valueList!!.first { it.value == value }.description
            val state = when (name) {
                "Opening" -> Opening
                "Closing" -> Closing
                else -> Stop
            }
            if (currentState == state) return@registerProperty
            currentState = state
            stateListeners.forEach { it.invoke(state) }
        }

        // 开窗
        view(ButtonBinding::inflate) {
            title = Open.descriptionTranslation
            icon(AndroidIcons.CurtainOpen)
            onClick {
                if (currentState == Opening) {
                    update(Pause)
                } else {
                    update(Open)
                }
            }
            onStateChanged { state ->
                updateState(state == Opening)
            }
        }
        // 关窗
        view(ButtonBinding::inflate) {
            title = Close.descriptionTranslation
            icon(AndroidIcons.CurtainClose)
            onClick {
                if (currentState == Closing) {
                    update(Pause)
                } else {
                    update(Close)
                }
            }
            onStateChanged { state ->
                updateState(state == Closing)
            }
        }
    }

    // 窗帘的属性由其他东西定义, 绑定的属性只能写不能读
    override fun onUpdateValue(value: Int) = Unit

    private fun update(value: SpecAtt.Service.Property.Value) {
        update(value.value)
    }

    private fun onStateChanged(block: (state: CurtainState) -> Unit) {
        stateListeners.add(block)
    }

    private sealed interface CurtainState {
        data object Opening : CurtainState
        data object Closing : CurtainState
        data object Stop : CurtainState
    }
}