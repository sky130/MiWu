package miwu.miot.client

import miwu.miot.att.get.GetAtt
import miwu.miot.model.MiotUser
import miwu.miot.model.att.DeviceAtt
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotDevices
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotHomes
import miwu.miot.model.miot.MiotScene
import miwu.miot.model.miot.MiotScenes

/**
 * 提供用于获取和管理家庭、设备以及场景的方法。
 * 包括获取家庭列表、家庭下的设备和场景列表，以及执行场景等功能。
 *
 * 通常通过依赖注入框架（如 [Koin]）获取实例。使用时需要提供有效的 [MiotUser] 身份凭证
 *
 * 下面是使用 Koin 来进行依赖注入的例子
 *
 * ``` kt
 * val user: MiotUser = ...
 * val client: MiotHomeClient = get<MiotHomeClient> { parametersOf(user) }
 * val home: MiotHome = client.getHomes().getOrThrow()
 * for (item in home.result.homes) {
 *     println("${item.id} ${item.name}")
 * }
 * ```
 *
 * @see [MiotUser] 用户身份信息
 * @see [MiotHomes] 家庭详情列表
 * @see [MiotDevices] 设备详情列表
 * @see [MiotScenes] 情景详情列表
 *
 */
interface MiotHomeClient {

    /**
     * 异步获取用户账户下的所有家庭列表（包括自己创建的和他人共享的）。
     *
     * @param fetchShare 是否获取共享的家庭列表。
     * @param fetchShareDev 是否获取共享家庭中的设备列表。
     * @param appVer 应用版本号，可能会影响API返回的数据格式，默认值为7。
     * @param limit 期望返回的家庭最大数量。
     * @return 一个包含家庭列表 [MiotHomes] 的 [Result] 对象。
     */
    suspend fun getHomes(
        fetchShare: Boolean = true,
        fetchShareDev: Boolean = true,
        appVer: Int = 7,
        limit: Int = 300,
    ): Result<MiotHomes>

    /**
     * 异步获取指定家庭下的所有设备列表。
     * 这是 [getDevices] 的一个便捷重载方法，通过传递完整的家庭对象来获取。
     *
     * @param home 目标家庭对象 [MiotHome]。
     * @param limit 期望返回的设备最大数量。
     * @return 一个包含设备列表 [MiotDevices] 的 [Result] 对象。
     */
    suspend fun getDevices(home: MiotHome, limit: Int = 200): Result<MiotDevices>

    /**
     * 异步获取指定家庭下的所有智能场景列表。
     * 这是 [getScenes] 的一个便捷重载方法，通过传递完整的家庭对象来获取。
     *
     * @param home 目标家庭对象 [MiotHome]。
     * @return 一个包含场景列表 [MiotScenes] 的 [Result] 对象。
     */
    suspend fun getScenes(home: MiotHome): Result<MiotScenes>

    /**
     * 异步获取指定家庭下的所有智能场景列表。
     *
     * @param homeId 目标家庭的唯一标识ID。
     * @param ownerUid 该家庭所有者的用户ID。
     * @return 一个包含场景列表 [MiotScenes] 的 [Result] 对象。
     */
    suspend fun getScenes(homeId: Long, ownerUid: Long): Result<MiotScenes>

    /**
     * 异步获取指定家庭下的所有设备列表。
     *
     * @param homeId 目标家庭的唯一标识ID。
     * @param ownerUid 该家庭所有者的用户ID。
     * @param limit 期望返回的设备最大数量。
     * @return 一个包含设备列表 [MiotDevices] 的 [Result] 对象。
     */
    suspend fun getDevices(
        homeId: Long,
        ownerUid: Long,
        limit: Int = 200
    ): Result<MiotDevices>

    /**
     * 异步执行指定的智能场景。
     * 这是 [runScene] 的一个便捷重载方法，通过传递完整的家庭和场景对象来执行。
     *
     * @param home 场景所在的家庭对象 [MiotHome]。
     * @param scene 需要执行的目标场景对象 [MiotScene]。
     * @return 一个 [Result] 对象，成功时包含 [Unit]，表示操作完成。
     */
    suspend fun runScene(home: MiotHome, scene: MiotScene): Result<Unit>

    /**
     * 异步执行指定的智能场景。
     *
     * @param homeId 场景所在的家庭ID。
     * @param ownerUid 该家庭所有者的用户ID。
     * @param scene 需要执行的目标场景对象 [MiotScene]。
     * @return 一个 [Result] 对象，成功时包含 [Unit]，表示操作完成。
     */
    suspend fun runScene(homeId: Long, ownerUid: Long, scene: MiotScene): Result<Unit>
}