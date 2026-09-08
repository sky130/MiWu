package com.github.miwu.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.miwu.R
import com.github.miwu.domain.repository.HomeRepository
import com.github.miwu.domain.repository.SettingsRepository
import kndroidx.extension.string
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import miwu.miot.model.miot.MiotHome
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel(
    private val homeRepository: HomeRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val mutableEvent = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = mutableEvent.asStateFlow()

    val homeList = homeRepository.homes
        .map { it.getOrNull() ?: emptyList() }
        .asLiveData()

    fun isCurrentHome(item: MiotHome) = item.id.toLong() == settingsRepository.selectedHomeId

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

    fun setHome(item: MiotHome) {
        viewModelScope.launch {
            if (homeRepository.selectHome(item).isSuccess) {
                mutableEvent.value = Event.HomeSelected
            }
        }
    }

    fun consumeEvent(event: Event) {
        mutableEvent.compareAndSet(event, null)
    }

    sealed interface Event {
        data object HomeSelected : Event
    }
}
