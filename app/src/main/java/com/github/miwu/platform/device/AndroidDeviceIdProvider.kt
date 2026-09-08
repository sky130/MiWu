package com.github.miwu.platform.device

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.github.miwu.domain.gateway.DeviceIdProvider
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Singleton

@Singleton
class AndroidDeviceIdProvider(
    @Provided private val context: Context,
) : DeviceIdProvider {
    @delegate:SuppressLint("HardwareIds")
    override val id: String by lazy {
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}
