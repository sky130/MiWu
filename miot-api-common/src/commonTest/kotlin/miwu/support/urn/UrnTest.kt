package miwu.support.urn

import kotlin.test.Test
import kotlin.test.assertEquals

class UrnTest {
    @Test
    fun parseAndToStringRoundTrip() {
        listOf(
            "urn:miot-spec-v2:service:device-information:00007801",
            "urn:miot-spec-v2:property:on:00000001:light:00000001",
        ).forEach { value ->
            assertEquals(value, Urn.parseFrom(value).toString())
        }

        val device = Urn.parseFrom(
            "urn:miot-spec-v2:device:light:0000A001:bull-jld01:1:0000C802"
        )
        assertEquals(1, device.version)
        assertEquals("0000C802", device.revision)
        assertEquals(
            "urn:miot-spec-v2:device:light:0000A001:bull-jld01:00000001:0000C802",
            device.toString()
        )
    }
}
