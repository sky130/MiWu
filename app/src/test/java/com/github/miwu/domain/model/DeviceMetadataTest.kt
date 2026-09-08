package com.github.miwu.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DeviceMetadataTest {
    @Test
    fun returnsStoredMetadata() {
        val metadata = DeviceMetadata(
            icons = mapOf("model" to "https://example.com/icon.png"),
            rooms = mapOf("did" to "客厅"),
        )

        assertEquals("https://example.com/icon.png", metadata.getIcon("model"))
        assertEquals("客厅", metadata.getRoom("did"))
    }

    @Test
    fun returnsFallbacksForMissingMetadata() {
        val metadata = DeviceMetadata()

        assertNull(metadata.getIcon("missing"))
        assertEquals("未知位置", metadata.getRoom("missing"))
    }
}
