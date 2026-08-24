package com.github.miwu.ui.device

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.github.miwu.logic.repository.LocalRepository
import com.github.miwu.logic.device.DeviceSessionResolver
import com.github.miwu.utils.Logger
import com.github.miwu.utils.MiotDeviceClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import miwu.android.icon.generated.icon.AndroidIcons
import miwu.android.translate.AndroidTranslateHelper
import miwu.miot.model.spec.SpecAtt
import miwu.miot.model.miot.MiotDevice
import miwu.miot.provider.MiotSpecAttrProvider
import miwu.support.manager.MiotDeviceManager
import org.koin.core.component.KoinComponent

class DeviceViewModel(
    private val application: Application,
    private val localRepository: LocalRepository,
    private val savedStateHandle: SavedStateHandle,
    private val specAttrProvider: MiotSpecAttrProvider,
    sessionResolver: DeviceSessionResolver,
    private val uiDispatcher: CoroutineDispatcher,
    private val workDispatcher: CoroutineDispatcher,
) : AndroidViewModel(application), MiotDeviceManager.Callback, KoinComponent {
    private val logger = Logger()
    private val _event = Channel<Event>()
    private val session = sessionResolver.resolve(savedStateHandle["did"])
    val device: MiotDevice? = session?.device
    private val user = session?.user
    private val miotDeviceClient = user?.let(::MiotDeviceClient)
    val event: ReceiveChannel<Event> = _event
    val isFromTile = savedStateHandle.get<Boolean>("isFromTile") ?: false
    val manager: MiotDeviceManager? by lazy {
        val currentDevice = device ?: return@lazy null
        MiotDeviceManager.build(
            miotDeviceClient,
            specAttrProvider,
            currentDevice,
            AndroidIcons,
            AndroidCache(application),
            AndroidTranslateHelper,
            uiDispatcher,
            workDispatcher,
            this
        )
    }

    fun printDeviceInfo() {
        device?.run {
            logger.info(
                "Current device: model={}, did={}, isOnline={}, specType={}",
                model,
                did,
                isOnline,
                specType,
            )
        }
    }

    fun addFavorite() {
        device?.let(localRepository::addDevice)
    }

    override fun onDeviceInitiated() {
        _event.trySend(Event.DeviceInitiated)
    }

    override fun onDeviceAttLoaded(specAtt: SpecAtt) {
        logger.info("Device attributes loaded: did={}", device?.did)
    }

    sealed interface Event {
        object DeviceInitiated : Event
    }
}
