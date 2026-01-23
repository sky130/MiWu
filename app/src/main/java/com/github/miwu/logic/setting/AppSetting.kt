package com.github.miwu.logic.setting

import kndroidx.setting.Setting
import miwu.miot.common.getRandomDeviceId
import miwu.miot.model.MiotUser

object AppSetting : Setting("app_v2") {

    val homeId = long("homeId", 0L)

    val ownerId = long("ownerId", 0L)

    val isCrash = boolean("isCrash", false)

}