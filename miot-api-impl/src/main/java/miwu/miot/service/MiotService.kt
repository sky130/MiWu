package miwu.miot.service

import miwu.miot.model.att.*
import miwu.miot.service.body.*
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface MiotService {
    @POST("miotspec/prop/set")
    suspend fun setDeviceAtt(@Body body: SetParams): ResponseBody

    @POST("miotspec/prop/get")
    suspend fun getDeviceAtt(@Body body: GetParams): DeviceAtt

    @POST("miotspec/action")
    suspend fun doAction(@Body body: ActionBody): Action
}