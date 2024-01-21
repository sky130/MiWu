package com.github.miwu.ui.about

import androidx.lifecycle.ViewModel
import com.github.miwu.databinding.ActivityAboutBinding
import com.github.miwu.ui.license.LicenseActivity
import com.github.miwu.viewmodel.AboutViewModel
import kndroidx.activity.ViewActivityX
import kndroidx.extension.start

class AboutActivity : ViewActivityX<ActivityAboutBinding, AboutViewModel>() {

    fun startLicenseActivity() {
        start<LicenseActivity>()
    }

}