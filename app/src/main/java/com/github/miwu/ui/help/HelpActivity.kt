package com.github.miwu.ui.help

import com.github.miwu.databinding.ActivityHelpBinding as Binding
import kndroidx.activity.ViewActivityX
import org.koin.androidx.viewmodel.ext.android.viewModel

class HelpActivity : ViewActivityX<Binding>(Binding::inflate) {
    override val viewModel: HelpViewModel by viewModel()
}