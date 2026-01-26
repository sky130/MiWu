package miwu.miot.impl.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import miwu.miot.client.MiotUserClient
import miwu.miot.common.MIOT_SERVER_URL
import miwu.miot.interceptor.MiotAuthInterceptor
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotUserInfo
import miwu.miot.service.UserService
import miwu.miot.service.body.GetUserInfo
import miwu.miot.utils.JsonConverterFactory
import miwu.miot.utils.OkHttpClient
import miwu.miot.utils.Retrofit
import miwu.miot.utils.create

class MiotUserClientImpl(private val user: MiotUser) : MiotUserClient {
    private val client = OkHttpClient {
        addInterceptor(MiotAuthInterceptor(user))
    }
    private val retrofit = Retrofit(
        baseUrl = MIOT_SERVER_URL,
        factories = arrayOf(
            JsonConverterFactory()
        ),
        client = client
    )
    private val userService = retrofit.create<UserService>()

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