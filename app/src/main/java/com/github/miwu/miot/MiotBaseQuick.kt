@file:Suppress("PackageDirectoryMismatch")

package com.github.miwu.miot.quick

import com.github.miwu.MainApplication.Companion.miot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import miot.kotlin.model.miot.MiotDevices
import miot.kotlin.model.miot.MiotScenes

sealed class MiotBaseQuick {

    abstract suspend fun doAction()
    abstract val name: String
    abstract fun initValue()

    sealed class DeviceQuick<T>(
        val device: MiotDevices.Result.Device, val siid: Int,
        val piid: Int,
    ) : MiotBaseQuick() {
        abstract var value: T
        override val name = device.name

        @Suppress("UNCHECKED_CAST")
        fun putValue(value: Any) {
            this.value = value as T
        }
    }

    class SceneQuick(private val scene: MiotScenes.Result.Scene) : MiotBaseQuick() {
        override val name = scene.sceneName
        override fun initValue() = Unit
        override suspend fun doAction() {
            miot.runScene(scene)
        }
    }

}