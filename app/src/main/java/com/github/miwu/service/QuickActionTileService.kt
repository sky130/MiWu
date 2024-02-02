@file:Suppress("FunctionName")

package com.github.miwu.service

import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.DimensionBuilders.weight
import androidx.wear.protolayout.DimensionBuilders.wrap
import androidx.wear.protolayout.ModifiersBuilders.Clickable
import androidx.wear.protolayout.material.Typography
import com.github.miwu.miot.manager.MiotQuickManager
import com.github.miwu.miot.quick.MiotBaseQuick
import kndroidx.wear.tile.*
import kndroidx.wear.tile.layout.*
import com.github.miwu.R
import kndroidx.KndroidX
import kndroidx.wear.tile.widget.*
import kndroidx.wear.tile.service.TileServiceX

class QuickActionTileService : TileServiceX() {
    val list get() = MiotQuickManager.quickList
    override val version = "1"

    init {
        imageMap.apply {
            set("device", R.drawable.ic_miwu_placeholder.toImage())
            set("scene", R.drawable.ic_miwu_scene_tile.toImage())
        }
    }

    override fun onClick(id: String) {
        try {
            val position = id.toInt()
            MiotQuickManager.doQuick(position)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onEnter() = MiotQuickManager.refresh(must = true)

    override fun onLayout() = layout {
        Grid(
            width = expand(),
            height = wrap(),
            modifiers = Modifier.padding(horizontal = 25.dp),
            rowModifiers = Modifier.padding(vertical = 3.dp),
            spanCount = 2
        ) {
            for ((i, quick) in list.withIndex()) {
                val resId = when (quick) {
                    is MiotBaseQuick.DeviceQuick<*> -> "device"
                    is MiotBaseQuick.SceneQuick -> "scene"
                    else -> ""
                }
                QuickCard(
                    quick = quick, resId = resId, Clickable(i.toString())
                )
            }
        }
    }

    private fun Any.QuickCard(
        quick: MiotBaseQuick, resId: String, clickable: Clickable
    ) = Box(width = weight(1f), height = wrap(),modifier = Modifier.padding(horizontal = 3.dp)) {
        val background = quick.run {
            if (this is MiotBaseQuick.DeviceQuick<*> && value is Boolean && value != null && (value as Boolean)) {
                ShapeBackground(0xee57D1B8.color, 15.dp)
            } else {
                ShapeBackground(0xFF202020.color, 15.dp)
            }
        }
        Box(
            width = expand(),
            height = wrap(),
            modifier = Modifier.background(background)
                .clickable(clickable)
        ) {
            Column(
                width = weight(1f),
                height = wrap(),
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
            ) {
                Image(width = 25.dp, height = 25.dp, resId = resId)
                Spacer(width = 0.dp, height = 5.dp)
                Text(
                    text = quick.name,
                    textColors = 0xFFFFFFFF.color,
                    typography = Typography.TYPOGRAPHY_BUTTON
                )
            }
        }
    }

    companion object {

        fun update() {
            getUpdater(KndroidX.context).requestUpdate(QuickActionTileService::class.java)
        }

    }
}
