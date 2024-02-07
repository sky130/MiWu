package com.github.miwu.ui.about

import androidx.lifecycle.ViewModel
import com.github.miwu.databinding.ActivityAboutBinding
import com.github.miwu.ui.crash.CrashActivity
import com.github.miwu.ui.help.HelpActivity
import com.github.miwu.ui.license.LicenseActivity
import com.github.miwu.ui.smart.SmartActivity
import com.github.miwu.viewmodel.AboutViewModel
import kndroidx.activity.ViewActivityX
import kndroidx.extension.start

class AboutActivity : ViewActivityX<ActivityAboutBinding, AboutViewModel>() {

    override fun onResume() {
        super.onResume()
        binding.scroll.requestFocus()
    }

    fun startLicenseActivity() {
        start<LicenseActivity>()
    }

    fun startCrashActivity() {
        start<CrashActivity>()
    }

    fun startHelpActivity() {
        start<HelpActivity>()
    }

}