package com.github.miwu.ui.smart

import androidx.lifecycle.lifecycleScope
import com.github.miwu.databinding.ActivitySmartBinding
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.ui.smart.adapter.SmartAdapter
import com.github.miwu.viewmodel.SmartViewModel
import kndroidx.activity.ViewActivityX
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SmartActivity : ViewActivityX<ActivitySmartBinding, SmartViewModel>() {
    private val adapter by lazy { SmartAdapter() }

    override fun init() {
        AppRepository.loadSmart()
        binding.recycler.adapter = adapter
        viewModel.list.onEach {
            adapter.updateList(it)
        }.launchIn(lifecycleScope)
    }

}