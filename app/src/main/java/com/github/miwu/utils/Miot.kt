package com.github.miwu.utils

import android.content.ComponentCallbacks
import miwu.miot.client.MiotDeviceClient
import miwu.miot.client.MiotHomeClient
import miwu.miot.client.MiotUserClient
import miwu.miot.model.MiotUser
import org.koin.android.ext.android.get
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

fun KoinComponent.MiotUserClient(user: MiotUser): MiotUserClient =
    get<MiotUserClient> {
        parametersOf(user)
    }

fun KoinComponent.MiotHomeClient(user: MiotUser): MiotHomeClient =
    get<MiotHomeClient> {
        parametersOf(user)
    }

fun KoinComponent.MiotDeviceClient(user: MiotUser): MiotDeviceClient =
    get<MiotDeviceClient> {
        parametersOf(user)
    }

fun ComponentCallbacks.MiotUserClient(user: MiotUser): MiotUserClient =
    get<MiotUserClient> {
        parametersOf(user)
    }

fun ComponentCallbacks.MiotHomeClient(user: MiotUser): MiotHomeClient =
    get<MiotHomeClient> {
        parametersOf(user)
    }

fun ComponentCallbacks.MiotDeviceClient(user: MiotUser): MiotDeviceClient =
    get<MiotDeviceClient> {
        parametersOf(user)
    }