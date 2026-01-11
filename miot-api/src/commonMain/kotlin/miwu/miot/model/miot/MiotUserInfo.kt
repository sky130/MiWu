package miwu.miot.model.miot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MiotUserInfo(
    @SerialName("code") val code: Int,
    @SerialName("result") val info: UserInfo,
) {
    @Serializable
    data class UserInfo(
        @SerialName("userid") val uid: Long,
        @SerialName("icon") val avatar: String,
        @SerialName("nickname") val nickname: String,
    )
}