package miwu.miot.kmp.service

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import miwu.miot.model.att.SpecAtt
import miwu.miot.model.att.SpecType

interface SpecService {

    @GET("miot-spec-v2/instance")
    suspend fun getInstance(@Query("type") urn: String): SpecAtt

    @GET("instance/v2/multiLanguage")
    suspend fun getSpecMultiLanguage(@Query("urn") urn: String): String

    @GET("miot-spec-v2/spec/devices")
    suspend fun getDevices() : SpecType

    @GET("miot-spec-v2/spec/services")
    suspend fun getServices() : SpecType

    @GET("miot-spec-v2/spec/actions")
    suspend fun getActions() : SpecType

    @GET("miot-spec-v2/spec/properties")
    suspend fun getProperties() : SpecType

}