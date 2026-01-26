package miwu.miot.kmp.impl.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import miwu.miot.client.MiotUserClient
import miwu.miot.kmp.service.body.GetUserInfo
import miwu.miot.kmp.service.createUserService
import miwu.miot.kmp.utils.IO
import miwu.miot.kmp.utils.MiotAuthKtorfit
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotUserInfo

class MiotUserClientImpl(private val user: MiotUser) : MiotUserClient {
    private val ktorfit = MiotAuthKtorfit(user)
    private val userService = ktorfit.createUserService()

    override suspend fun getUserInfo(): Result<MiotUserInfo> = withContext(Dispatchers.IO) {
        runCatching {
            val getUserInfo = GetUserInfo(user.userId)
            return@runCatching userService.getUserInfo(getUserInfo)
        }
    }

    override suspend fun getIsServiceTokenValid(): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            val userInfo = getUserInfo().getOrThrow()
            return@runCatching userInfo.code == 0
        }
    }
}