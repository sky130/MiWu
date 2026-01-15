package miwu.miot.client

import miwu.miot.model.miot.MiotDevices
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotHomes
import miwu.miot.model.miot.MiotScene
import miwu.miot.model.miot.MiotScenes

interface MiotHomeClient {

    /**
     * 获取家庭列表
     * @param fetchShare 是否获取共享家庭
     * @param fetchShareDev 是否获取共享设备
     * @param appVer app 版本, 不清楚什么作用
     * @param limit 返回数量
     */
    suspend fun getHomes(
        fetchShare: Boolean = true,
        fetchShareDev: Boolean = true,
        appVer: Int = 7,
        limit: Int = 300,
    ): Result<MiotHomes>

    /**
     * 获取所选家庭下的设备列表
     * @param home
     * @param limit 返回数量
     */
    suspend fun getDevices(home: MiotHome, limit: Int = 200): Result<MiotDevices>

    /**
     * 获取家庭下的场景列表
     * @param home
     */
    suspend fun getScenes(home: MiotHome): Result<MiotScenes>

    /**
     * 获取家庭下的场景列表
     * @param homeId 家庭id
     * @param ownerUid 家庭所有者id
     */
    suspend fun getScenes(homeId: Long, ownerUid: Long): Result<MiotScenes>

    /**
     * 获取家庭下的设备列表
     * @param homeId 家庭id
     * @param ownerUid 家庭所有者id
     * @param limit 返回数量
     */
    suspend fun getDevices(
        homeId: Long,
        ownerUid: Long,
        limit: Int = 200
    ): Result<MiotDevices>

    /**
     * 运行场景
     * @param home
     * @param scene
     */
    suspend fun runScene(home: MiotHome, scene: MiotScene): Result<Unit>

    /**
     * 运行场景
     * @param homeId 家庭id
     * @param ownerUid 家庭所有者id
     * @param scene
     */
    suspend fun runScene(homeId: Long, ownerUid: Long, scene: MiotScene): Result<Unit>
}