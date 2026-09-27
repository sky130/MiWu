package miwu.miot.impl

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import miwu.miot.model.request.GetDevices
import miwu.miot.interceptor.MiotAuthInterceptor
import miwu.miot.impl.provider.buildLoginQrCodeUrl
import miwu.miot.impl.provider.findSetCookie
import miwu.miot.model.MiotUser
import miwu.miot.service.HomeService
import miwu.miot.utils.JsonConverterFactory
import miwu.miot.utils.Retrofit
import miwu.miot.utils.create
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MiotTransportContractTest {
    private lateinit var server: MockWebServer
    private lateinit var client: OkHttpClient
    private lateinit var service: HomeService

    @Before
    fun setUp() {
        server = MockWebServer()
        client = OkHttpClient.Builder()
            .addInterceptor(MiotAuthInterceptor(TEST_USER))
            .build()
        service = Retrofit(
            baseUrl = server.url("/v2/").toString(),
            factories = arrayOf(JsonConverterFactory()),
            client = client,
        ).create()
    }

    @After
    fun tearDown() {
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
        server.shutdown()
    }

    @Test
    fun getDevicesUsesSharedRequestContract() = runBlocking {
        server.enqueue(errorResponse("application/json"))

        service.getDevices(GetDevices(homeId = 123L, ownerUid = 456L, limit = 78))

        val request = server.takeRequest()
        val form = "https://example.test/?${request.body.readUtf8()}".toHttpUrl()
        val body = Json.parseToJsonElement(form.queryParameter("data")!!).jsonObject
        assertEquals("123", body.getValue("home_id").jsonPrimitive.content)
        assertEquals("456", body.getValue("home_owner").jsonPrimitive.content)
        assertEquals("78", body.getValue("limit").jsonPrimitive.content)
        assertEquals(
            "PassportDeviceId=device-id;userId=user-id;serviceToken=service-token",
            request.getHeader("Cookie"),
        )
        assertEquals(true, form.queryParameter("_nonce")!!.isNotEmpty())
        assertEquals(true, form.queryParameter("signature")!!.isNotEmpty())
    }

    @Test
    fun textPlainNullResultPreservesBusinessError() = runBlocking {
        server.enqueue(errorResponse("text/plain"))

        val response = service.getDevices(GetDevices(homeId = 123L, ownerUid = 456L))

        assertEquals(-1, response.code)
        assertEquals("no permit", response.message)
        assertEquals(null, response.result)
    }

    @Test
    fun qrCodeParametersRemainIndividuallyAddressable() {
        val url = buildLoginQrCodeUrl(123L).toHttpUrl()

        assertEquals("240", url.queryParameter("_qrsize"))
        assertEquals("?sid=xiaomiio", url.queryParameter("qs"))
        assertEquals("https://sts.api.io.mi.com/sts", url.queryParameter("callback"))
        assertEquals("xiaomiio", url.queryParameter("sid"))
        assertEquals("", url.queryParameter("serviceParam"))
        assertEquals("zh_CN", url.queryParameter("_locale"))
        assertEquals("123", url.queryParameter("_dc"))
    }

    @Test
    fun serviceTokenCanComeFromASecondSetCookieHeader() {
        server.enqueue(
            MockResponse()
                .addHeader("Set-Cookie", "other=value; Path=/")
                .addHeader("Set-Cookie", "serviceToken=expected; Path=/"),
        )
        val cookieClient = OkHttpClient()

        try {
            cookieClient.newCall(Request.Builder().url(server.url("/")).build()).execute().use {
                assertEquals("expected", it.findSetCookie("serviceToken")?.value)
            }
        } finally {
            cookieClient.dispatcher.executorService.shutdown()
            cookieClient.connectionPool.evictAll()
        }
    }

    private fun errorResponse(contentType: String) = MockResponse()
        .setHeader("Content-Type", contentType)
        .setBody("""{"code":-1,"message":"no permit","result":null}""")

    private companion object {
        val TEST_USER = MiotUser(
            userId = "user-id",
            cUserId = "c-user-id",
            nonce = 0,
            ssecurity = "c2VjcmV0",
            psecurity = "",
            passToken = "pass-token",
            serviceToken = "service-token",
            deviceId = "device-id",
        )
    }
}
