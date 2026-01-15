package miwu.miot.kmp.impl.client

import io.ktor.client.plugins.ClientRequestException
import miwu.miot.client.MiotUserClient
import miwu.miot.exception.MiotAuthException
import miwu.miot.exception.MiotClientException
import miwu.miot.kmp.service.body.GetUserInfo
import miwu.miot.kmp.service.createUserService
import miwu.miot.kmp.utils.MiotAuthKtorfit
import miwu.miot.model.MiotUser

class MiotUserClientImpl(private val user: MiotUser) : MiotUserClient {
    private val ktorfit = MiotAuthKtorfit(user)
    private val userService = ktorfit.createUserService()

    override suspend fun getUserInfo() = runCatching {
        userService.getUserInfo(GetUserInfo(user.userId))
    }.recoverCatching {
        when (it) {
            is ClientRequestException -> {
                if (it.response.status.value == 401) throw MiotAuthException.tokenExpired(it)
            }
        }
        throw MiotClientException.getUserInfoFailed(it)
    }

    override suspend fun checkTokenExpired() = runCatching {
        when (getUserInfo().exceptionOrNull()) {
            null -> false
            is MiotAuthException -> true
            else -> false
        }
    }
}