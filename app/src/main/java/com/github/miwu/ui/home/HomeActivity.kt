package com.github.miwu.ui.home

import com.github.miwu.databinding.ActivityHomeBinding
import com.github.miwu.logic.preferences.AppPreferences
import com.github.miwu.viewmodel.HomeViewModel
import kndroidx.activity.ViewActivityX
import miot.kotlin.model.miot.MiotHomes

class HomeActivity : ViewActivityX<ActivityHomeBinding, HomeViewModel>() {

    fun onItemClick(item: Any?) {
        item as MiotHomes.Result.Home
        AppPreferences.apply {
            homeId = item.id.toLong()
            homeUid = item.uid
        }
        finish()
    }

}