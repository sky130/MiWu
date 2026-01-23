package miwu.miot.client

import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotUserInfo

/**
 * 用于获取米家用户信息以及管理用户状态。
 *
 * 通常通过依赖注入框架（如 [Koin]）获取实例。使用时需要提供有效的 [MiotUser] 身份凭证
 *
 * 下面是使用 Koin 来进行依赖注入的例子
 *
 * ``` kt
 * val user: MiotUser = ...
 * val client: MiotUserClient = get<MiotUserClient> { parametersOf(user) }
 * val info: MiotUserInfo = client.getUserInfo().getOrThrow()
 * println(info.result.nickname)
 * ```
 *
 * @see [MiotUser] 用户身份信息
 * @see [MiotUserInfo] 用户信息
 */
interface MiotUserClient {

    /**
     * 获取用户信息
     *
     * @return 包含 [MiotUserInfo] 的 [Result] 对象
     */
    suspend fun getUserInfo(): Result<MiotUserInfo>

    /**
     * 检查当前用户的 `serviceToken` 是否已过期。
     *
     * @return 包含布尔值的 [Result] 对象
     */
    suspend fun checkTokenExpired(): Result<Boolean>

}
