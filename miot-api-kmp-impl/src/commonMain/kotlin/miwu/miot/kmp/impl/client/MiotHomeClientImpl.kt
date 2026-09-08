package miwu.miot.kmp.impl.client

import miwu.miot.client.MiotHomeClient
import miwu.miot.exception.MiotClientException
import miwu.miot.kmp.service.body.GetDevices
import miwu.miot.kmp.service.body.GetHome
import miwu.miot.kmp.service.body.GetScene
import miwu.miot.kmp.service.body.RunNewScene
import miwu.miot.kmp.service.createHomeService
import miwu.miot.kmp.utils.MiotAuthKtorfit
import miwu.miot.model.MiotUser
import miwu.miot.model.requireSuccess
import kotlinx.coroutines.CancellationException
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotScene
import miwu.miot.utils.runCatchingSuspend
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class MiotHomeClientImpl(@InjectedParam private val user: MiotUser) : MiotHomeClient {
    private val ktorfit = MiotAuthKtorfit(user)
    private val homeService = ktorfit.createHomeService()

    override suspend fun getHomes(
        fetchShare: Boolean,
        fetchShareDev: Boolean,
        appVer: Int,
        limit: Int
    ) = runCatchingSuspend {
        homeService.getHomes(GetHome(appVer, fetchShare, fetchShareDev, false, limit))
            .requireSuccess("Get homes")
    }.recoverCatching {
        throw MiotClientException.getHomesFailed(it)
    }

    override suspend fun getDevices(
        home: MiotHome,
        limit: Int
    ) = runCatchingSuspend {
        getDevices(home.uid, home.id.toLong(), limit).getOrThrow()
    }

    override suspend fun getScenes(home: MiotHome) = runCatchingSuspend {
        homeService.getScenes(GetScene(homeId = home.id, ownerUid = home.uid.toString()))
            .requireSuccess("Get scenes")
    }.recoverCatching {
        throw MiotClientException.getScenesFailed(it)
    }

    override suspend fun getScenes(
        homeId: Long,
        ownerUid: Long
    ) = runCatchingSuspend {
        homeService.getScenes(GetScene(homeId = homeId.toString(), ownerUid = ownerUid.toString()))
            .requireSuccess("Get scenes")
    }.recoverCatching {
        throw MiotClientException.getScenesFailed(it)
    }

    override suspend fun getDevices(
        homeId: Long,
        ownerUid: Long,
        limit: Int
    ) = runCatchingSuspend {
        homeService.getDevices(GetDevices(ownerUid, homeId, limit)).requireSuccess("Get devices")
    }.recoverCatching {
        throw MiotClientException.getDevicesFailed(it)
    }

    override suspend fun runScene(
        home: MiotHome,
        scene: MiotScene
    ): Result<Unit> = runCatchingSuspend {
        homeService.runScene(RunNewScene(home.id, home.uid.toString(), scene.sceneId))
    }

    override suspend fun runScene(
        homeId: Long,
        ownerUid: Long,
        scene: MiotScene
    ): Result<Unit> = runCatchingSuspend {
        homeService.runScene(RunNewScene(homeId.toString(), ownerUid.toString(), scene.sceneId))
    }
}
