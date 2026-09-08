package com.github.miwu.data.metadata

import com.github.miwu.di.AppScope
import com.github.miwu.di.IoDispatcher
import com.github.miwu.domain.repository.DeviceIconRepository
import com.github.miwu.domain.repository.DeviceMetadataRepository
import com.github.miwu.domain.repository.FavoriteDeviceRepository
import com.github.miwu.utils.Logger
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Singleton

@Singleton
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class DeviceIconRepositoryImpl(
    favoriteDeviceRepository: FavoriteDeviceRepository,
    metadataRepository: DeviceMetadataRepository,
    private val httpClient: HttpClient,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @AppScope applicationScope: CoroutineScope,
) : DeviceIconRepository {
    private val logger = Logger()
    private val mutableIcons = MutableStateFlow<Map<String, ByteArray>>(emptyMap())
    override val icons: StateFlow<Map<String, ByteArray>> = mutableIcons.asStateFlow()

    init {
        combine(favoriteDeviceRepository.devices, metadataRepository.metadata) { devices, metadata ->
            devices.map { it.model }.distinct() to metadata
        }
            .mapLatest { (models, metadata) ->
                models.toSet() to loadMissingIcons(models, metadata.icons)
            }
            .onEach { (activeModels, loaded) ->
                mutableIcons.value = mutableIcons.value
                    .filterKeys(activeModels::contains)
                    .plus(loaded)
            }
            .launchIn(applicationScope)
    }

    private suspend fun loadMissingIcons(
        models: List<String>,
        iconUrls: Map<String, String>,
    ): Map<String, ByteArray> = withContext(ioDispatcher) {
        buildMap {
            models.forEach { model ->
                currentCoroutineContext().ensureActive()
                if (mutableIcons.value.containsKey(model)) return@forEach
                val url = iconUrls[model] ?: return@forEach
                try {
                    put(model, httpClient.get(url).bodyAsBytes())
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Throwable) {
                    logger.warn("load icon failed: model={}, message={}", model, error.message)
                }
            }
        }
    }
}
