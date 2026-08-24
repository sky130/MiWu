package miwu.miot.impl.client

import miwu.miot.client.MiotUserClient
import miwu.miot.common.MIOT_SERVER_URL
import miwu.miot.interceptor.MiotAuthInterceptor
import miwu.miot.model.MiotUser
import miwu.miot.model.MiotResponse
import miwu.miot.model.requireSuccess
import kotlinx.coroutines.CancellationException
import miwu.miot.model.miot.UserInfo
import miwu.miot.service.UserService
import miwu.miot.service.body.GetUserInfo
import miwu.miot.utils.JsonConverterFactory
import miwu.miot.utils.OkHttpClient
import miwu.miot.utils.Retrofit
import miwu.miot.utils.create
import miwu.miot.utils.runCatchingSuspend

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
