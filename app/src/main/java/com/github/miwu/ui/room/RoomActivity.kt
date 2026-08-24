package com.github.miwu.ui.room

import android.content.Context
import com.github.miwu.ui.device.DeviceActivity.Companion.startDeviceActivity
import com.github.miwu.utils.Logger
import kndroidx.activity.ViewActivityX
import kndroidx.extension.extra
import kndroidx.extension.start
import kndroidx.extension.toast
import miwu.miot.model.miot.MiotDevice
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import com.github.miwu.databinding.ActivityRoomBinding as Binding

class RoomActivity : ViewActivityX<Binding>(Binding::inflate) {
    private val logger = Logger()
    override val viewModel: RoomViewModel by viewModel(parameters = { parametersOf(room) })
    val room by extra<String>("room")

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
