package com.github.miwu.service

import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.DimensionBuilders.weight
import androidx.wear.protolayout.DimensionBuilders.wrap
import androidx.wear.protolayout.LayoutElementBuilders.*
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ModifiersBuilders.Clickable
import androidx.wear.protolayout.material.Typography
import com.github.miwu.R
import com.github.miwu.miot.manager.MiotQuickManager
import com.github.miwu.miot.quick.MiotBaseQuick
import kndroidx.KndroidX
import kndroidx.extension.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

private const val RESOURCES_VERSION = "1"

@Suppress("SameParameterValue")
class QuickActionTileService : KtxTileService() {

    private val job = Job()
    private val scopes = CoroutineScope(job)

    override val version = "4"

    val list get() = MiotQuickManager.quickList

    init {
        imageMap.apply {
            set("test_res", R.drawable.mi_icon_small.toImage())
        }
    }

    override fun onLayout() =
        Box(width = expand(), height = expand(), padding = PaddingValue(horizontal = 25.dp)) {
            setVerticalAlignment(Vertical(VERTICAL_ALIGN_CENTER))
            setHorizontalAlignment(Horizontal(HORIZONTAL_ALIGN_CENTER))
            contents(Grid(
                expand(), wrap(), spanCount = 2, rowPadding = PaddingValue(vertical = 3.dp)
            ) {
                for ((i, quick) in list.withIndex()) {
                    contents(
                        QuickCard(
                            quick = quick, iconId = "test_res", Clickable(i.toString())
                        )
                    )
                }
            })
        }

    @Suppress("FunctionName")
    private fun QuickCard(quick: MiotBaseQuick, iconId: String, clickable: Clickable? = null) =
        Box(weight(1f), wrap(), padding = PaddingValue(horizontal = 3.dp)) {
            contents(Box(expand(), wrap()) {
                setModifiers(Modifiers {
                    quick.apply {
                        if (this is MiotBaseQuick.DeviceQuick<*> &&
                            value is Boolean &&
                            value != null &&
                            (value as Boolean)
                        ) {
                            setBackground(Background(argb(0xee57D1B8.toInt()), 15.dp))
                        } else {
                            setBackground(Background(argb(0xFF202020.toInt()), 15.dp))
                        }
                    }


                    clickable?.let { setClickable(it) }
                })
                contents(Column(
                    width = weight(1f), height = wrap(), padding = PaddingValue(10.dp)
                ) {
                    contents(
                        Image(width = 25.dp, height = 25.dp, resId = iconId),
                        Spacer(width = 0.dp, height = 5.dp),
                        Text(
                            text = quick.name,
                            textColors = argb(0xFFFFFFFF.toInt()),
                            typography = Typography.TYPOGRAPHY_BUTTON
                        )
                    )
                })
            })
        }


    override fun onClick(id: String) {
        try {
            val position = id.toInt()
            MiotQuickManager.doQuick(position)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onAttachedToWindow() {
        MiotQuickManager.refresh(must = true)
    }

    companion object {

        fun update() {
            getUpdater(KndroidX.context).requestUpdate(QuickActionTileService::class.java)
        }

    }
}