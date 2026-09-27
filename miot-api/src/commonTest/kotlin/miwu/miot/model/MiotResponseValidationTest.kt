package miwu.miot.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.serialization.json.Json
import miwu.miot.exception.MiotBusinessException
import miwu.miot.model.att.Action
import miwu.miot.model.att.ActionList
import miwu.miot.model.att.Property
import miwu.miot.model.att.PropertyList

class MiotResponseValidationTest {
    @Test
    fun rejectsTopLevelAndEntryErrors() {
        assertFailsWith<MiotBusinessException> {
            MiotResponse<PropertyList>(1, "denied", PropertyList()).requirePropertySuccess("get")
        }
        assertFailsWith<MiotBusinessException> {
            MiotResponse<PropertyList>(
                0,
                result = arrayListOf(Property("did", "", 2, 3, code = -1, exeTime = 0))
            ).requirePropertySuccess("get")
        }
    }

    @Test
    fun actionReturnsOutputsOrUnit() {
        val actions: ActionList = arrayListOf(Action("did", "", 2, 1, arrayListOf(42), 0, 0))
        assertEquals(
            listOf(42),
            MiotResponse<ActionList>(0, result = actions).actionOutputOrUnit("action")
        )
        assertEquals(
            Unit,
            MiotResponse<ActionList>(0, result = ActionList()).actionOutputOrUnit("action")
        )
    }

    @Test
    fun nullResultPreservesBusinessError() {
        val response = Json.decodeFromString<MiotResponse<PropertyList>>(
            """{"code":-1,"message":"no permit","result":null}"""
        )

        val error = assertFailsWith<MiotBusinessException> {
            response.requireSuccess("Get devices")
        }

        assertEquals(-1, error.code)
        assertEquals("Get devices failed: no permit", error.message)
    }

    @Test
    fun successfulResponseRequiresResult() {
        val response = Json.decodeFromString<MiotResponse<PropertyList>>(
            """{"code":0,"message":"ok","result":null}"""
        )

        val error = assertFailsWith<MiotBusinessException> {
            response.requireSuccess("Get devices")
        }

        assertEquals(0, error.code)
        assertEquals("Get devices failed: response result is missing", error.message)
    }
}
