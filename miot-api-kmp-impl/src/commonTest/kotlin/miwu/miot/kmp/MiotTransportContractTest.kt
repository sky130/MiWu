package miwu.miot.kmp

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import miwu.miot.kmp.service.HomeService
import miwu.miot.kmp.service.createHomeService
import miwu.miot.kmp.impl.provider.buildLoginQrCodeUrl
import miwu.miot.kmp.impl.provider.findSetCookie
import miwu.miot.kmp.utils.MiotAuthHttpClient
import miwu.miot.kmp.utils.MiotAuthKtorfit
import miwu.miot.model.MiotUser
import miwu.miot.model.request.GetDevices

class MiotTransportContractTest {
    @Test
    fun getDevicesUsesSharedRequestContract() = runBlocking {
        var requestBody = ""
        val (client, service) = contractService { body -> requestBody = body }

        service.getDevices(GetDevices(homeId = 123L, ownerUid = 456L, limit = 78))

        val body = Json.parseToJsonElement(requestBody).jsonObject
        assertEquals("123", body.getValue("home_id").jsonPrimitive.content)
        assertEquals("456", body.getValue("home_owner").jsonPrimitive.content)
        assertEquals("78", body.getValue("limit").jsonPrimitive.content)
        client.close()
    }

    @Test
    fun textPlainNullResultPreservesBusinessError() = runBlocking {
        val (client, service) = contractService()

        val response = service.getDevices(GetDevices(homeId = 123L, ownerUid = 456L))

        assertEquals(-1, response.code)
        assertEquals("no permit", response.message)
        assertEquals(null, response.result)
        client.close()
    }

    @Test
    fun qrCodeParametersRemainIndividuallyAddressable() {
        val url = Url(buildLoginQrCodeUrl(123L))

        assertEquals("240", url.parameters["_qrsize"])
        assertEquals("?sid=xiaomiio", url.parameters["qs"])
        assertEquals("https://sts.api.io.mi.com/sts", url.parameters["callback"])
        assertEquals("xiaomiio", url.parameters["sid"])
        assertEquals("", url.parameters["serviceParam"])
        assertEquals("zh_CN", url.parameters["_locale"])
        assertEquals("123", url.parameters["_dc"])
    }

    @Test
    fun serviceTokenCanComeFromASecondSetCookieHeader() {
        val headers = headersOf(
            HttpHeaders.SetCookie,
            listOf("other=value; Path=/", "serviceToken=expected; Path=/"),
        )

        assertEquals("expected", headers.findSetCookie("serviceToken")?.value)
    }

    private fun contractService(
        captureBody: (String) -> Unit = {},
    ): Pair<HttpClient, HomeService> {
        val engine = MockEngine { request ->
            val form = request.body as? FormDataContent
                ?: error("Unexpected request body: ${request.body::class}")
            captureBody(requireNotNull(form.formData["data"]))
            assertEquals(
                "PassportDeviceId=device-id;userId=user-id;serviceToken=service-token",
                request.headers[HttpHeaders.Cookie],
            )
            assertEquals(true, requireNotNull(form.formData["_nonce"]).isNotEmpty())
            assertEquals(true, requireNotNull(form.formData["signature"]).isNotEmpty())
            respond(
                content = """{"code":-1,"message":"no permit","result":null}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Text.Plain.toString()),
            )
        }
        val client = MiotAuthHttpClient(TEST_USER, engine)
        val service = MiotAuthKtorfit(client, "https://example.test/").createHomeService()
        return client to service
    }

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
