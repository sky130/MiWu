package com.github.miwu.ui.about.crash

import com.github.miwu.databinding.ActivityCrashBinding  as Binding
import com.github.miwu.logic.setting.AppSetting
import kndroidx.activity.ViewActivityX
import org.koin.androidx.viewmodel.ext.android.viewModel

class CrashActivity : ViewActivityX<Binding>(Binding::inflate) {
    override val viewModel: CrashViewModel by viewModel()

    override fun onDestroy() {
        AppSetting.isCrash.value = false
        super.onDestroy()
    }

}