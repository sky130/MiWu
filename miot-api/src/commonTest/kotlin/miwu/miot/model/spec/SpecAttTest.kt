package miwu.miot.model.spec

import kotlin.test.Test
import kotlin.test.assertEquals

class SpecAttTest {
    @Test
    fun defaultValueUsesFormatAndValidatedRange() {
        assertEquals(5, property("int32", listOf(5L, 10L, 1L)).getDefaultValue())
        assertEquals(5f, property("float", listOf(5, 10, 1)).getDefaultValue())
        assertEquals(0, property("int32", listOf("bad")).getDefaultValue())
        assertEquals(false, property("bool", null).getDefaultValue())
    }

    private fun property(format: String, range: List<Any>?) = SpecAtt.Property(
        access = emptyList(),
        description = "test",
        format = format,
        iid = 1,
        type = "urn:miot-spec-v2:property:test:00000001",
        valueRange = range,
    )
}
