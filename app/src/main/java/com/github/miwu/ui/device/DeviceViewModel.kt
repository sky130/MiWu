package com.github.miwu.ui.device

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.miwu.logic.database.AppDatabase
import com.github.miwu.logic.database.entity.FavoriteDevice.Companion.toMiwu
import com.github.miwu.logic.repository.LocalRepository
import com.github.miwu.utils.Logger
import com.github.miwu.utils.MiotDeviceClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import miwu.android.icon.generated.icon.AndroidIcons
import miwu.android.translate.AndroidTranslateHelper
import miwu.miot.kmp.utils.to
import miwu.miot.model.MiotUser
import miwu.miot.model.att.SpecAtt
import miwu.miot.model.miot.MiotDevice
import miwu.miot.provider.MiotSpecAttrProvider
import miwu.support.manager.MiotDeviceManager
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

class DeviceViewModel(
    private val application: Application,
    private val localRepository: LocalRepository,
    private val savedStateHandle: SavedStateHandle,
    private val specAttrProvider: MiotSpecAttrProvider
) : AndroidViewModel(application), MiotDeviceManager.Callback, KoinComponent {
    private val logger = Logger()
    private val device = savedStateHandle.get<String>("device")
        ?.to<MiotDevice>()
        ?.getOrThrow()
        ?: error("MiotDevice is not found")
    private val user = savedStateHandle.get<String>("user")
        ?.to<MiotUser>()
        ?.getOrThrow()
        ?: error("MiotUser is not found")
    private val miotDeviceClient = MiotDeviceClient(user)
    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()
    val isFromTile = savedStateHandle.get<Boolean>("isFromTile") ?: false
    val manager by lazy {
        MiotDeviceManager.build(
            miotDeviceClient,
            specAttrProvider,
            device,
            AndroidIcons,
            AndroidCache(application),
            AndroidTranslateHelper,
            Dispatchers.Main,
            this
        )
    }

    fun printDeviceInfo() {
        with(device) {
            logger.info(
                "Current miot device info: model={}, mac={}, did={}, isOnline={}, specType={}",
                model,
                mac,
                did,
                isOnline,
                specType,
            )
            logger.debug("Current miot all device info: {}", this)
        }
    }

    fun addFavorite() {
        localRepository.addDevice(device)
    }

    override fun onDeviceInitiated() {
        viewModelScope.launch {
            _event.emit(Event.DeviceInitiated)
        }
    }

    override fun onDeviceAttLoaded(specAtt: SpecAtt) {
        logger.info("Device {}, spec att: {}", device.name, specAtt)
    }

    sealed interface Event {
        object DeviceInitiated: Event
    }
}