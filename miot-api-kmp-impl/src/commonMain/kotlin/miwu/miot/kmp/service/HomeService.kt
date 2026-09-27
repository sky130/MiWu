package miwu.miot.kmp.service

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import miwu.miot.model.request.GetDevices
import miwu.miot.model.request.GetHome
import miwu.miot.model.request.GetScene
import miwu.miot.model.request.RunCommonScene
import miwu.miot.model.request.RunNewScene
import miwu.miot.model.request.RunScene
import miwu.miot.model.MiotResponse
import miwu.miot.model.miot.DeviceList
import miwu.miot.model.miot.HomeList
import miwu.miot.model.miot.SceneList
import kotlinx.serialization.json.JsonElement

interface HomeService {

    @POST("v2/homeroom/gethome")
    suspend fun getHomes(@Body body: GetHome = GetHome()): MiotResponse<HomeList>

    @POST("v2/home/home_device_list")
    suspend fun getDevices(@Body body: GetDevices): MiotResponse<DeviceList>

    /**
     * @POST("appgateway/miot/appsceneservice/AppSceneService/GetCommonUsedSceneList")
     * suspend fun getScenes(@Body body: GetScene): MiotScenes
     *
     * @POST("appgateway/miot/appsceneservice/AppSceneService/RunScene")
     * suspend fun runScene(@Body body: RunCommonScene): ResponseBody
     **/

    @POST("appgateway/miot/appsceneservice/AppSceneService/GetSimpleSceneList")
    suspend fun getScenes(@Body body: GetScene): MiotResponse<SceneList>


    @POST("appgateway/miot/appsceneservice/AppSceneService/NewRunScene")
    suspend fun runScene(@Body body: RunNewScene): MiotResponse<JsonElement>

    /**
     * 两个 runScene 方法使用根据获取他们的 icon 链接是否为空
     * icon 不为空使用 RunCommonScene
     * icon 为空使用 RunScene
     */
    @Deprecated("api 接口残缺", replaceWith = ReplaceWith("runScene(RunNewScene)"))
    @POST("appgateway/miot/appsceneservice/AppSceneService/RunScene")
    suspend fun runScene(@Body body: RunCommonScene): MiotResponse<JsonElement>

    @Deprecated("api 接口残缺", replaceWith = ReplaceWith("runScene(RunNewScene)"))
    @POST("scene/start")
    suspend fun runScene(@Body body: RunScene): MiotResponse<JsonElement>
}
