package com.github.miwu.logic.network

import com.google.gson.Gson
import com.github.miwu.MainApplication.Companion.loginMsg
import com.github.miwu.logic.model.device.GetDeviceATT
import com.github.miwu.logic.model.user.LoginMsg
import com.github.miwu.logic.model.mi.MiDevice
import com.github.miwu.logic.model.mi.MiHome
import com.github.miwu.logic.model.mi.MiRoom
import com.github.miwu.logic.model.device.SetDeviceATT
import com.github.miwu.util.OkHttpUtils
import com.github.miwu.util.TextUtils.log

object DeviceService {

    fun setDeviceATT(did: String, siid: Int, piid: Int, value: Any): SetDeviceATT? {
        val gson = Gson()
        val data =
            gson.toJson(SetParams(listOf(SetDeviceAtt(did, siid, piid, value)))) ?: return null
        val result = OkHttpUtils.postData("/miotspec/prop/set", data, loginMsg) ?: return null
        try {
            val deviceATT = gson.fromJson(result, SetResult::class.java)
            deviceATT.result[0].apply {
                return SetDeviceATT(
                    code.toString(), iid, siid, piid, exe_time
                )
            }
        } catch (_: Exception) {
            return null
        }
    }

    fun getDeviceATT(did: String, siid: Int, piid: Int): GetDeviceATT? {
        val gson = Gson()
        val data = gson.toJson(GetParams(listOf(GetDeviceAtt(did, siid, piid)))) ?: return null
        val result = OkHttpUtils.postData("/miotspec/prop/get", data, loginMsg) ?: return null
        try {
            val deviceAtt = gson.fromJson(result, GetResult::class.java)
            deviceAtt.result[0].apply {
                return GetDeviceATT(
                    code.toString(), iid, siid, piid, value, updateTime, exe_time
                )
            }
        } catch (_: Exception) {
            return null
        }
    }

    fun getDeviceATT(atts:ArrayList<GetDeviceAtt>) : ArrayList<GetDeviceAtt>? {
        val gson = Gson()
        val data = gson.toJson(GetParams(atts)) ?: return null
        val result = OkHttpUtils.postData("/miotspec/prop/get", data, loginMsg) ?: return null
        try {
            val deviceAtt = gson.fromJson(result, GetResult::class.java)
            return ArrayList<GetDeviceATT>().apply{
                for(i in deviceAtt.result){
                    add(
                        GetDeviceATT(
                            code.toString(), iid, siid, piid, value, updateTime, exe_time
                        )
                    )
                }
            }
        } catch (_: Exception) {
            return null
        }
    }

    fun doAction(did: String, siid: Int, aiid: Int) {
        val data = "{\"params\":{\"did\":\"$did\",\"siid\":$siid,\"aiid\":$aiid,\"in\":[]}}"
        val result = OkHttpUtils.postData("/miotspec/action", data, loginMsg)
    }

    // 用于解析Json
    private data class GetDeviceAtt(val did: String, val siid: Int, val piid: Int)
    private data class SetDeviceAtt(val did: String, val siid: Int, val piid: Int, val value: Any)
    private data class GetParams(val params: List<GetDeviceAtt>)
    private data class SetParams(val params: List<SetDeviceAtt>)
    private data class SetResultItem(
        val did: String,
        val iid: String,
        val siid: Int,
        val piid: Int,
        val code: Int,
        val exe_time: Int,
    )

    private data class SetResult(
        val code: Int,
        val message: String,
        val result: List<SetResultItem>,
    )

    private data class GetResultItem(
        val did: String,
        val iid: String,
        val siid: Int,
        val piid: Int,
        val value: Any,
        val code: Int,
        val updateTime: Long,
        val exe_time: Int,
    )

    private data class GetResult(
        val code: Int,
        val message: String,
        val result: List<GetResultItem>,
    )

    private data class DeviceResult(
        val code: Int,
        val message: String,
        val result: DevicesResult?,
    )

    private data class DevicesResult(
        val list: List<Device>?,
    )

    private data class Extra(
        val isSetPincode: Int,
        val pincodeType: Int,
        val fw_version: String,
        val needVerifyCode: Int,
        val isPasswordEncrypt: Int,
    )

    private data class Device(
        val did: String,
        val token: String,
        val longitude: String,
        val latitude: String,
        val name: String,
        val pid: String,
        val localip: String,
        val mac: String,
        val ssid: String,
        val bssid: String,
        val parent_id: String,
        val parent_model: String,
        val show_mode: Int,
        val model: String,
        val adminFlag: Int,
        val shareFlag: Int,
        val permitLevel: Int,
        val isOnline: Boolean,
        val desc: String,
        val extra: Extra,
        val uid: Long,
        val pd_id: Int,
        val password: String,
        val p2p_id: String,
        val rssi: Int,
        val family_id: Int,
        val reset_flag: Int,
    )

    private data class Home(
        val id: String,
        val name: String,
        val bssid: String?,
        val dids: List<String>,
        val temp_dids: Any?,
        val icon: String,
        val shareflag: Int,
        val permit_level: Int,
        val status: Int,
        val background: String,
        val smart_room_background: String,
        val longitude: Double,
        val latitude: Double,
        val city_id: Int,
        val address: String,
        val create_time: Long,
        val roomlist: List<Room>,
        val uid: Long,
        val appear_home_list: Any?,
        val popup_flag: Int,
        val popup_time_stamp: Long,
        val car_did: String,
    )

    private data class Room(
        val id: String,
        val name: String,
        val bssid: String?,
        val parentid: String,
        val dids: List<String>,
        val icon: String,
        val background: String,
        val shareflag: Int,
        val create_time: Long,
    )


    private data class HomeResult(
        val homelist: List<Home>,
        val has_more: Boolean,
        val max_id: String,
    )


}