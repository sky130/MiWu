package com.github.miwu.ui.home

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kndroidx.activity.ViewActivityX
import kotlinx.coroutines.launch
import miwu.miot.model.miot.MiotHome
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.github.miwu.databinding.ActivityHomeBinding as Binding

class HomeActivity : ViewActivityX<Binding>(Binding::inflate) {
    override val viewModel: HomeViewModel by viewModel()

    override fun init() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { pendingEvent ->
                    pendingEvent?.let {
                        if (it is HomeViewModel.Event.HomeSelected) finish()
                        viewModel.consumeEvent(it)
                    }
                }
            }
        }
    }

    fun onItemClick(item: Any?) {
        when (item) {
            is MiotHome -> {
                viewModel.setHome(item)
            }
        }
    }

}
