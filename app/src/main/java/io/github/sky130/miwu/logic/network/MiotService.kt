package io.github.sky130.miwu.logic.network

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.github.sky130.miwu.MainApplication.Companion.loginMsg
import io.github.sky130.miwu.logic.model.mi.MiDevice
import io.github.sky130.miwu.logic.model.mi.MiHome
import io.github.sky130.miwu.logic.model.mi.MiInfo
import io.github.sky130.miwu.logic.model.mi.MiRoom
import io.github.sky130.miwu.logic.model.mi.MiScene
import io.github.sky130.miwu.util.OkHttpUtils
import io.github.sky130.miwu.util.TextUtils.log
import org.json.JSONObject

object MiotService {


    fun getMiInfo(): MiInfo? {
        val homeUrlPath = "/v2/homeroom/gethome" // 获取全部家庭的接口
        val homeData = // 发送的数据
            " {\"fg\": false, \"fetch_share\": true, \"fetch_share_dev\": true, \"limit\": 300, \"app_ver\": 7}"
        val homeResultJson = OkHttpUtils.postData(homeUrlPath, homeData, loginMsg) ?: return null
        homeResultJson.log()

        data class Room(
            @SerializedName("id")
            val roomId: String,
            @SerializedName("name")
            val roomName: String,
            val dids: ArrayList<String>,
        )

        data class Home(
            @SerializedName("id")
            val homeId: String,
            @SerializedName("name")
            val homeName: String,
            val roomlist: ArrayList<Room>,
            val uid: Long,
        )

        data class HomeResult(
            @SerializedName("homelist") val homeList: ArrayList<Home>,
            @SerializedName("share_home_list") val shareHomeList: ArrayList<Home>?,
        )

        data class Result(val code: Int, val result: HomeResult)

        val result = Gson().fromJson(homeResultJson, Result::class.java)
        if (result.code != 0) return null
        val homeList = ArrayList<MiHome>()
        for (i in result.result.homeList) { // 非共享家庭
            val miHome = MiHome(
                i.homeId, i.homeName, i.uid.toString(), false,
                getMiScenes(i.homeId, i.uid.toString()),// 场景列表
                ArrayList(),// 房间列表
                ArrayList(),// 设备列表
            )
            for (x in i.roomlist) {
                val miRoom = MiRoom(
                    x.roomId, x.roomName, i.homeId, i.homeName, x.dids, ArrayList() // 设备列表暂时为空
                )
                miHome.roomList.add(miRoom)
            }

            val deviceInfo = getHomeDevice(miHome.userId, miHome.homeId) ?: continue
            for (x in deviceInfo) { // 嵌套循环
                for (y in miHome.roomList) {
                    if (x.did in y.deviceIdList) { // 判断是否在房间中
                        y.apply {
                            val miDevice = MiDevice(
                                roomId,
                                roomName,
                                homeId,
                                homeName,
                                x.name,
                                x.model,
                                x.did,
                                x.isOnline,
                                x.specType,
                                getModelIconUrl(x.model) ?: ""
                            )
                            y.deviceList.add(miDevice)
                            miHome.deviceList.add(miDevice)
                        }
                    }
                }
            }
            homeList.add(miHome)
        }
        if (result.result.shareHomeList != null)
            for (i in result.result.shareHomeList) { // 共享家庭
                val miHome = MiHome(
                    i.homeId, i.homeName, i.uid.toString(), true,
                    getMiScenes(i.homeId, i.uid.toString()),// 场景列表
                    ArrayList(),// 房间列表
                    ArrayList(),// 设备列表
                )
                for (x in i.roomlist) {
                    val miRoom = MiRoom(
                        x.roomId, x.roomName, i.homeId, i.homeName, x.dids, ArrayList() // 设备列表暂时为空
                    )
                    miHome.roomList.add(miRoom)
                }

                val deviceInfo = getHomeDevice(miHome.userId, miHome.homeId) ?: continue
                for (x in deviceInfo) { // 嵌套循环
                    for (y in miHome.roomList) {
                        if (x.did in y.deviceIdList) { // 判断是否在房间中
                            y.apply {
                                val miDevice = MiDevice(
                                    roomId,
                                    roomName,
                                    homeId,
                                    homeName,
                                    x.name,
                                    x.model,
                                    x.did,
                                    x.isOnline,
                                    x.specType,
                                    getModelIconUrl(x.model) ?: ""
                                )
                                y.deviceList.add(miDevice)
                                miHome.deviceList.add(miDevice)
                            }
                        }
                    }
                }
                homeList.add(miHome)
            }
        return MiInfo(homeList)
    }

    fun getMiScenes(homeId: String, userId: String): ArrayList<MiScene> {
        val data =
            "{\"home_id\": $homeId,\"home_owner\":$userId,\"need_recommended_template\":true}"
        val json = OkHttpUtils.postData(
            "/appgateway/miot/appsceneservice/AppSceneService/GetCommonUsedSceneList",
            data,
            loginMsg
        ) ?: return ArrayList()

        data class SceneResult(@SerializedName("common_use_scene") val sceneList: ArrayList<MiScene>)
        data class Result(val code: Int, val result: SceneResult)

        val result = Gson().fromJson(json, Result::class.java)
        if (result.code != 0) return ArrayList()
        for (i in result.result.sceneList) {
            i.homeId = homeId
        }
        return result.result.sceneList
    }

    fun getModelIconUrl(model: String): String? {
        val url = "https://home.mi.com/cgi-op/api/v1/baike/v2/product?model=$model"
        val json = OkHttpUtils.getRequest(url) ?: return null
        val jsonObject = JSONObject(json)
        if (jsonObject.getInt("code") != 0) return null
        return jsonObject.getJSONObject("data").getString("realIcon")
    }

    fun runScene(scene: MiScene) {
        if (scene.icon.isNotEmpty()) {
            val data = " {\"scene_id\": ${scene.sceneId}, \"trigger_key\": \"user.click\"}"
            val url = "/appgateway/miot/appsceneservice/AppSceneService/RunScene"
            OkHttpUtils.postData(url, data, loginMsg)
        } else {
            val data = " {\"us_id\": ${scene.sceneId}, \"key\": \"\"}"
            val url = "/scene/start"
            OkHttpUtils.postData(url, data, loginMsg)
        }
    }

    private fun getHomeDevice(userId: String, homeId: String): ArrayList<HomeDeviceInfo>? {
        val deviceUrlPath = "/v2/home/home_device_list" // 获取全部家庭的接口
        val homeData = "{\"home_owner\":${userId},\"home_id\":${homeId},\"limit\":200}"
        val homeResultJson = OkHttpUtils.postData(deviceUrlPath, homeData, loginMsg) ?: return null

        data class Result(@SerializedName("device_info") val deviceList: ArrayList<HomeDeviceInfo>)
        data class HomeResult(val code: Int, val result: Result)

        val deviceResult = Gson().fromJson(homeResultJson, HomeResult::class.java) // 全部设备的信息
        if (deviceResult.code != 0) return null
        return deviceResult.result.deviceList
    }

    data class HomeDeviceInfo(
        val did: String,
        val name: String,
        val model: String,
        val isOnline: Boolean,
        @SerializedName("spec_type") val specType: String,
    )

}