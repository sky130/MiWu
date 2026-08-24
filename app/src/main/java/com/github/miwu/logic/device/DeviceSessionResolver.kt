package com.github.miwu.logic.device

import com.github.miwu.logic.auth.AuthService
import com.github.miwu.logic.datastore.isLogin
import com.github.miwu.logic.repository.LocalRepository
import com.github.miwu.logic.repository.MiotRepository
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotDevice

class DeviceSessionResolver(
    private val authService: AuthService,
    private val miotRepository: MiotRepository,
    private val localRepository: LocalRepository,
) {
    fun resolve(did: String?): DeviceSession? {
        val safeDid = did?.takeIf { DID_PATTERN.matches(it) } ?: return null
        val user = authService.getCurrentUser()?.takeIf(MiotUser::isLogin) ?: return null
        val uid = user.userId.toLongOrNull() ?: return null
        val currentDevice = miotRepository.currentHome.value
            .getOrNull()
            ?.devices
            ?.firstOrNull { it.did == safeDid }
        val favoriteDevice = localRepository.findDevice(safeDid, uid)
        val device = currentDevice ?: favoriteDevice ?: return null
        return device.takeIf { it.specType != null }?.let { DeviceSession(it, user) }
    }

    data class DeviceSession(val device: MiotDevice, val user: MiotUser)

    private companion object {
        val DID_PATTERN = Regex("[A-Za-z0-9._:-]{1,128}")
    }
}
