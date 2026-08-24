package miwu.miot.model

import miwu.miot.exception.MiotBusinessException
import miwu.miot.model.att.ActionList
import miwu.miot.model.att.PropertyList

fun <T> MiotResponse<T>.requireSuccess(operation: String): MiotResponse<T> {
    if (code != 0) throw MiotBusinessException(code, "$operation failed: $message")
    return this
}

fun MiotResponse<PropertyList?>.requirePropertySuccess(operation: String): MiotResponse<PropertyList?> {
    requireSuccess(operation)
    result.orEmpty().firstOrNull { it.code != 0 }?.let {
        throw MiotBusinessException(it.code, "$operation failed for siid=${it.siid}, piid=${it.piid}")
    }
    return this
}

fun MiotResponse<ActionList?>.actionOutputOrUnit(operation: String): Any {
    requireSuccess(operation)
    val actions = result.orEmpty()
    actions.firstOrNull { it.code != 0 }?.let {
        throw MiotBusinessException(it.code, "$operation failed for siid=${it.siid}, aiid=${it.aiid}")
    }
    return actions.firstOrNull()?.out?.takeIf { it.isNotEmpty() } ?: Unit
}
