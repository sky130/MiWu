package com.github.miwu.ui.device

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.github.miwu.logic.repository.LocalRepository
import com.github.miwu.utils.Logger
import com.github.miwu.utils.MiotDeviceClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import miwu.android.icon.generated.icon.AndroidIcons
import miwu.android.translate.AndroidTranslateHelper
import miwu.miot.kmp.utils.to
import miwu.miot.model.MiotUser
import miwu.miot.model.att.SpecAtt
import miwu.miot.model.miot.MiotDevice
import miwu.miot.provider.MiotSpecAttrProvider
import miwu.mock.MOCK_PREFIX
import miwu.mock.NormalMockMiotDeviceClient
import miwu.support.manager.MiotDeviceManager
import org.koin.core.component.KoinComponent

class DeviceViewModel(
    private val application: Application,
    private val localRepository: LocalRepository,
    private val savedStateHandle: SavedStateHandle,
    private val specAttrProvider: MiotSpecAttrProvider
) : AndroidViewModel(application), MiotDeviceManager.Callback, KoinComponent {
    private val logger = Logger()
    private val _event = Channel<Event>()
    private val isMockDevice: Boolean
    val device = savedStateHandle.get<String>("device")
        ?.to<MiotDevice>()
        ?.getOrThrow()
        ?.takeIf { it.specType != null }
        ?.also { isMockDevice = it.specType!!.startsWith(MOCK_PREFIX) }
        ?.let {
            if (isMockDevice) it.copy(specType = it.specType!!.substring(MOCK_PREFIX.length))
            else it
        }
        ?: error("MiotDevice is not found")
    val user = savedStateHandle.get<String>("user")
        ?.to<MiotUser>()
        ?.getOrThrow()
        ?: error("MiotUser is not found")
    val miotDeviceClient = if (isMockDevice) null else MiotDeviceClient(user)
    val event: ReceiveChannel<Event> = _event
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
            this,
            ::NormalMockMiotDeviceClient
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
        _event.trySend(Event.DeviceInitiated)
    }

    override fun onDeviceAttLoaded(specAtt: SpecAtt) {
        logger.info("onDeviceAttLoaded, device {}, spec att: {}", device.name, specAtt)
    }

    sealed interface Event {
        object DeviceInitiated : Event
    }
}