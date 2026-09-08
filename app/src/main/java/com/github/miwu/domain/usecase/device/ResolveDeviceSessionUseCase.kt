package com.github.miwu.domain.usecase.device

import com.github.miwu.domain.repository.AccountRepository
import com.github.miwu.domain.repository.FavoriteDeviceRepository
import com.github.miwu.domain.repository.HomeRepository
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotDevice
import org.koin.core.annotation.Factory

@Factory
class ResolveDeviceSessionUseCase(
    private val accountRepository: AccountRepository,
    private val homeRepository: HomeRepository,
    private val favoriteDeviceRepository: FavoriteDeviceRepository,
) {
    operator fun invoke(did: String?): DeviceSession? {
        val safeDid = did?.takeIf { DID_PATTERN.matches(it) } ?: return null
        val user = accountRepository.currentUser?.takeIf { it.hasCredentials() } ?: return null
        val uid = user.userId.toLongOrNull() ?: return null
        val currentDevice = homeRepository.currentHome.value
            .getOrNull()
            ?.devices
            ?.firstOrNull { it.did == safeDid }
        val favoriteDevice = favoriteDeviceRepository.find(safeDid, uid)
        val device = currentDevice ?: favoriteDevice ?: return null
        return device.takeIf { it.specType != null }?.let { DeviceSession(it, user) }
    }

    data class DeviceSession(val device: MiotDevice, val user: MiotUser)

    private fun MiotUser.hasCredentials(): Boolean =
        userId.isNotEmpty() && cUserId.isNotEmpty() && passToken.isNotEmpty()

    private companion object {
        val DID_PATTERN = Regex("[A-Za-z0-9._:-]{1,128}")
    }
}
