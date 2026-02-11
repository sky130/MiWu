@file:Suppress("FunctionName")

package com.github.miwu.service

import android.content.Context
import androidx.wear.protolayout.material.Typography.TYPOGRAPHY_BUTTON
import androidx.wear.tiles.EventBuilders
import androidx.wear.tiles.tooling.preview.Preview
import androidx.wear.tiles.tooling.preview.TilePreviewData
import androidx.wear.tiles.tooling.preview.TilePreviewHelper
import androidx.wear.tooling.preview.devices.WearDevices
import com.github.miwu.R
import com.github.miwu.appModule
import com.github.miwu.logic.database.entity.FavoriteDevice.Companion.toMiot
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.repository.LocalRepository
import com.github.miwu.utils.Logger
import kndroidx.KndroidConfig
import kndroidx.KndroidX
import kndroidx.wear.tile.Clickable
import kndroidx.wear.tile.Modifier
import kndroidx.wear.tile.ShapeBackground
import kndroidx.wear.tile.alignment.HorizontalAlignment
import kndroidx.wear.tile.alignment.VerticalAlignment
import kndroidx.wear.tile.background
import kndroidx.wear.tile.boolean
import kndroidx.wear.tile.clickable
import kndroidx.wear.tile.color
import kndroidx.wear.tile.dp
import kndroidx.wear.tile.fillMaxSize
import kndroidx.wear.tile.fillMaxWidth
import kndroidx.wear.tile.height
import kndroidx.wear.tile.layout
import kndroidx.wear.tile.layout.Box
import kndroidx.wear.tile.layout.Column
import kndroidx.wear.tile.layout.Grid
import kndroidx.wear.tile.padding
import kndroidx.wear.tile.service.LayoutTileService
import kndroidx.wear.tile.string
import kndroidx.wear.tile.weight
import kndroidx.wear.tile.widget.Image
import kndroidx.wear.tile.widget.Spacer
import kndroidx.wear.tile.widget.Text
import kndroidx.wear.tile.width
import kndroidx.wear.tile.wrapContentHeight
import kndroidx.wear.tile.wrapContentSize
import miwu.miot.model.miot.MiotDevice
import miwu.miot.utils.json
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DeviceTileService : LayoutTileService() {
    private val logger = Logger()
    val localRepository: LocalRepository by inject()
    val appRepository: AppRepository by inject()

    override val version get() = (localRepository.iconMap.hashCode() + resVersion).toString()
    private val resVersion = 1

    init {
        onResourcesRequest()
    }

    override fun onClick(id: String) = invokeById(id)

    override fun onTileAddEvent(requestParams: EventBuilders.TileAddEvent) {
        super.onTileAddEvent(requestParams)
    }

    override fun onResourcesRequest() {
        ResImage("scene", R.drawable.ic_miwu_scene_tile)
        for ((model, icon) in localRepository.iconMap) {
            logger.info("res version {}", version)
            ByteImage(model, icon)
        }
    }

    override fun onLayout() = layout {
        Box(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = HorizontalAlignment.Center,
            verticalAlignment = VerticalAlignment.Center
        ) {
            if (localRepository.deviceList.isEmpty() || appRepository.miotUser == null) {
                Text(text = "hello tile")
            } else {
                Page(localRepository.deviceList.map { it.toMiot() })
            }
        }
    }

    fun Any.Page(list: List<MiotDevice>) =
        Grid(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 25.dp),
            rowModifier = Modifier.padding(vertical = 3.dp),
            spanCount = 2
        ) {
            for ((i, device) in list.withIndex()) {
                if (i >= 4) break
                CardItem(device)
            }
        }

    fun Any.CardItem(device: MiotDevice) =
        Box(
            modifier = Modifier
                .wrapContentSize()
                .weight(1f, null)
                .padding(horizontal = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .fillMaxWidth()
                    .background(ShapeBackground(0xFF202020.color, 15.dp))
                    .clickable(
                        Clickable(
                            device.did,
                            packageName = packageName,
                            className = "com.github.miwu.ui.device.DeviceActivity"
                        ) {
                            boolean("isFromTile", true)
                            string("device", json.encodeToString(device))
                            string("user", json.encodeToString(appRepository.miotUser))
                        }
                    )
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(weight = 1f)
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                ) {
                    Image(
                        resId = device.model,
                        modifier = Modifier
                            .width(25.dp)
                            .height(25.dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp).width(0.dp))
                    Text(
                        text = device.name,
                        typography = TYPOGRAPHY_BUTTON
                    ) {
                        setColor(0xFFFFFFFF.color)
                    }
                }
            }
        }

    companion object {

        fun refresh() {
            getUpdater(KndroidX.context).requestUpdate(DeviceTileService::class.java)
        }

    }


    @Preview(device = WearDevices.LARGE_ROUND)
    fun tilePreview(context: Context) = TilePreviewData { request ->
        KndroidConfig.context = context
        startKoin {
            androidLogger()
            androidContext(context)
            modules(appModule)
        }
        onLayout()
            .let(TilePreviewHelper::singleTimelineEntryTileBuilder)
            .build()
    }
}