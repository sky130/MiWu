package miwu.miot.service

import miwu.miot.model.MiotResponse
import miwu.miot.model.miot.UserInfo
import miwu.miot.service.body.GetUserInfo
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("home/profile")
    suspend fun getUserInfo(@Body body: GetUserInfo): MiotResponse<UserInfo>
}