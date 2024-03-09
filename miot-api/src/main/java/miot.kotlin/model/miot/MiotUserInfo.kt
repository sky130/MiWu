package miot.kotlin.model.miot

import com.google.gson.annotations.SerializedName

data class MiotUserInfo(
    @SerializedName("code") val code: Int,
    @SerializedName("result") val info: UserInfo,
) {
    data class UserInfo(
        @SerializedName("userid") val uid: String,
        @SerializedName("icon") val avatar: String,
        @SerializedName("nickname") val nickname: String,
    )

}