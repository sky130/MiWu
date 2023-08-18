package io.github.sky130.miwu.logic.network

import com.google.gson.Gson
import io.github.sky130.miwu.logic.model.MiInstance
import io.github.sky130.miwu.logic.model.miot.MiotDevice
import io.github.sky130.miwu.util.OkHttpUtils

object MiotSpecService {

    fun getAllInstances(): ArrayList<MiInstance>? {
        val url = "http://miot-spec.org/miot-spec-v2/instances?status=all"
        val json = OkHttpUtils.getRequest(url) ?: return null
        val result = Gson().fromJson(json, Result::class.java) ?: return null
        return result.instances
    }

    fun getInstanceServices(type: String): MiotDevice? {
        val url = "https://miot-spec.org/miot-spec-v2/instance?type=$type"
        val json = OkHttpUtils.getRequest(url) ?: return null
        return Gson().fromJson(json, MiotDevice::class.java)
    }



    fun parseUrn(urn: String): MiotUrn? {
        val split = urn.split(":")
        if (split.isEmpty()) return null
        return MiotUrn(split[2], split[3])
    }

    data class MiotUrn(
        val type: String,
        val value: String,
    )

    private data class Result(val instances: ArrayList<MiInstance>)

}