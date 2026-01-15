package miwu.miot.kmp.service

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import miwu.miot.kmp.service.body.ActionBody
import miwu.miot.kmp.service.body.GetDevices
import miwu.miot.kmp.service.body.GetHome
import miwu.miot.kmp.service.body.GetParams
import miwu.miot.kmp.service.body.GetScene
import miwu.miot.kmp.service.body.GetUserInfo
import miwu.miot.kmp.service.body.RunCommonScene
import miwu.miot.kmp.service.body.RunNewScene
import miwu.miot.kmp.service.body.RunScene
import miwu.miot.kmp.service.body.SetParams
import miwu.miot.model.att.Action
import miwu.miot.model.att.DeviceAtt
import miwu.miot.model.miot.MiotDevices
import miwu.miot.model.miot.MiotHomes
import miwu.miot.model.miot.MiotScenes
import miwu.miot.model.miot.MiotUserInfo

interface HomeService {

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
}