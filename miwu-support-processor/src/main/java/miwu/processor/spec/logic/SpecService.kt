package miwu.processor.spec.logic

import retrofit2.http.GET

interface SpecService {

    @GET("devices")
    suspend fun getDevices() : Type

    @GET("services")
    suspend fun getServices() : Type

    @GET("actions")
    suspend fun getActions() : Type

    @GET("properties")
    suspend fun getProperties() : Type

}