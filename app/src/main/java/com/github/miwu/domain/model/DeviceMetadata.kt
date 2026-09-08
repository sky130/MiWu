package com.github.miwu.domain.model

data class DeviceMetadata(
    val icons: Map<String, String> = emptyMap(),
    val rooms: Map<String, String> = emptyMap(),
) {
    fun getRoom(did: String): String = rooms[did] ?: UNKNOWN_ROOM

    fun getIcon(model: String): String? = icons[model]

    private companion object {
        const val UNKNOWN_ROOM = "未知位置"
    }
}
