package com.github.miwu.service

import android.content.Context
import androidx.wear.tiles.tooling.preview.Preview
import androidx.wear.tiles.tooling.preview.TilePreviewData
import androidx.wear.tiles.tooling.preview.TilePreviewHelper
import androidx.wear.tooling.preview.devices.WearDevices
import kndroidx.extension.toast
import kndroidx.kndroidxConfig
import kndroidx.wear.tile.Modifier
import kndroidx.wear.tile.alignment.HorizontalAlignment
import kndroidx.wear.tile.alignment.VerticalAlignment
import kndroidx.wear.tile.dp
import kndroidx.wear.tile.fillMaxSize
import kndroidx.wear.tile.id
import kndroidx.wear.tile.layout
import kndroidx.wear.tile.layout.Arc
import kndroidx.wear.tile.layout.Box
import kndroidx.wear.tile.layout.Column
import kndroidx.wear.tile.layout.Grid
import kndroidx.wear.tile.service.TileServiceX
import kndroidx.wear.tile.widget.Text

class DeviceTileService : TileServiceX() {

    override val version = "1"

    override fun onClick(id: String) = autoHandle(id)

    override fun onResourcesRequest() {}

    override fun onLayout() = layout {
        Box(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = HorizontalAlignment.Center,
            verticalAlignment = VerticalAlignment.Center
        ) {
            Text(
                text = "hello tile",
                modifier = Modifier
                    .id("text")
                    .onClick {
                        "you click it!".toast()
                    }
            )
        }
    }
}