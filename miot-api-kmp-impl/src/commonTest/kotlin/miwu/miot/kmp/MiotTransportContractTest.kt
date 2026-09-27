package miwu.miot.kmp

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import miwu.miot.kmp.utils.json
import miwu.miot.model.MiotResponse
import miwu.miot.model.miot.DeviceList
import miwu.miot.model.request.GetDevices

class MiotTransportContractTest {
    @Test
    fun getDevicesUsesSharedRequestContract() = runBlocking {
        var requestBody = ""
        val client = contractClient { body -> requestBody = body }

        client.post("https://example.test/v2/home/home_device_list") {
            contentType(ContentType.Application.Json)
            setBody(GetDevices(homeId = 123L, ownerUid = 456L, limit = 78))
        }.body<MiotResponse<DeviceList>>()

        val body = Json.parseToJsonElement(requestBody).jsonObject
        assertEquals("123", body.getValue("home_id").jsonPrimitive.content)
        assertEquals("456", body.getValue("home_owner").jsonPrimitive.content)
        assertEquals("78", body.getValue("limit").jsonPrimitive.content)
        client.close()
    }

    @Test
    fun textPlainNullResultPreservesBusinessError() = runBlocking {
        val client = contractClient()

        val response = client.post("https://example.test/v2/home/home_device_list") {
            contentType(ContentType.Application.Json)
            setBody(GetDevices(homeId = 123L, ownerUid = 456L))
        }.body<MiotResponse<DeviceList>>()

        assertEquals(-1, response.code)
        assertEquals("no permit", response.message)
        assertEquals(null, response.result)
        client.close()
    }

    private fun contractClient(captureBody: (String) -> Unit = {}): HttpClient {
        val engine = MockEngine { request ->
            val body = when (val content = request.body) {
                is TextContent -> content.text
                is OutgoingContent.ByteArrayContent -> content.bytes().decodeToString()
                else -> error("Unexpected request body: ${content::class}")
            }
            captureBody(body)
            respond(
                content = """{"code":-1,"message":"no permit","result":null}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Text.Plain.toString()),
            )
        }
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(json, ContentType.Application.Json)
                json(json, ContentType.Text.Plain)
            }
        }
    }
}
