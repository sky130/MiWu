package com.github.miwu.ui.about

import com.github.miwu.databinding.ActivityAboutBinding as Binding
import com.github.miwu.ui.crash.CrashActivity
import com.github.miwu.ui.help.HelpActivity
import com.github.miwu.ui.license.LicenseActivity
import kndroidx.activity.ViewActivityX
import kndroidx.extension.start
import org.koin.androidx.viewmodel.ext.android.viewModel

class AboutActivity : ViewActivityX<Binding>(Binding::inflate) {
    override val viewModel: AboutViewModel by viewModel()

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