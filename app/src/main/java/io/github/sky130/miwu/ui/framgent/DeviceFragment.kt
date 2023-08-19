package io.github.sky130.miwu.ui.framgent

import android.annotation.SuppressLint
import android.content.ClipData.Item
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.sky130.miwu.databinding.FragmentMainDeviceBinding
import io.github.sky130.miwu.logic.dao.HomeDAO
import io.github.sky130.miwu.logic.model.mi.MiDevice
import io.github.sky130.miwu.ui.adapter.DeviceItemAdapter
import io.github.sky130.miwu.widget.ViewExtra
import kotlin.concurrent.thread


class DeviceFragment : BaseFragment() ,ViewExtra{

    private lateinit var binding: FragmentMainDeviceBinding

    @SuppressLint("UseRequireInsteadOfGet", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainDeviceBinding.inflate(layoutInflater)
        if (HomeDAO.isInit() && HomeDAO.homeSize() > 0) {
            binding.recycler.adapter = DeviceItemAdapter(0).apply {
                setOnClickListener {
                    startDeviceActivity(list[it])
                }
            }
            thread {
                HomeDAO.resetDeviceOnline {
                    if (it) {
                        runOnUiThread {
                            binding.recycler.adapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        } else {
            // 初始化失败
        }
        return binding.root
    }

}