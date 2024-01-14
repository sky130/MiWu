package com.github.miwu.service

import android.util.ArrayMap
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.DimensionBuilders.wrap
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.*
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.material.Typography
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.github.miwu.R
import com.github.miwu.miot.MiotQuickManager
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kndroidx.KndroidX
import kndroidx.extension.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val RESOURCES_VERSION = "1"

class QuickActionTileService : KtxTileService() {

    private val job = Job()
    private val scopes = CoroutineScope(job)

    override val version = "4"

    val list get() = MiotQuickManager.quickList

    override val resMap = super.resMap.apply {
        put("test_res", R.drawable.mi_icon_small)
    }

    override fun onLayout() = Box(width = expand(), height = expand()) {
        setVerticalAlignment(Vertical(VERTICAL_ALIGN_CENTER))
        setHorizontalAlignment(Horizontal(HORIZONTAL_ALIGN_CENTER))
        contents(
            Grid(expand(), wrap(), spanCount = 2, rowPadding = PaddingValue(5.dp)) {
                setVerticalAlignment(Vertical(VERTICAL_ALIGN_CENTER))
                setHorizontalAlignment(Horizontal(HORIZONTAL_ALIGN_CENTER))
                for ((i, quick) in list.withIndex()) {
                    contents(
                        Box(wrap(), wrap(), padding = PaddingValue(horizontal = 2.dp)) {
                            contents(
                                Chip(
                                    width = wrap(),
                                    clickable = Clickable(i.toString()),
                                    title = quick.name,
                                    icon = "test_res",
                                )
                            )
                        }
                    )
                }
            }
        )
    }


    override fun onClick(id: String) {
        try {
            val position = id.toInt()
            MiotQuickManager.doQuick(position)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {

        fun update() {
            getUpdater(KndroidX.context).requestUpdate(QuickActionTileService::class.java)
        }

    }
}