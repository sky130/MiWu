package miwu.miot

import kotlinx.coroutines.CoroutineScope
import miwu.miot.model.MiotUser
import kotlin.coroutines.CoroutineContext
import miwu.miot.model.login.LoginQrCode

interface MiotLoginClient {

    /**
     * 账号密码登录
     * @param user 用户名
     * @param pwd 密码
     * @return 登录成功返回 MiotUser，否则返回 null
     */
    suspend fun login(user: String, pwd: String): MiotUser?

    /**
     * 扫码登录（简易版）
     * @param loginUrl 扫码登录url
     * @return 登录成功返回 MiotUser，否则返回 null
     */
    suspend fun loginByQrCode(loginUrl: String): MiotUser?

    /**
     * 扫码登录（带回调）
     * @param loginUrl 扫码登录url
     * @param onSuccess 登录成功回调
     * @param onTimeout 超时回调
     * @param onFailure 失败回调
     * @param context 回调所用协程上下文，默认 Dispatchers.Default
     */
    suspend fun loginByQrCode(
        loginUrl: String,
        onSuccess: suspend CoroutineScope.(MiotUser) -> Unit,
        onTimeout: suspend CoroutineScope.() -> Unit,
        onFailure: suspend CoroutineScope.(Throwable?) -> Unit,
        context: CoroutineContext = kotlinx.coroutines.Dispatchers.Default
    )

    /**
     * 生成二维码
     * @return 登录二维码相关信息
     */
    suspend fun generateLoginQrCode(): LoginQrCode
}
