package miwu.miot.model.att

import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull
import miwu.miot.model.JsonAnySerializer

@Serializable
data class DeviceAtt(
    @SerialName("code") val code: Int,
    @SerialName("message") val message: String,
    @SerialName("result") val result: ArrayList<Att>? = null,
) {
    @Serializable
    data class Att(
        @SerialName("did") val did: String,
        @SerialName("iid") val iid: String,
        @SerialName("siid") val siid: Int,
        @SerialName("piid") val piid: Int,
        @SerialName("value") val value: @Serializable(with = JsonAnySerializer::class) Any? = null,
        @SerialName("code") val code: Int,
        @SerialName("updateTime") val updateTime: Long? = null,
        @SerialName("exe_time") val exeTime: Int,
    )
}
