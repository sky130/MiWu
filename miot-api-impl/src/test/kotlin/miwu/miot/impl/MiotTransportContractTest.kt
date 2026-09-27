package miwu.miot.impl

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import miwu.miot.model.request.GetDevices
import miwu.miot.service.HomeService
import miwu.miot.utils.JsonConverterFactory
import miwu.miot.utils.Retrofit
import miwu.miot.utils.create
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MiotTransportContractTest {
    private lateinit var server: MockWebServer
    private lateinit var service: HomeService

    @Before
    fun setUp() {
        server = MockWebServer()
        service = Retrofit(
            baseUrl = server.url("/v2/").toString(),
            factories = arrayOf(JsonConverterFactory()),
        ).create()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun getDevicesUsesSharedRequestContract() = runBlocking {
        server.enqueue(errorResponse("application/json"))

        service.getDevices(GetDevices(homeId = 123L, ownerUid = 456L, limit = 78))

        val body = Json.parseToJsonElement(server.takeRequest().body.readUtf8()).jsonObject
        assertEquals("123", body.getValue("home_id").jsonPrimitive.content)
        assertEquals("456", body.getValue("home_owner").jsonPrimitive.content)
        assertEquals("78", body.getValue("limit").jsonPrimitive.content)
    }

    @Test
    fun textPlainNullResultPreservesBusinessError() = runBlocking {
        server.enqueue(errorResponse("text/plain"))

        val response = service.getDevices(GetDevices(homeId = 123L, ownerUid = 456L))

        assertEquals(-1, response.code)
        assertEquals("no permit", response.message)
        assertEquals(null, response.result)
    }

    private fun errorResponse(contentType: String) = MockResponse()
        .setHeader("Content-Type", contentType)
        .setBody("""{"code":-1,"message":"no permit","result":null}""")
}
