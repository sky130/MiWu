package com.github.miwu.logic.setting

import kndroidx.setting.Setting
import miwu.miot.common.getRandomDeviceId

object AppSetting : Setting("app") {

    val userId = string("userId", "")

    val securityToken = string("securityToken", "")

    val serviceToken = string("serviceToken", "")

    val deviceId = string("deviceId", getRandomDeviceId())

    val homeId = long("homeId", 0L)

    val homeUid = long("homeUid", 0L)

    val isCrash = boolean("isCrash", false)

}