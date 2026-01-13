package miwu.miot.kmp.service

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import miwu.miot.model.att.SpecAtt

interface SpecService {

    @GET("miot-spec-v2/instance")
    suspend fun getInstance(@Query("type") urn: String): SpecAtt

    @GET("instance/v2/multiLanguage")
    suspend fun getSpecMultiLanguage(@Query("urn") urn: String): String

}