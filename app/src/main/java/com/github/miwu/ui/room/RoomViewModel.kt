package com.github.miwu.ui.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.miwu.logic.datastore.MiotUserDataStore
import com.github.miwu.logic.repository.CacheRepository
import com.github.miwu.logic.repository.LocalRepository
import com.github.miwu.logic.repository.MiotRepository
import com.github.miwu.ui.main.state.FragmentState.Empty
import com.github.miwu.ui.main.state.FragmentState.Error
import com.github.miwu.ui.main.state.FragmentState.Loading
import com.github.miwu.ui.main.state.FragmentState.Normal
import com.github.miwu.utils.Logger
import fr.haan.resultat.fold
import kotlinx.coroutines.flow.map
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotHomes


class RoomViewModel(
    val miotRepository: MiotRepository,
    val cacheRepository: CacheRepository,
    val room: String,
) : ViewModel() {
    private val logger = Logger()
    val info get() = miotRepository.user
    val home = miotRepository.currentHome
    val devices = home.map { it.getOrNull()?.rooms[room].orEmpty() }
        .map { device ->
            device.sortedWith(
                compareBy(
                    { !it.isOnline },
                    { cacheRepository.getRoom(it.did) },
                    { it.name.lowercase() }
                )
            )
        }.asLiveData()
    val metadataHandler = cacheRepository.deviceMetadataHandler
    val deviceState = home.map { resultat ->
        resultat.fold(
            onSuccess = { if (it.devices.isEmpty()) Empty else Normal },
            onFailure = { Error },
            onLoading = { Loading }
        )
    }.asLiveData()

    fun loadDevice() {
        miotRepository.refreshCurrentHome()
    }
}