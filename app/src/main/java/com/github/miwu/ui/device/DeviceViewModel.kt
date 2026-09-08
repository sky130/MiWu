package com.github.miwu.ui.device

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.github.miwu.domain.gateway.MiotClientFactory
import com.github.miwu.domain.repository.FavoriteDeviceRepository
import com.github.miwu.domain.usecase.device.ResolveDeviceSessionUseCase
import com.github.miwu.utils.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import miwu.android.icon.generated.icon.AndroidIcons
import miwu.android.translate.AndroidTranslateHelper
import miwu.miot.model.spec.SpecAtt
import miwu.miot.model.miot.MiotDevice
import miwu.miot.provider.MiotSpecAttrProvider
import miwu.support.manager.MiotDeviceManager
import org.koin.core.annotation.Named

class DeviceViewModel(
    private val application: Application,
    private val favoriteDeviceRepository: FavoriteDeviceRepository,
    private val savedStateHandle: SavedStateHandle,
    private val specAttrProvider: MiotSpecAttrProvider,
    resolveDeviceSession: ResolveDeviceSessionUseCase,
    clientFactory: MiotClientFactory,
    @Named("app_main_dispatcher") private val uiDispatcher: CoroutineDispatcher,
    @Named("app_io_dispatcher") private val workDispatcher: CoroutineDispatcher,
) : AndroidViewModel(application), MiotDeviceManager.Callback {
    private val logger = Logger()
    private val mutableEvent = MutableStateFlow<Event?>(null)
    private val session = resolveDeviceSession(savedStateHandle["did"])
    val device: MiotDevice? = session?.device
    private val user = session?.user
    private val miotDeviceClient = user?.let(clientFactory::createDeviceClient)
    val event: StateFlow<Event?> = mutableEvent.asStateFlow()
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
        device?.let { currentDevice ->
            viewModelScope.launch { favoriteDeviceRepository.add(currentDevice) }
        }
    }

    override fun onDeviceInitiated() {
        mutableEvent.value = Event.DeviceInitiated
    }

    fun consumeEvent(event: Event) {
        mutableEvent.compareAndSet(event, null)
    }

    override fun onDeviceAttLoaded(specAtt: SpecAtt) {
        logger.info("Device attributes loaded: did={}", device?.did)
    }

    sealed interface Event {
        object DeviceInitiated : Event
    }
}
