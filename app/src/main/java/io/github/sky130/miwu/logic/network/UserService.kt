package io.github.sky130.miwu.logic.network.miot

import io.github.sky130.miwu.logic.model.user.LoginMsg
import io.github.sky130.miwu.logic.model.user.SidMsg
import io.github.sky130.miwu.util.OkHttpUtils
import okhttp3.FormBody
import org.json.JSONObject
import java.security.MessageDigest
import java.util.Locale


object UserService {

    fun login(user: String, pwd: String): LoginMsg? {
        val sidMsg = getMsg(MiotData.MISID) ?: return null
        val url = "https://account.xiaomi.com/pass/serviceLoginAuth2"
        val pwdHash = MessageDigest.getInstance("MD5").digest(pwd.toByteArray())
            .joinToString("") { "%02x".format(it) }.uppercase(Locale.ROOT).padEnd(32, '0')
        val body = FormBody.Builder().apply {
            sidMsg.apply {
                add("qs", this.qs)
                add("sid", this.sid)
                add("_sign", this.sign)
                add("callback", this.callback)
            }
            add("user", user)
            add("hash", pwdHash)
            add("_json", "true")
        }.build()
        val result = OkHttpUtils.getRequest(url, body) ?: return null
        JSONObject(result.substring(11)).apply {
            val code = getInt("code")
            var message = getStr("desc")
            val loginMsg = LoginMsg(code != 0, code.toString(), message)
            if (loginMsg.isError) return loginMsg
            loginMsg.apply {
                val response = OkHttpUtils.getRequestResponse(getStr("location"))
                val setCookieHeader = response.headers["Set-Cookie"] ?: return null
                val cookies = setCookieHeader.split(", ")
                for (item in cookies) {
                    val parts = item.split("; ")[0].split("=", limit = 2)
                    if (parts.size == 2) {
                        loginMsg.serviceToken = parts[1]
                    }
                }
                sid = MiotData.MISID
                userId = getStr("userId")
                securityToken = getStr("ssecurity")
                deviceId = MiotData.getRandomDeviceId()
                message = "success"
            }
            return loginMsg
        }
    }

    fun getMsg(sid: String): SidMsg? {
        val url = "https://account.xiaomi.com/pass/serviceLogin?sid=$sid&_json=true"
        val result = OkHttpUtils.getRequest(url) ?: return null
        JSONObject(result.substring(11)).apply {
            return SidMsg(
                getStr("qs"),
                getStr("sid"),
                getStr("_sign"),
                getStr("callback"),
            )
        }
    }

    private fun JSONObject.getStr(key: String) = this.getString(key)
}