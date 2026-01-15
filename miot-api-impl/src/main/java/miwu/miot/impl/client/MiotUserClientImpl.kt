package miwu.miot.impl.client

import miwu.miot.utils.JsonConverterFactory
import miwu.miot.utils.OkHttpClient
import miwu.miot.utils.Retrofit
import miwu.miot.utils.create
import miwu.miot.service.body.GetUserInfo
import miwu.miot.client.MiotUserClient
import miwu.miot.common.MIOT_SERVER_URL
import miwu.miot.exception.MiotAuthException
import miwu.miot.exception.MiotClientException
import miwu.miot.interceptor.MiotAuthInterceptor
import miwu.miot.model.MiotUser
import miwu.miot.service.UserService
import retrofit2.HttpException

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

    override suspend fun getUserInfo() = runCatching {
        userService.getUserInfo(GetUserInfo(user.userId))
    }.recoverCatching {
        when (it) {
            is HttpException -> {
                if (it.code() == 401) throw MiotAuthException.tokenExpired(it)
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