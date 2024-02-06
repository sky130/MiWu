package com.github.miwu.ui.crash

import com.github.miwu.databinding.ActivityCrashBinding
import com.github.miwu.logic.preferences.AppPreferences
import com.github.miwu.ui.crash.model.CrashItem
import com.github.miwu.viewmodel.CrashViewModel
import kndroidx.activity.ViewActivityX
import java.text.FieldPosition

class CrashActivity : ViewActivityX<ActivityCrashBinding, CrashViewModel>() {

    override fun init() {
        viewModel.load()
    }

    override fun onDestroy() {
        AppPreferences.isCrash = false
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        binding.recycler.requestFocus()
    }

}