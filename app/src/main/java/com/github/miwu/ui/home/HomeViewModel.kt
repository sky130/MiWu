package com.github.miwu.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.miwu.R
import com.github.miwu.logic.repository.MiotRepository
import com.github.miwu.logic.setting.AppSetting
import kndroidx.extension.log
import kndroidx.extension.string
import kotlinx.coroutines.flow.map
import miwu.miot.model.miot.MiotHome

class HomeViewModel(val miotRepository: MiotRepository) : ViewModel() {

    val homeList get() = miotRepository.homes
        .map { it.getOrNull() ?: emptyList() }
        .asLiveData()

    fun isCurrentHome(item: MiotHome) = item.id.toLong() == AppSetting.homeId.value

    fun getDesc(item: MiotHome): String {
        return if (!item.isShareHome) {
            var deviceSize = item.dids.size
            for (i in item.rooms) {
                deviceSize += i.dids.size
            }
            R.string.home_desc.string.format(item.rooms.size, deviceSize)
        } else {
            R.string.home_desc_share.string
        }
    }

    fun setHome(item: MiotHome){
        miotRepository.setActiveHome(item)
    }
}
