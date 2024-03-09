package miot.kotlin.service

import miot.kotlin.model.att.SpecAtt
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SpecService {

    @GET("/miot-spec-v2/instance")
    fun getInstance(@Query("type") urn: String): Call<SpecAtt>

    @GET("/instance/v2/multiLanguage")
    fun getSpecMultiLanguage(@Query("urn") urn: String): Call<ResponseBody>

}