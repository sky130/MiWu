package miwu.processor.spec.logic

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SpecProvider {
    const val BASE_URL = "https://miot-spec.org/miot-spec-v2/spec/"
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service: SpecService = retrofit.create(SpecService::class.java)
}