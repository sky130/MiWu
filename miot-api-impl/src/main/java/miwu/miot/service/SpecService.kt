package miwu.miot.service

import miwu.miot.model.att.SpecAtt
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SpecService {

    @GET("/miot-spec-v2/instance")
    suspend fun getInstance(@Query("type") urn: String): SpecAtt

    @GET("/instance/v2/multiLanguage")
    suspend fun getSpecMultiLanguage(@Query("urn") urn: String): ResponseBody

}