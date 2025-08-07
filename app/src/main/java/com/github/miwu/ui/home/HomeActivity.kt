package com.github.miwu.ui.home

import com.github.miwu.logic.setting.AppSetting
import kndroidx.activity.ViewActivityX
import miwu.miot.model.miot.MiotHomes
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.github.miwu.databinding.ActivityHomeBinding as Binding

class HomeActivity : ViewActivityX<Binding>(Binding::inflate) {
    override val viewModel: HomeViewModel by viewModel()

    fun onItemClick(item: Any?) {
        when (item) {
            is MiotHomes.Result.Home -> {
                AppSetting.homeId = item.id.toLong()
                finish()
            }
        }
    }

}