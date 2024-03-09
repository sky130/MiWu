package miot.kotlin.model.login

import com.google.gson.annotations.SerializedName

data class Login(
    @SerializedName("code") val code:Int,
    @SerializedName("desc") val desc:String,
    @SerializedName("location") val location:String,
    @SerializedName("userId") val userId:String,
    @SerializedName("ssecurity") val securityToken:String
)
