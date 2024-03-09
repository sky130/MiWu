package miot.kotlin.utils

import retrofit2.Retrofit
import retrofit2.create

inline fun <reified T> Retrofit.create(): T = this.create(T::class.java)