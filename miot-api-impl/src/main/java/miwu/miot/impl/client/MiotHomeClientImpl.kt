package miwu.miot.impl.client

import miwu.miot.utils.JsonConverterFactory
import miwu.miot.utils.OkHttpClient
import miwu.miot.utils.Retrofit
import miwu.miot.utils.create
import miwu.miot.client.MiotHomeClient
import miwu.miot.common.MIOT_SERVER_URL
import miwu.miot.exception.MiotClientException
import miwu.miot.interceptor.MiotAuthInterceptor
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotScene
import miwu.miot.service.HomeService
import miwu.miot.service.body.GetDevices
import miwu.miot.service.body.GetHome
import miwu.miot.service.body.GetScene
import miwu.miot.service.body.RunNewScene

class MiotHomeClientImpl(private val user: MiotUser) : MiotHomeClient {
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
    private val homeService = retrofit.create<HomeService>()

    override suspend fun getHomes(
        fetchShare: Boolean,
        fetchShareDev: Boolean,
        appVer: Int,
        limit: Int
    ) = runCatching {
        homeService.getHomes(GetHome(appVer, fetchShare, fetchShareDev, false, limit))
    }.recoverCatching {
        throw MiotClientException.getHomesFailed(it)
    }

    override suspend fun getDevices(
        home: MiotHome,
        limit: Int
    ) = runCatching {
        getDevices(home.uid, home.id.toLong(), limit).getOrThrow()
    }

    override suspend fun getScenes(home: MiotHome) = runCatching {
        homeService.getScenes(GetScene(homeId = home.id, ownerUid = home.uid.toString()))
    }.recoverCatching {
        throw MiotClientException.getScenesFailed(it)
    }

    override suspend fun getScenes(
        homeId: Long,
        ownerUid: Long
    ) = runCatching {
        homeService.getScenes(GetScene(homeId = homeId.toString(), ownerUid = ownerUid.toString()))
    }.recoverCatching {
        throw MiotClientException.getScenesFailed(it)
    }

    override suspend fun getDevices(
        homeId: Long,
        ownerUid: Long,
        limit: Int
    ) = runCatching {
        homeService.getDevices(GetDevices(ownerUid, homeId, limit))
    }.recoverCatching {
        throw MiotClientException.getDevicesFailed(it)
    }

    override suspend fun runScene(
        home: MiotHome,
        scene: MiotScene
    ): Result<Unit> = runCatching {
        homeService.runScene(RunNewScene(home.id, home.uid.toString(), scene.sceneId)).use {
            it.string()
        }
    }

    override suspend fun runScene(
        homeId: Long,
        ownerUid: Long,
        scene: MiotScene
    ): Result<Unit> = runCatching {
        homeService.runScene(RunNewScene(homeId.toString(), ownerUid.toString(), scene.sceneId))
    }
}