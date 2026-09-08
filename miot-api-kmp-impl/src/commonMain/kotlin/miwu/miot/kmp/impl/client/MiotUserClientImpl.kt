package miwu.miot.kmp.impl.client

import kotlinx.coroutines.CancellationException
import miwu.miot.client.MiotUserClient
import miwu.miot.kmp.service.body.GetUserInfo
import miwu.miot.kmp.service.createUserService
import miwu.miot.kmp.utils.MiotAuthKtorfit
import miwu.miot.model.MiotUser
import miwu.miot.model.MiotResponse
import miwu.miot.model.requireSuccess
import miwu.miot.model.miot.UserInfo
import miwu.miot.utils.runCatchingSuspend
import org.koin.core.annotation.InjectedParam

class MiotUserClientImpl(@InjectedParam private val user: MiotUser) : MiotUserClient {
    private val ktorfit = MiotAuthKtorfit(user)
    private val userService = ktorfit.createUserService()

    override suspend fun getUserInfo(): Result<MiotResponse<UserInfo>> =
        runCatchingSuspend {
            userService.getUserInfo(GetUserInfo(user.userId)).requireSuccess("Get user info")
        }

    override suspend fun getIsServiceTokenValid(): Result<Boolean> =
        runCatchingSuspend {
            getUserInfo().getOrThrow()
            true
        }
}
