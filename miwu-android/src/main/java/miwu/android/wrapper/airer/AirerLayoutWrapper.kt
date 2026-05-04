@file:Suppress("PrivatePropertyName")

package miwu.android.wrapper.airer

import android.content.Context
import miwu.android.icon.generated.icon.AndroidIcons
import miwu.android.view.binding.icon
import miwu.android.view.binding.onClick
import miwu.android.view.binding.title
import miwu.android.view.binding.updateState
import miwu.android.wrapper.base.MiwuLayoutWrapper
import miwu.annotation.Wrapper
import miwu.layout.AirerLayout
import miwu.miot.model.spec.SpecAtt
import miwu.spec.MiotSpec.Property
import miwu.spec.MiotSpec.Service
import miwu.support.MiwuWidget
import miwu.android.databinding.MiotWidgetListButtonBinding as ButtonBinding

@Wrapper(AirerLayout::class)
class AirerLayoutWrapper(context: Context, widget: MiwuWidget<Int>) :
    MiwuLayoutWrapper<Int>(context, widget) {

    private val Pause = valueListOf("Pause")
    private val Up = valueListOf("Up")
    private val Down = valueListOf("Down")
    private val stateListeners = mutableSetOf<(state: AirerState) -> Unit>()
    private var currentState: AirerState = AirerState.Pause
        set(value) {
            if (field == value) return
            field = value
            stateListeners.forEach { it.invoke(value) }
        }

    override fun initWrapper() {
        registerProperty(Service.Airer, Property.Status) { property, value ->
            val name = property.valueListOf(value as Int).description
            val state = AirerState.from(name, value)
            currentState = state
        }
        view(ButtonBinding::inflate) {
            val viewState = AirerState.Rising
            title = Up.descriptionTranslation
            icon(AndroidIcons.PointUp)
            onClick {
                if (currentState == viewState) {
                    updateValue(Pause)
                } else {
                    updateValue(Up)
                }
            }
            onStateChanged { state ->
                updateState(state == viewState)
            }
        }
        view(ButtonBinding::inflate) {
            val viewState = AirerState.Down
            title = Down.descriptionTranslation
            icon(AndroidIcons.PointDown)
            onClick {
                if (currentState == viewState) {
                    updateValue(Pause)
                } else {
                    updateValue(Down)
                }
            }
            onStateChanged { state ->
                updateState(state == viewState)
            }
        }
    }

    override fun onUpdateValue(value: Int) = Unit

    @WrapperFun
    private fun onStateChanged(block: (state: AirerState) -> Unit) {
        stateListeners.add(block)
    }

    private sealed interface AirerState {
        data object UpStop : AirerState // 上限位停止
        data object Rising : AirerState
        data object Down : AirerState
        data object Pause : AirerState
        data object DownStop : AirerState // 下限位停止

        companion object {
            fun from(name: String, value: Int) = when (name) {
                "Stop" -> {
                    if (value == 0) UpStop
                    else DownStop
                }

                "Rising", "Up" -> Rising
                "Down" -> Down
                "Pause" -> Pause
                else -> DownStop
            }
        }
    }
}