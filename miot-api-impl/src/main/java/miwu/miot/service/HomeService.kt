package miwu.miot.service

import miwu.miot.model.miot.*
import miwu.miot.service.body.*
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

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
    suspend fun runScene(@Body body: RunNewScene): ResponseBody

    /**
     * 两个 runScene 方法使用根据获取他们的 icon 链接是否为空
     * icon 不为空使用 RunCommonScene
     * icon 为空使用 RunScene
     */
    @Deprecated("api 接口残缺", replaceWith = ReplaceWith("runScene(RunNewScene)"))
    @POST("appgateway/miot/appsceneservice/AppSceneService/RunScene")
    suspend fun runScene(@Body body: RunCommonScene): ResponseBody

    @Deprecated("api 接口残缺", replaceWith = ReplaceWith("runScene(RunNewScene)"))
    @POST("scene/start")
    suspend fun runScene(@Body body: RunScene): ResponseBody

}