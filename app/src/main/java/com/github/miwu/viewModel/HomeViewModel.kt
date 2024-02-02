package com.github.miwu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.miwu.R
import com.github.miwu.logic.preferences.AppPreferences
import com.github.miwu.logic.repository.AppRepository
import kndroidx.extension.log
import kndroidx.extension.string
import miot.kotlin.model.miot.MiotHomes

class HomeViewModel : ViewModel() {
    val homeList get() = AppRepository.homeFlow.asLiveData()

    fun isHome(item: MiotHomes.Result.Home) = item.id.toLong() == AppPreferences.homeId

    fun getDesc(item: MiotHomes.Result.Home): String {
        return if (item.shareFlag == 0) { // 本地家庭
            var deviceSize = item.dids.size
            for (i in item.rooms) {
                deviceSize += i.dids.size
            }
            R.string.home_desc.string.format(item.rooms.size, deviceSize)
        } else { // 共享家庭
            R.string.home_desc_share.string
        }.apply {
            log.d()
        }
    }
}