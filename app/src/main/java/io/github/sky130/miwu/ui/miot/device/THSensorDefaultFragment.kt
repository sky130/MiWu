package io.github.sky130.miwu.ui.miot.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.sky130.miwu.databinding.DeviceThSensorDefaultBinding
import io.github.sky130.miwu.logic.model.miot.MiotService
import io.github.sky130.miwu.logic.network.MiotSpecService
import io.github.sky130.miwu.ui.miot.BaseFragment

// 全程Temperature Humidity Sensor
class THSensorDefaultFragment(private val miotServices: ArrayList<MiotService>) : BaseFragment() {

    lateinit var binding: DeviceThSensorDefaultBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?,
    ): View {
        binding = DeviceThSensorDefaultBinding.inflate(inflater)
        for (i in miotServices) {
            val type = MiotSpecService.parseUrn(i.type)?.value ?: continue
            if (type != "temperature-humidity-sensor") continue
            val siid = i.iid
            for (x in i.properties) {
                val type2 = MiotSpecService.parseUrn(x.type)?.value ?: continue
                val piid = x.iid
                when(type2){
                    "temperature"->{
                        binding.temperature.apply {
                            visibility = View.VISIBLE
                            setPiid(piid)
                            setSiid(siid)
                        }
                    }

                    "relative-humidity"->{
                        binding.relativeHumidity.apply {
                            visibility = View.VISIBLE
                            setPiid(piid)
                            setSiid(siid)
                        }
                    }
                }
            }
        }
        return binding.root
    }

}