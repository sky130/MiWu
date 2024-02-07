@file:Suppress("PackageDirectoryMismatch")

package com.github.miwu.miot.quick

import com.github.miwu.MainApplication.Companion.miot
import com.github.miwu.miot.utils.getUnitString
import kndroidx.extension.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import miot.kotlin.helper.GetAtt
import miot.kotlin.model.att.SpecAtt
import miot.kotlin.model.miot.MiotDevices
import miot.kotlin.model.miot.MiotScenes

sealed class MiotBaseQuick {

    abstract suspend fun doAction()
    abstract val name: String
    abstract fun initValue()

    abstract class DeviceQuick<T>(
        val device: MiotDevices.Result.Device,
        val siid: Int,
        val piid: Int,
    ) : MiotBaseQuick() {
        abstract var value: T
        override val name = device.name

        @Suppress("UNCHECKED_CAST")
        fun putValue(value: Any) {
            this.value = value as T
        }
    }

    class TextQuick(
        val device: MiotDevices.Result.Device,
        private val textPropertyList: ArrayList<Pair<Int, SpecAtt.Service.Property>>
    ) : MiotBaseQuick() {
        override suspend fun doAction() = Unit
        override val name = device.name
        override fun initValue() = Unit

        fun Pair<Int, SpecAtt.Service.Property>.getBaseText() =
            "${second.description}P[${first},${second.iid}]${getUnitString(second.unit)}"

        suspend fun getTexts() = withContext(Dispatchers.IO) {
            val list = arrayListOf<String>()
            val attList = textPropertyList.filter { "read" in it.second.access }
                .map { GetAtt(it.first, it.second.iid) }.toTypedArray()

            miot.getDeviceAtt(device, attList)?.let {
                it.result?.forEach { i ->
                    val regex = "P[${i.siid},${i.piid}]"
                    textPropertyList.find { i.siid == it.first && i.piid == it.second.iid }?.apply {
                        list.add(this.getBaseText().replace(regex, i.value.toString()))
                    }
                }
            }

            return@withContext list
        }

        // "P[15,15]"
        // "A[15,15]"
    }

    class SceneQuick(private val scene: MiotScenes.Result.Scene) : MiotBaseQuick() {
        override val name = scene.sceneName
        override fun initValue() = Unit
        override suspend fun doAction() {
            miot.runScene(scene)
        }
    }

}