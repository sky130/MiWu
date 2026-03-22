package com.github.miwu.logic.repository.entity

import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotHomes
import miwu.miot.model.miot.MiotRoom
import miwu.miot.model.miot.MiotScene

data class MiotHomeData(
    val home: MiotHome,
    val rooms: Map<String, List<MiotDevice>>,
    val scenes: List<MiotScene>,
    val devices: List<MiotDevice>,
    val roomMap: Map<String, MiotRoom>
)
