package com.github.miwu.ui.about.license

import com.github.miwu.databinding.ActivityLicenseBinding as Binding
import kndroidx.activity.ViewActivityX
import org.koin.androidx.viewmodel.ext.android.viewModel

class LicenseActivity : ViewActivityX<Binding>(Binding::inflate) {
    override val viewModel: LicenseViewModel by viewModel()
}
