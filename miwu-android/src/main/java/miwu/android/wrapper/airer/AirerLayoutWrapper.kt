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


    override fun initWrapper() {
        registerProperty(Service.Airer, Property.Status) { property, value ->
            val name = property.valueList!!.first { it.value == value }.description
            val state = when (name) {
                "Stop" -> {
                    if (value == 0) AirerState.UpStop
                    else AirerState.DownStop
                }

                "Rising", "Up" -> AirerState.Rising
                "Down" -> AirerState.Down
                "Pause" -> AirerState.Pause
                else -> AirerState.DownStop
            }
            if (currentState == state) return@registerProperty
            currentState = state
            stateListeners.forEach { it.invoke(state) }
        }

        // 上升
        view(ButtonBinding::inflate) {
            val viewState = AirerState.Rising
            title = Up.descriptionTranslation
            icon(AndroidIcons.PointUp)
            onClick {
                if (currentState == viewState) {
                    update(Pause)
                } else {
                    update(Up)
                }
            }
            onStateChanged { state ->
                updateState(state == viewState)
            }
        }
        // 下降
        view(ButtonBinding::inflate) {
            val viewState = AirerState.Down
            title = Down.descriptionTranslation
            icon(AndroidIcons.PointDown)
            onClick {
                if (currentState == viewState) {
                    update(Pause)
                } else {
                    update(Down)
                }
            }
            onStateChanged { state ->
                updateState(state == viewState)
            }
        }
    }

    override fun onUpdateValue(value: Int) = Unit

    private fun update(value: SpecAtt.Property.Value) {
        update(value.value)
    }

    private fun onStateChanged(block: (state: AirerState) -> Unit) {
        stateListeners.add(block)
    }

    private sealed interface AirerState {
        data object UpStop : AirerState // Stop 上限位停止
        data object Rising : AirerState
        data object Down : AirerState
        data object Pause : AirerState
        data object DownStop : AirerState // 下限位停止
    }
}