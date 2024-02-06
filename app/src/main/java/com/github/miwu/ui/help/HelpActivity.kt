package com.github.miwu.ui.help

import com.github.miwu.databinding.ActivityHelpBinding
import com.github.miwu.viewmodel.HelpViewModel
import kndroidx.activity.ViewActivityX

class HelpActivity : ViewActivityX<ActivityHelpBinding, HelpViewModel>() {
    override fun init() {
        viewModel.load()
    }

    override fun onResume() {
        super.onResume()
        binding.recycler.requestFocus()
    }

}