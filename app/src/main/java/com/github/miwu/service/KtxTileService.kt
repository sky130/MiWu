package com.github.miwu.service

import android.util.ArrayMap
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

abstract class KtxTileService : TileService() {

    abstract fun onLayout(): LayoutElementBuilders.LayoutElement

    abstract val version: String

    abstract fun onClick(id: String)

    open val resMap = ArrayMap<String, Int>()

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> =
        Futures.immediateFuture(
            TileBuilders.Tile.Builder()
                .setResourcesVersion(version)
                .setTileTimeline(
                    TimelineBuilders.Timeline.fromLayoutElement(
                        onLayout()
                    )
                ).build()
        ).apply {
            if (requestParams.currentState.lastClickableId.isNotEmpty())
                onClick(requestParams.currentState.lastClickableId)
        }

    override fun onTileResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<ResourceBuilders.Resources> =
        Futures.immediateFuture(
            ResourceBuilders.Resources.Builder().setVersion(version)
                .apply {
                    for ((id, res) in resMap) {
                        addImageById(id, res)
                    }
                }.build()
        )

    private fun ResourceBuilders.Resources.Builder.addImageById(itStr: String, id: Int) =
        this.addIdToImageMapping(
            itStr, ResourceBuilders.ImageResource.Builder().setAndroidResourceByResId(
                ResourceBuilders.AndroidImageResourceByResId.Builder().setResourceId(id).build()
            ).build()
        )

}