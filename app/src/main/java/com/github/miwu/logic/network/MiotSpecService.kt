package com.github.miwu.logic.network

import com.google.gson.Gson
import com.github.miwu.logic.dao.database.AppDatabase
import com.github.miwu.logic.model.MiInstance
import com.github.miwu.logic.model.mi.MiLanguage
import com.github.miwu.logic.model.mi.MiSpecType
import com.github.miwu.logic.model.miot.MiotDevice
import com.github.miwu.util.OkHttpUtils
import com.github.miwu.util.TextUtils.log
import org.json.JSONException
import org.json.JSONObject

object MiotSpecService {

    fun getAllInstances(): ArrayList<MiInstance>? {
        val url = "http://miot-spec.org/miot-spec-v2/instances?status=all"
        val json = OkHttpUtils.getRequest(url) ?: return null
        val result = Gson().fromJson(json, Result::class.java) ?: return null
        return result.instances
    }

    fun getInstanceServices(type: String): MiotDevice? {
        val spec = AppDatabase.getDatabase().specDAO()
        val miSpec = spec.getSpec(type)
        val json = if (miSpec != null) {
            miSpec.specJson
        } else {
            val url = "https://miot-spec.org/miot-spec-v2/instance?type=$type"
            OkHttpUtils.getRequest(url) ?: return null
        }
        spec.addSpec(MiSpecType(type, json))
        val miSpecLanguage = AppDatabase.getDatabase().specDAO().getSpecLanguage(type)
        val urnJson = if (miSpecLanguage != null) {
            miSpecLanguage.specJson
        } else {
            val urn = "https://miot-spec.org/instance/v2/multiLanguage?urn=$type"
            OkHttpUtils.getRequest(urn) ?: return null
        }
        spec.addSpecLanguage(MiLanguage(type, urnJson))
        val jsonObj = JSONObject(urnJson).getJSONObject("data")
        try {
            val enLanguage = ArrayList<String>()
            val cnLanguage = ArrayList<String>()
            val enObj = jsonObj.getJSONObject("en")
            val cnObj = jsonObj.getJSONObject("zh_cn")
            for (key in enObj.keys()) {
                try {
                    val cn = cnObj.getString(key)
                    val en = enObj.getString(key)
                    cnLanguage.add(cn)
                    enLanguage.add(en)
                } catch (_: JSONException) {
                }
            }
            val regexPattern = enLanguage.joinToString("|").toRegex()
            val str = json.replace(regexPattern) { matchResult ->
                val matchedText = matchResult.value
                val index = enLanguage.indexOf(matchedText)
                if (index != -1) {
                    cnLanguage[index]
                } else {
                    matchedText
                }
            }
            return Gson().fromJson(str, MiotDevice::class.java)
        } catch (_: Exception) {
        }

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