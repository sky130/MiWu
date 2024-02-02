package com.github.miwu.ui.main.fragment

import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.miwu.MainApplication.Companion.miot
import com.github.miwu.databinding.FragmentMainSceneBinding
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.miot.manager.MiotQuickManager
import com.github.miwu.miot.quick.MiotBaseQuick
import com.github.miwu.viewmodel.MainViewModel
import kndroidx.extension.toast
import kndroidx.fragment.ViewFragmentX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miot.kotlin.model.miot.MiotDevices
import miot.kotlin.model.miot.MiotScenes

class SceneFragment : ViewFragmentX<FragmentMainSceneBinding, MainViewModel>(),
    SwipeRefreshLayout.OnRefreshListener {

    override fun init() {
        binding.swipe.setOnRefreshListener(this)
        lifecycleScope.launch {
            AppRepository.sceneFlow.collectLatest {
                binding.swipe.isRefreshing = false
            }
        }
    }

    fun getRoomName(item: Any?) = AppRepository.getRoomName(item as MiotDevices.Result.Device)

    fun onItemClick(item: Any?) {
        item as MiotScenes.Result.Scene
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            miot.runScene(item)
        }
    }

    fun onItemLongClick(item: Any?): Boolean {
        item as MiotScenes.Result.Scene
        MiotQuickManager.addQuick(MiotBaseQuick.SceneQuick(item))
        return true
    }

    override fun onRefresh() {
        AppRepository.updateScene()
        binding.swipe.isRefreshing = false
    }
}