package miwu.miot.client

import miwu.miot.model.miot.MiotUserInfo

interface MiotUserClient {

    /**
     * 获取用户信息
     */
    suspend fun getUserInfo(): Result<MiotUserInfo>

    /**
     * 检查当前 `MiotUser` 的 `Token` 是否过期
     */
    suspend fun checkTokenExpired(): Result<Boolean>

}