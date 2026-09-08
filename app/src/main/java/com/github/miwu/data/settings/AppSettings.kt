package com.github.miwu.data.settings

import kndroidx.setting.Setting

internal object AppSettings : Setting("app_v2") {
    val homeId = long("homeId", 0L)
    val ownerId = long("ownerId", 0L)
    val isCrash = boolean("isCrash", false)
}
