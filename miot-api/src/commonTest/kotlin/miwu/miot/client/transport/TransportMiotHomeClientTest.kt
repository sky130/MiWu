package miwu.miot.client.transport

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import miwu.miot.exception.MiotBusinessException
import miwu.miot.model.MiotResponse
import miwu.miot.model.miot.DeviceList
import miwu.miot.model.miot.HomeList
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotScene
import miwu.miot.model.miot.SceneList
import miwu.miot.model.request.GetDevices
import miwu.miot.model.request.GetHome
import miwu.miot.model.request.GetScene
import miwu.miot.model.request.RunNewScene

class TransportMiotHomeClientTest {
    @Test
    fun homeMapsIdAndOwnerToTheirNamedRequestFields() = runBlocking {
        val client = FakeHomeClient()

        client.getDevices(home(id = "123", uid = 456L), limit = 78).getOrThrow()

        assertEquals(GetDevices(homeId = 123L, ownerUid = 456L, limit = 78), client.devicesBody)
    }

    @Test
    fun runSceneRejectsBusinessError() = runBlocking {
        val client = FakeHomeClient(
            runSceneResponse = MiotResponse(code = -1, message = "no permit", result = JsonNull),
        )

        val result = client.runScene(homeId = 123L, ownerUid = 456L, scene = scene("789"))

        val error = assertFailsWith<MiotBusinessException> { result.getOrThrow() }
        assertEquals(-1, error.code)
        assertEquals("Run scene failed: no permit", error.message)
        assertEquals(
            RunNewScene(homeId = "123", ownerUid = "456", sceneId = "789"),
            client.runSceneBody,
        )
    }

    private class FakeHomeClient(
        private val runSceneResponse: MiotResponse<JsonElement> =
            MiotResponse(code = 0, result = JsonNull),
    ) : TransportMiotHomeClient() {
        var devicesBody: GetDevices? = null
        var runSceneBody: RunNewScene? = null

        override suspend fun requestHomes(body: GetHome) = MiotResponse(
            0,
            result = HomeList(hasMore = false, homes = emptyList(), maxId = ""),
        )

        override suspend fun requestDevices(body: GetDevices): MiotResponse<DeviceList> {
            devicesBody = body
            return MiotResponse(0, result = DeviceList(hasMore = false, maxDid = ""))
        }

        override suspend fun requestScenes(body: GetScene) =
            MiotResponse(0, result = SceneList(distanceCeiling = 0))

        override suspend fun requestRunScene(body: RunNewScene): MiotResponse<JsonElement> {
            runSceneBody = body
            return runSceneResponse
        }

        override fun close() = Unit
    }

    private fun home(id: String, uid: Long) = MiotHome(
        address = "",
        background = "",
        bssid = "",
        carDid = "",
        cityId = 0,
        createTime = 0,
        dids = emptyList(),
        icon = "",
        id = id,
        latitude = 0.0,
        longitude = 0.0,
        name = "",
        permitLevel = 0,
        popupFlag = 0,
        popupTimeStamp = 0,
        rooms = emptyList(),
        shareFlag = 0,
        smartRoomBackground = "",
        status = 0,
        tempDids = JsonNull,
        uid = uid,
    )

    private fun scene(id: String) = MiotScene(
        authed = emptyList(),
        createTime = "",
        enable = true,
        iconUrl = "",
        lastEditTime = "",
        name = "",
        sceneAction = MiotScene.SceneAction(emptyList(), 0),
        sceneId = id,
        tags = MiotScene.Tags(),
        templateId = "",
        type = 0,
        version = 0,
    )
}
