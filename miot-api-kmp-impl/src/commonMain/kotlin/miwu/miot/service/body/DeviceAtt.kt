package miwu.miot.service.body

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import miwu.miot.model.JsonAnySerializer

@Serializable
data class SetParams(@SerialName("params") val params: Array<Att>) {
    @Serializable
    data class Att(
        @SerialName("did") val did: String,
        @SerialName("siid") val siid: Int,
        @SerialName("piid") val piid: Int,
        @SerialName("value") val value: @Serializable(with = JsonAnySerializer::class) Any
    )


    override fun hashCode(): Int {
        return params.contentHashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as SetParams
        return params.contentEquals(other.params)
    }
}


@Serializable
data class GetParams(@SerialName("params") val params: Array<Att>) {
    @Serializable
    data class Att(
        @SerialName("did") val did: String,
        @SerialName("siid") val siid: Int,
        @SerialName("piid") val piid: Int,
    )

    override fun hashCode(): Int {
        return params.contentHashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as GetParams
        return params.contentEquals(other.params)
    }
}
