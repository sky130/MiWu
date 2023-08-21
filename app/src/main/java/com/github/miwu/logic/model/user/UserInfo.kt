package com.github.miwu.logic.model.user

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("userid")
    val uid: String, // 用户Id
    @SerializedName("icon")
    val avatar: String, // 用户头像
    @SerializedName("nickname")
    val nickname: String, // 用户昵称
)
