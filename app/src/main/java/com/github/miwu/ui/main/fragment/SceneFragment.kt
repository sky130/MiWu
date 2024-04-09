package com.github.miwu.ui.main.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.miwu.MainApplication.Companion.miot
import com.github.miwu.databinding.FragmentMainSceneBinding
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.miot.manager.MiotQuickManager
import com.github.miwu.miot.quick.MiotBaseQuick
import com.github.miwu.viewmodel.MainViewModel
import kndroidx.extension.log
import kndroidx.extension.toast
import kndroidx.fragment.ViewFragmentX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miot.kotlin.model.miot.MiotDevices
import miot.kotlin.model.miot.MiotScenes

class SceneFragment : ViewFragmentX<FragmentMainSceneBinding, MainViewModel>(),
    SwipeRefreshLayout.OnRefreshListener {
    val isEmpty = MutableLiveData(false)

    override fun init() {
        binding.swipe.setOnRefreshListener(this)
        AppRepository.sceneFlow.onEach {
            isEmpty.value = it.isEmpty()
        }.launchIn(lifecycleScope)
        AppRepository.sceneRefreshFlow.onEach {
            binding.swipe.isRefreshing = false
        }.launchIn(lifecycleScope)
    }

    fun onItemClick(item: Any?) {
        item as MiotScenes.Result.Scene
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            miot.runScene(item).execute()
        }
    }

    fun onItemLongClick(item: Any?): Boolean {
        item as MiotScenes.Result.Scene
        MiotQuickManager.addQuick(MiotBaseQuick.SceneQuick(item))
        return true
    }

    override fun onRefresh() {
        AppRepository.updateScene()
    }
}
