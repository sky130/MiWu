package com.github.miwu

import android.app.Application
import kndroidx.kndroidx
import miot.kotlin.Miot
import miot.kotlin.MiotManager

class MainApplication : Application() {

    companion object {
        lateinit var miotUser: Miot.MiotUser
        val Any.miot by lazy { MiotManager.from(miotUser) }
    }

    override fun onCreate() {
        super.onCreate()
        kndroidx {
            context = applicationContext
        }
    }
}