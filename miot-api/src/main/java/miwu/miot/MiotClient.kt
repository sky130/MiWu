package miwu.miot

import miwu.miot.att.get.GetAtt
import miwu.miot.att.set.SetAtt
import miwu.miot.model.MiotUser
import miwu.miot.model.att.DeviceAtt
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotDevices
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotHomes
import miwu.miot.model.miot.MiotScene
import miwu.miot.model.miot.MiotScenes
import miwu.miot.model.miot.MiotUserInfo

interface MiotClient {

    /**
     * 获取用户信息
     */
    suspend fun getUserInfo(): Result<MiotUserInfo>

    /**
     * 返回当前 `MiotUser` 的 `Token` 是否过期
     */
    suspend fun checkTokenValid(): Result<Boolean>

    val Home: IMiotClientHome
    val Device: IMiotClientDevice

    fun setUser(user: MiotUser)

    interface IMiotClientHome {
        /**
         * 获取家庭列表
         */
        suspend fun getHomes(
            fetchShare: Boolean = true,
            fetchShareDev: Boolean = true,
            appVer: Int = 7,
            limit: Int = 300,
        ): Result<MiotHomes>

        /**
         * 获取家庭下的设备列表
         */
        suspend fun getDevices(
            home: MiotHome, limit: Int = 200,
        ): Result<MiotDevices>

        /**
         * 获取家庭下的场景列表
         */
        suspend fun getScenes(
            home: MiotHome,
        ): Result<MiotScenes>

        /**
         * 获取家庭下的场景列表（通过 homeId）
         */
        suspend fun getScenes(
            homeId: Long
        ): Result<MiotScenes>

        /**
         * 获取家庭下的设备列表（通过 homeOwnerId 和 homeId）
         */
        suspend fun getDevices(
            homeOwnerId: Long, homeId: Long, limit: Int = 200,
        ): Result<MiotDevices>

        /**
         * 运行场景
         */
        suspend fun runScene(scene: MiotScene)
    }

    interface IMiotClientDevice {
        /**
         * 获取设备属性
         */
        suspend fun get(device: MiotDevice, att: Array<out GetAtt>): Result<DeviceAtt>

        /**
         * 设置设备属性
         */
        suspend fun set(device: MiotDevice, att: Array<out SetAtt>): Result<Unit>

        /**
         * 执行动作
         */
        suspend fun action(
            device: MiotDevice, siid: Int, aiid: Int, vararg obj: Any
        ): Result<Any?> // 可替换为具体的 Action 返回类型

        /**
         * 获取带语言的设备属性
         */
        suspend fun getSpecAttWithLanguage(
            device: MiotDevices.Result.Device,
            languageCode: String = "zh_cn"
        ): Result<Any?> // 可替换为具体的 SpecAtt 类型

        /**
         * 获取设备的图标 Url
         * @param model 设备型号
         * @return 图标 Url, 失败返回 null
         */
        suspend fun getIconUrl(model: String): Result<String>
    }
}
