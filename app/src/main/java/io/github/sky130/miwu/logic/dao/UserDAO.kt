package io.github.sky130.miwu.logic.dao

import android.content.Context
import io.github.sky130.miwu.MainApplication.Companion.context
import io.github.sky130.miwu.MainApplication.Companion.loginMsg
import io.github.sky130.miwu.logic.dao.database.AppDatabase
import io.github.sky130.miwu.logic.model.user.LoginMsg
import io.github.sky130.miwu.logic.model.user.UserInfo
import io.github.sky130.miwu.logic.network.miot.UserService
import io.github.sky130.miwu.util.SettingUtils


object UserDAO {

    private val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)!!

    private val edit = SettingUtils(sharedPreferences)

    fun logout() {
        saveUserInfo(UserInfo("", "", ""))
        saveUser(LoginMsg(true, "", "", "", "", "", "", ""))
        AppDatabase.getDatabase().clearAllTables()
    }

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

    fun getLocalUserInfo(): UserInfo {
        edit.apply {
            return UserInfo(
                get("uid", ""),
                get("avatar", ""),
                get("nickname", ""),
            )
        }
    }

    fun saveUserInfo(info: UserInfo): UserInfo {
        edit.apply {
            put("uid",info.uid)
            put("avatar", info.avatar)
            put("nickname", info.nickname)
        }
        return info
    }


    // 在加载新信息的同时也保存到了本地
    fun getLatestUserInfo(): UserInfo {
        return saveUserInfo(UserService.getUserInfo(loginMsg.userId) ?: getLocalUserInfo())
    }

}