package miwu.miot.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.*

object JsonAnySerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Any")

    override fun serialize(encoder: Encoder, value: Any) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("This serializer only works with JSON")

        val jsonElement = when (value) {
            is Number -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is String -> JsonPrimitive(value)

            is List<*> -> JsonArray(value.map {
                it?.let { serializeToJsonElement(it) } ?: JsonPrimitive(null as String?)
            })

            is Map<*, *> -> JsonObject(value.mapNotNull { (k, v) ->
                if (k is String) k to (v?.let { serializeToJsonElement(it) }
                    ?: JsonPrimitive(null as String?))
                else null
            }.toMap())

            else -> throw SerializationException("Unsupported type: ${value::class}")
        }

        jsonEncoder.encodeJsonElement(jsonElement)
    }

    override fun deserialize(decoder: Decoder): Any {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("This serializer only works with JSON")
        val element = jsonDecoder.decodeJsonElement()
        return parseJsonElement(element)
    }

    private fun serializeToJsonElement(value: Any): JsonElement = when (value) {
        is Number -> JsonPrimitive(value)
        is Boolean -> JsonPrimitive(value)
        is String -> JsonPrimitive(value)
        is List<*> -> JsonArray(value.map {
            it?.let { serializeToJsonElement(it) } ?: JsonPrimitive(
                null as String?
            )
        })

        is Map<*, *> -> JsonObject(value.mapNotNull { (k, v) ->
            if (k is String) k to (v?.let { serializeToJsonElement(it) }
                ?: JsonPrimitive(null as String?))
            else null
        }.toMap())

        else -> throw SerializationException("Unsupported type: ${value::class}")
    }

    private fun parseJsonElement(element: JsonElement): Any = when {
        element is JsonNull -> throw SerializationException("Null value in JSON")

        element is JsonPrimitive -> with(element) {
            when {
                isString -> content
                intOrNull != null -> int
                booleanOrNull != null -> boolean
                longOrNull != null -> long
                doubleOrNull != null -> double
                floatOrNull != null -> float
                else -> content
            }
        }

        element is JsonArray -> element.map { parseJsonElement(it) }
        element is JsonObject -> element.mapValues { parseJsonElement(it.value) }
        else -> throw SerializationException("Unknown JSON element: $element")
    }
}

fun List<JsonElement>.asAny(): List<Any> = map { it.asAny() }

private fun JsonElement.asAny(): Any = when (this) {
    is JsonPrimitive -> {
        when {
            isString -> content
            intOrNull != null -> int
            booleanOrNull != null -> boolean
            longOrNull != null -> long
            doubleOrNull != null -> double
            floatOrNull != null -> float
            else -> content
        }
    }

    else -> throw IllegalStateException()
}