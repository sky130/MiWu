package miwu.miot.model

import miwu.miot.exception.MiotBusinessException
import miwu.miot.model.att.ActionList
import miwu.miot.model.att.PropertyList

fun <T> MiotResponse<T>.requireSuccess(operation: String): MiotSuccess<T> {
    if (code != 0) throw MiotBusinessException(code, "$operation failed: $message")
    return MiotSuccess(
        result ?: throw MiotBusinessException(
            code = code,
            message = "$operation failed: response result is missing",
        )
    )
}

fun MiotResponse<PropertyList>.requirePropertySuccess(operation: String): MiotSuccess<PropertyList> {
    val success = requireSuccess(operation)
    success.result.firstOrNull { it.code != 0 }?.let {
        throw MiotBusinessException(it.code, "$operation failed for siid=${it.siid}, piid=${it.piid}")
    }
    return success
}

fun MiotResponse<ActionList>.actionOutputOrUnit(operation: String): Any {
    val actions = requireSuccess(operation).result
    actions.firstOrNull { it.code != 0 }?.let {
        throw MiotBusinessException(it.code, "$operation failed for siid=${it.siid}, aiid=${it.aiid}")
    }
    return actions.firstOrNull()?.out?.takeIf { it.isNotEmpty() } ?: Unit
}
