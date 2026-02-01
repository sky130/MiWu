package miwu.miot.service

import miwu.miot.model.att.SpecAtt
import miwu.miot.model.att.SpecType
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SpecService {

    @GET("/miot-spec-v2/instance")
    suspend fun getInstance(@Query("type") urn: String): SpecAtt

    @GET("/instance/v2/multiLanguage")
    suspend fun getSpecMultiLanguage(@Query("urn") urn: String): ResponseBody

    @GET("miot-spec-v2/spec/devices")
    suspend fun getDevices(): SpecType

    @GET("miot-spec-v2/spec/services")
    suspend fun getServices(): SpecType

    @GET("miot-spec-v2/spec/actions")
    suspend fun getActions(): SpecType

    @GET("miot-spec-v2/spec/properties")
    suspend fun getProperties(): SpecType

}