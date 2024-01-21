package com.github.miwu.service

import android.annotation.SuppressLint
import android.util.ArrayMap
import androidx.annotation.DrawableRes
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.tiles.EventBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

abstract class KtxTileService : TileService() {

    abstract fun onLayout(): LayoutElementBuilders.LayoutElement

    abstract val version: String

    abstract fun onClick(id: String)

    abstract fun onAttachedToWindow()

    open val imageMap = ArrayMap<String, Image>()

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> =
        Futures.immediateFuture(
            TileBuilders.Tile.Builder()
                .apply {
                    if (requestParams.currentState.lastClickableId.isNotEmpty())
                        onClick(requestParams.currentState.lastClickableId)
                }
                .setResourcesVersion(version)
                .setTileTimeline(
                    TimelineBuilders.Timeline.fromLayoutElement(
                        onLayout()
                    )
                ).build()
        )

    override fun onTileEnterEvent(requestParams: EventBuilders.TileEnterEvent) {
        onAttachedToWindow()
    }

    override fun onTileResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<ResourceBuilders.Resources> =
        Futures.immediateFuture(
            ResourceBuilders.Resources.Builder().setVersion(version)
                .apply {
                    for ((id, res) in imageMap) {
                        when (res) {
                            is ResImage -> {
                                addImage(id, res.resId)
                            }

                            is ByteImage -> {
                                addImage(id, res.byte)
                            }
                        }
                    }

                }.build()
        )

    fun ResourceBuilders.Resources.Builder.addImage(itStr: String, id: Int) =
        this.addIdToImageMapping(
            itStr, ResourceBuilders.ImageResource.Builder().setAndroidResourceByResId(
                ResourceBuilders.AndroidImageResourceByResId.Builder().setResourceId(id).build()
            ).build()
        )

    fun ResourceBuilders.Resources.Builder.addImage(
        itStr: String,
        byte: ByteArray,
        widthPx: Int = 48,
        heightPx: Int = 48
    ) =
        this.addIdToImageMapping(
            itStr, ResourceBuilders.ImageResource.Builder()
                .setInlineResource(
                    ResourceBuilders.InlineImageResource.Builder()
                        .setData(byte)
                        .setWidthPx(widthPx)
                        .setHeightPx(heightPx)
                        .setFormat(ResourceBuilders.IMAGE_FORMAT_RGB_565)
                        .build()
                ).build()
        ).build()

    sealed class Image()

    class ResImage(@DrawableRes val resId: Int) : Image()

    class ByteImage(val byte: ByteArray) : Image()

    @SuppressLint("SupportAnnotationUsage")
    @DrawableRes
    fun Int.toImage() = ResImage(this)

    fun ByteArray.toImage() = ByteImage(this)
}