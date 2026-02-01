package miwu.miot.kmp.service

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import miwu.miot.kmp.service.body.GetUserInfo
import miwu.miot.model.miot.MiotUserInfo

interface UserService {
    @POST("home/profile")
    suspend fun getUserInfo(@Body body: GetUserInfo): MiotUserInfo
}