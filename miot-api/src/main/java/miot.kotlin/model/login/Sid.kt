package miot.kotlin.model.login

import com.google.gson.annotations.SerializedName

data class Sid(
    @SerializedName("qs") val qs: String,
    @SerializedName("sid") val sid: String,
    @SerializedName("_sign") val sign: String,
    @SerializedName("callback") val callback: String,
    @SerializedName("location") val location:String,
    @SerializedName("ssecurity") val securityToken:String
)
