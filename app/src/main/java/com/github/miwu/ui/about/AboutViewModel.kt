package com.github.miwu.ui.about

import androidx.lifecycle.ViewModel
import kndroidx.KndroidX.context

class AboutViewModel : ViewModel() {
    val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
}