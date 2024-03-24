package miot.kotlin.model.login

import com.google.gson.annotations.SerializedName

data class Login(
    @SerializedName("code") val code:Int,
    @SerializedName("desc") val desc:String,
    @SerializedName("location") var location:String,
    @SerializedName("userId") val userId:String,
    @SerializedName("ssecurity") var securityToken:String
)
