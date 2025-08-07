package com.github.miwu.logic.setting

import kndroidx.setting.Setting
import miwu.miot.utils.getRandomDeviceId

object AppSetting : Setting("app") {

    var userId by string("userId", "")

    var securityToken by string("securityToken", "")

    var serviceToken by string("serviceToken", "")

    val deviceId by string("deviceId", getRandomDeviceId())

    var homeId by long("homeId", 0L)

    var homeUid by long("homeUid", 0L)

    var isCrash by boolean("isCrash", false)

}