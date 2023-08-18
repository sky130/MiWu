package io.github.sky130.miwu.logic.dao

import android.content.Context
import io.github.sky130.miwu.MainApplication.Companion.context
import io.github.sky130.miwu.logic.model.user.LoginMsg
import io.github.sky130.miwu.util.SettingUtils


object UserDAO {

    private val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)!!

    private val edit = SettingUtils(sharedPreferences)

    fun getLocalUser(): LoginMsg {
        edit.apply {
            return LoginMsg(
                false,
                get("code", ""),
                get("message", ""),
                get("sid", ""),
                get("userId", ""),
                get("securityToken", ""),
                get("deviceId", ""),
                get("serviceToken", "")
            )
        }
    }

    fun saveUser(loginMsg: LoginMsg) {
        edit.apply {
            loginMsg.apply {
                put("code", code)
                put("message", message)
                put("sid", sid)
                put("userId", userId)
                put("securityToken", securityToken)
                put("deviceId", deviceId)
                put("serviceToken", serviceToken)
            }
        }
    }

}