package miwu.miot.service

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
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
import miwu.miot.service.body.RunNewScene
import miwu.miot.service.body.RunScene
import miwu.miot.service.body.SetParams

interface MiotService {

    @POST("v2/homeroom/gethome")
    suspend fun getHomes(@Body body: GetHome = GetHome()): MiotHomes

    @POST("v2/home/home_device_list")
    suspend fun getDevices(@Body body: GetDevices): MiotDevices

    /**
     * @POST("appgateway/miot/appsceneservice/AppSceneService/GetCommonUsedSceneList")
     * suspend fun getScenes(@Body body: GetScene): MiotScenes
     *
     * @POST("appgateway/miot/appsceneservice/AppSceneService/RunScene")
     * suspend fun runScene(@Body body: RunCommonScene): ResponseBody
     **/

    @POST("appgateway/miot/appsceneservice/AppSceneService/GetSimpleSceneList")
    suspend fun getScenes(@Body body: GetScene): MiotScenes


    @POST("appgateway/miot/appsceneservice/AppSceneService/NewRunScene")
    suspend fun runScene(@Body body: RunNewScene): String

    /**
     * 两个 runScene 方法使用根据获取他们的 icon 链接是否为空
     * icon 不为空使用 RunCommonScene
     * icon 为空使用 RunScene
     */
    @Deprecated("api 接口残缺", replaceWith = ReplaceWith("runScene(RunNewScene)"))
    @POST("appgateway/miot/appsceneservice/AppSceneService/RunScene")
    suspend fun runScene(@Body body: RunCommonScene)

    @Deprecated("api 接口残缺", replaceWith = ReplaceWith("runScene(RunNewScene)"))
    @POST("scene/start")
    suspend fun runScene(@Body body: RunScene)

    @POST("miotspec/prop/set")
    suspend fun setDeviceAtt(@Body body: SetParams): String

    @POST("miotspec/prop/get")
    suspend fun getDeviceAtt(@Body body: GetParams): DeviceAtt

    @POST("miotspec/action")
    suspend fun doAction(@Body body: ActionBody): Action

    @POST("home/profile")
    suspend fun getUserInfo(@Body body: GetUserInfo): MiotUserInfo
}