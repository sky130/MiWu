package io.github.sky130.miwu.ui.miot

import android.app.Activity
import io.github.sky130.miwu.logic.network.MiotSpecService
import io.github.sky130.miwu.startActivity
import io.github.sky130.miwu.ui.DeviceActivity
import io.github.sky130.miwu.ui.miot.device.EmptyFragment
import io.github.sky130.miwu.ui.miot.device.LightDefaultFragment
import io.github.sky130.miwu.ui.miot.device.OutletDefaultFragment
import io.github.sky130.miwu.ui.miot.device.THSensorDefaultFragment
import io.github.sky130.miwu.ui.miot.device.lemesh_ight_wy0c03
import kotlin.concurrent.thread

object DeviceUtils {

    fun Activity.startDeviceActivity(model: String, did: String, name: String, specType: String) {
        this.startActivity<DeviceActivity> {
            putExtra("model", model)
            putExtra("did", did)
            putExtra("name", name)
            putExtra("specType", specType)
        }
    }

    fun getDeviceFragment(model: String, specType: String, block: (BaseFragment) -> Unit) {
        when (model) {
            "lemesh.light.wy0c03" -> {
                block(lemesh_ight_wy0c03())
            }

            else -> {
                thread {
                    val miotDevice =
                        MiotSpecService.getInstanceServices(specType)
                            ?: return@thread block(
                                EmptyFragment()
                            )
                    val urn = MiotSpecService.parseUrn(miotDevice.type) ?: return@thread block(
                        EmptyFragment()
                    )
                    block(
                        when (urn.value) {
                            "light" -> {
                                LightDefaultFragment(miotDevice.services)
                            }

                            "temperature-humidity-sensor" -> {
                                THSensorDefaultFragment(miotDevice.services)
                            }

                            "outlet" -> {
                                OutletDefaultFragment(miotDevice.services)
                            }

                            else -> {
                                EmptyFragment()
                            }
                        }
                    )
                }
            }
        }
    }


}