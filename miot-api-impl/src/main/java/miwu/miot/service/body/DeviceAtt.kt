package miwu.miot.service.body

import com.google.gson.annotations.SerializedName

data class SetParams(@SerializedName("params") val params: Array<Att>){
    data class Att(
        @SerializedName("did") val did: String,
        @SerializedName("siid") val siid: Int,
        @SerializedName("piid") val piid: Int,
        @SerializedName("value") val value: Any
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SetParams

        return params.contentEquals(other.params)
    }

    override fun hashCode(): Int {
        return params.contentHashCode()
    }
}


data class GetParams(@SerializedName("params") val params: Array<Att>){
    data class Att(
        @SerializedName("did") val did: String,
        @SerializedName("siid") val siid: Int,
        @SerializedName("piid") val piid: Int,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as GetParams
        return params.contentEquals(other.params)
    }

    override fun hashCode(): Int {
        return params.contentHashCode()
    }
}
