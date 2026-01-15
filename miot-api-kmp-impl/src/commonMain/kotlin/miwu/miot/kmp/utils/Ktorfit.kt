package miwu.miot.kmp.utils

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.userAgent
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import miwu.miot.kmp.plugin.ForceJsonSerializer
import miwu.miot.kmp.plugin.MiotAuth
import miwu.miot.common.MIOT_SERVER_URL
import miwu.miot.common.MI_HOME_USER_AGENT
import miwu.miot.model.MiotUser

@Suppress("FunctionName")
fun MiotAuthHttpClient(user: MiotUser) = HttpClient {
    install(ContentNegotiation) {
        json(json)
    }
    install(DefaultRequest) {
        val (userId, securityToken, serviceToken, deviceId) = user
        contentType(ContentType.Application.Json)
        userAgent(MI_HOME_USER_AGENT)
        headers["x-xiaomi-protocal-flag-cli"] = "PROTOCAL-HTTP2"
        headers["Cookie"] =
            "PassportDeviceId=${deviceId};userId=${userId};serviceToken=$serviceToken"
    }
    install(MiotAuth) {
        user(user)
    }
    install(ForceJsonSerializer.Companion) {
        json(json)
    }
    expectSuccess = true
}

@Suppress("FunctionName")
fun MiotAuthKtorfit(user: MiotUser) = Ktorfit.Builder()
    .baseUrl(MIOT_SERVER_URL)
    .httpClient(MiotAuthHttpClient(user))
    .build()

