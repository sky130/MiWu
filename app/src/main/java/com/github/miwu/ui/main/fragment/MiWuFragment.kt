package com.github.miwu.ui.main.fragment

import android.annotation.SuppressLint
import androidx.lifecycle.lifecycleScope
import com.github.miwu.ui.device.DeviceActivity.Companion.startDeviceActivity
import com.github.miwu.ui.edit.EditFavoriteActivity
import com.github.miwu.ui.main.MainViewModel
import com.github.miwu.utils.Logger
import kndroidx.extension.start
import kndroidx.fragment.ViewFragmentX
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import miwu.miot.model.miot.MiotDevice
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.github.miwu.databinding.FragmentMainMiwuBinding as Binding

class MiWuFragment : ViewFragmentX<Binding>(Binding::inflate) {
    override val viewModel: MainViewModel by viewModel()
    val logger = Logger()

    @SuppressLint("NotifyDataSetChanged")
    override fun init() {
        viewModel.home
            .onEach { binding.recycler.adapter?.notifyDataSetChanged() }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    fun onItemClick(item: Any?) {
        if (item !is MiotDevice || !item.isOnline) return
        requireContext().startDeviceActivity(item)
    }

    fun onItemLongClick(item: Any?) {
        requireContext().start<EditFavoriteActivity>()
    }

}
