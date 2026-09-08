package com.github.miwu.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface DeviceIconRepository {
    val icons: StateFlow<Map<String, ByteArray>>
}
