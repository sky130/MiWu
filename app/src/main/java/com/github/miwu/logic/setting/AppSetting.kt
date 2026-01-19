package com.github.miwu.logic.setting

import kndroidx.setting.Setting
import miwu.miot.common.getRandomDeviceId
import miwu.miot.model.MiotUser

object AppSetting : Setting("app") {

    val userId = string("userId", "")

    val cUserId = string("cUserId", "")

    val nonce = string("nonce", "")

    val ssecurity = string("ssecurity", "")

    val psecurity = string("psecurity", "")

    val passToken = string("passToken", "")

    val serviceToken = string("serviceToken", "")

    val deviceId = string("deviceId", getRandomDeviceId())

    val homeId = long("homeId", 0L)

    val homeUid = long("homeUid", 0L)

    val isCrash = boolean("isCrash", false)

    fun update(miotUser: MiotUser) {
        userId.value = miotUser.userId
        cUserId.value = miotUser.cUserId
        nonce.value = miotUser.nonce
        ssecurity.value = miotUser.ssecurity
        psecurity.value = miotUser.psecurity
        passToken.value = miotUser.passToken
        serviceToken.value = miotUser.serviceToken
    }

}