package com.github.miwu.service

import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.DimensionBuilders.wrap
import androidx.wear.protolayout.LayoutElementBuilders.*
import com.github.miwu.R
import com.github.miwu.miot.manager.MiotQuickManager
import kndroidx.KndroidX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

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