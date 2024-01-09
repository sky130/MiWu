package miot.kotlin.service

import miot.kotlin.model.att.DeviceAtt
import miot.kotlin.model.miot.MiotDevices
import miot.kotlin.model.miot.MiotHomes
import miot.kotlin.model.miot.MiotScenes
import miot.kotlin.service.body.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MiotService {

    @POST("v2/homeroom/gethome")
    fun getHomes(@Body body: GetHome = GetHome()): Call<MiotHomes>

    @POST("v2/home/home_device_list")
    fun getDevices(@Body body: GetDevices): Call<MiotDevices>

    @POST("appgateway/miot/appsceneservice/AppSceneService/GetCommonUsedSceneList")
    fun getScenes(@Body body: GetScene): Call<MiotScenes>

    /**
     * 两个 runScene 方法使用根据获取他们的 icon 链接是否为空
     * icon 不为空使用 RunCommonScene
     * icon 为空使用 RunScene
     */

    @POST("appgateway/miot/appsceneservice/AppSceneService/RunScene")
    fun runScene(@Body body: RunCommonScene): Call<ResponseBody>

    @POST("scene/start")
    fun runScene(@Body body: RunScene): Call<ResponseBody>

    @POST("miotspec/prop/set")
    fun setDeviceAtt(@Body body: SetParams): Call<ResponseBody>

    @POST("miotspec/prop/get")
    fun getDeviceAtt(@Body body: GetParams): Call<DeviceAtt>
}