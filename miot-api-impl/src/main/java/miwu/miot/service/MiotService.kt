package miwu.miot.service

import miwu.miot.model.att.Action
import miwu.miot.model.att.DeviceAtt
import miwu.miot.model.miot.MiotDevices
import miwu.miot.model.miot.MiotHomes
import miwu.miot.model.miot.MiotScenes
import miwu.miot.model.miot.MiotUserInfo
import miwu.miot.service.body.ActionBody
import miwu.miot.service.body.GetDevices
import miwu.miot.service.body.GetHome
import miwu.miot.service.body.GetParams
import miwu.miot.service.body.GetScene
import miwu.miot.service.body.GetUserInfo
import miwu.miot.service.body.RunCommonScene
import miwu.miot.service.body.RunScene
import miwu.miot.service.body.SetParams
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface MiotService {

    @POST("v2/homeroom/gethome")
    suspend fun getHomes(@Body body: GetHome = GetHome()): MiotHomes

    @POST("v2/home/home_device_list")
    suspend fun getDevices(@Body body: GetDevices): MiotDevices

    @POST("appgateway/miot/appsceneservice/AppSceneService/GetCommonUsedSceneList")
    suspend fun getScenes(@Body body: GetScene): MiotScenes

    /**
     * 两个 runScene 方法使用根据获取他们的 icon 链接是否为空
     * icon 不为空使用 RunCommonScene
     * icon 为空使用 RunScene
     */

    @POST("appgateway/miot/appsceneservice/AppSceneService/RunScene")
    suspend fun runScene(@Body body: RunCommonScene): ResponseBody

    @POST("scene/start")
    suspend fun runScene(@Body body: RunScene): ResponseBody

    @POST("miotspec/prop/set")
    suspend fun setDeviceAtt(@Body body: SetParams): ResponseBody

    @POST("miotspec/prop/get")
    suspend fun getDeviceAtt(@Body body: GetParams): DeviceAtt

    @POST("miotspec/action")
    suspend fun doAction(@Body body: ActionBody): Action

    @POST("home/profile")
    suspend fun getUserInfo(@Body body: GetUserInfo): MiotUserInfo
}