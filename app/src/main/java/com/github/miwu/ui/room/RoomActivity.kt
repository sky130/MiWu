package com.github.miwu.ui.room

import android.content.Context
import com.github.miwu.ui.device.DeviceActivity.Companion.startDeviceActivity
import com.github.miwu.utils.Logger
import kndroidx.activity.ViewActivityX
import kndroidx.extension.start
import miwu.miot.model.miot.MiotDevice
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.github.miwu.databinding.ActivityRoomBinding as Binding

class RoomActivity : ViewActivityX<Binding>(Binding::inflate) {
    private val logger = Logger()
    override val viewModel: RoomViewModel by viewModel()

    fun onItemClick(item: Any?) {
        if (item !is MiotDevice || !item.isOnline) return
        startDeviceActivity(item)
    }

    companion object {
        fun Context.startRoomActivity(room: String) {
            start<RoomActivity> {
                putExtra("room", room)
            }
        }
    }
}
