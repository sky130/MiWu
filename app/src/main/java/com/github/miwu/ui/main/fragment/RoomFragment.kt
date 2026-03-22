package com.github.miwu.ui.main.fragment

import com.github.miwu.ui.main.MainViewModel
import com.github.miwu.ui.room.RoomActivity.Companion.startRoomActivity
import com.github.miwu.utils.Logger
import kndroidx.fragment.ViewFragmentX
import miwu.miot.model.miot.MiotRoom
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.github.miwu.databinding.FragmentMainRoomBinding as Binding

class RoomFragment : ViewFragmentX<Binding>(Binding::inflate) {
    override val viewModel: MainViewModel by viewModel()
    val logger = Logger()

    fun onItemClick(item: Any?) {
        item as MiotRoom
        requireContext().startRoomActivity(item.name)
    }
}