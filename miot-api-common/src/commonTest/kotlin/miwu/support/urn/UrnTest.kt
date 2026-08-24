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
    }
}
