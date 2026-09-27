package miwu.miot.client.transport

import kotlinx.serialization.json.JsonElement
import miwu.miot.att.get.GetAtt
import miwu.miot.att.get.piid
import miwu.miot.att.get.siid
import miwu.miot.att.set.SetAtt
import miwu.miot.att.set.piid
import miwu.miot.att.set.siid
import miwu.miot.att.set.value
import miwu.miot.client.MiotDeviceClient
import miwu.miot.client.MiotHomeClient
import miwu.miot.client.MiotUserClient
import miwu.miot.exception.MiotClientException
import miwu.miot.exception.MiotDeviceException
import miwu.miot.model.MiotResponse
import miwu.miot.model.MiotUser
import miwu.miot.model.actionOutputOrUnit
import miwu.miot.model.att.ActionList
import miwu.miot.model.att.PropertyList
import miwu.miot.model.miot.DeviceList
import miwu.miot.model.miot.HomeList
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotScene
import miwu.miot.model.miot.SceneList
import miwu.miot.model.miot.UserInfo
import miwu.miot.model.request.ActionBody
import miwu.miot.model.request.GetDevices
import miwu.miot.model.request.GetHome
import miwu.miot.model.request.GetParams
import miwu.miot.model.request.GetScene
import miwu.miot.model.request.GetUserInfo
import miwu.miot.model.request.RunNewScene
import miwu.miot.model.request.SetParams
import miwu.miot.model.request.toGetDevices
import miwu.miot.model.requireCodeSuccess
import miwu.miot.model.requirePropertySuccess
import miwu.miot.model.requireSuccess
import miwu.miot.utils.runCatchingSuspend

abstract class TransportMiotHomeClient : MiotHomeClient {
    protected abstract suspend fun requestHomes(body: GetHome): MiotResponse<HomeList>
    protected abstract suspend fun requestDevices(body: GetDevices): MiotResponse<DeviceList>
    protected abstract suspend fun requestScenes(body: GetScene): MiotResponse<SceneList>
    protected abstract suspend fun requestRunScene(body: RunNewScene): MiotResponse<JsonElement>

    override suspend fun getHomes(
        fetchShare: Boolean,
        fetchShareDev: Boolean,
        appVer: Int,
        limit: Int,
    ) = runCatchingSuspend {
        requestHomes(GetHome(appVer, fetchShare, fetchShareDev, false, limit))
            .requireSuccess("Get homes")
    }.recoverCatching { throw MiotClientException.getHomesFailed(it) }

    override suspend fun getDevices(home: MiotHome, limit: Int) = runCatchingSuspend {
        requestDevices(home.toGetDevices(limit)).requireSuccess("Get devices")
    }.recoverCatching { throw MiotClientException.getDevicesFailed(it) }

    override suspend fun getScenes(home: MiotHome) =
        getScenes(home.id.toLong(), home.uid)

    override suspend fun getScenes(homeId: Long, ownerUid: Long) = runCatchingSuspend {
        requestScenes(GetScene(homeId = homeId.toString(), ownerUid = ownerUid.toString()))
            .requireSuccess("Get scenes")
    }.recoverCatching { throw MiotClientException.getScenesFailed(it) }

    override suspend fun getDevices(homeId: Long, ownerUid: Long, limit: Int) =
        runCatchingSuspend {
            requestDevices(GetDevices(homeId = homeId, ownerUid = ownerUid, limit = limit))
                .requireSuccess("Get devices")
        }.recoverCatching { throw MiotClientException.getDevicesFailed(it) }

    override suspend fun runScene(home: MiotHome, scene: MiotScene) =
        runScene(home.id.toLong(), home.uid, scene)

    override suspend fun runScene(homeId: Long, ownerUid: Long, scene: MiotScene) =
        runCatchingSuspend {
            requestRunScene(
                RunNewScene(homeId.toString(), ownerUid.toString(), scene.sceneId)
            ).requireCodeSuccess("Run scene")
        }
}

abstract class TransportMiotUserClient(private val user: MiotUser) : MiotUserClient {
    protected abstract suspend fun requestUserInfo(body: GetUserInfo): MiotResponse<UserInfo>

    override suspend fun getUserInfo() = runCatchingSuspend {
        requestUserInfo(GetUserInfo(user.userId)).requireSuccess("Get user info")
    }

    override suspend fun getIsServiceTokenValid() = runCatchingSuspend {
        getUserInfo().getOrThrow()
        true
    }
}

abstract class TransportMiotDeviceClient : MiotDeviceClient {
    protected abstract suspend fun requestGetProperties(body: GetParams): MiotResponse<PropertyList>
    protected abstract suspend fun requestSetProperties(body: SetParams): MiotResponse<PropertyList>
    protected abstract suspend fun requestAction(body: ActionBody): MiotResponse<ActionList>

    override suspend fun get(device: MiotDevice, att: Array<out GetAtt>) = runCatchingSuspend {
        val params = Array(att.size) { index ->
            att[index].run { GetParams.Att(device.did, siid, piid) }
        }
        requestGetProperties(GetParams(params)).requirePropertySuccess("Get device properties")
    }.recoverCatching {
        val specType = device.specType ?: throw it
        throw MiotClientException.getSpecAttFailed(specType, it)
    }

    override suspend fun set(device: MiotDevice, att: Array<out SetAtt>) = runCatchingSuspend {
        val params = Array(att.size) { index ->
            att[index].run { SetParams.Att(device.did, siid, piid, value) }
        }
        requestSetProperties(SetParams(params)).requirePropertySuccess("Set device properties")
        Unit
    }.recoverCatching {
        val specType = device.specType ?: throw MiotDeviceException.specNotFound(device.model)
        throw MiotClientException.getSpecAttFailed(specType, it)
    }

    override suspend fun action(
        device: MiotDevice,
        siid: Int,
        aiid: Int,
        vararg input: Any,
    ) = runCatchingSuspend {
        requestAction(
            ActionBody.Action(device.did, siid, aiid)
                .apply { `in`.addAll(input) }
                .body()
        ).actionOutputOrUnit("Execute device action")
    }.recoverCatching {
        throw MiotClientException.actionFailed(device.did, siid, aiid, *input, it)
    }
}
