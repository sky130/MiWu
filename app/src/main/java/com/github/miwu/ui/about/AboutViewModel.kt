package com.github.miwu.ui.about

import android.app.Application
import androidx.lifecycle.ViewModel
import org.koin.core.annotation.Provided

class AboutViewModel(
    @Provided private val application: Application,
) : ViewModel() {
    val versionName = application.packageManager.getPackageInfo(application.packageName, 0).versionName
}
