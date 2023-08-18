package io.github.sky130.miwu.ui.framgent

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.github.sky130.miwu.databinding.FragmentMainDeviceBinding
import io.github.sky130.miwu.logic.dao.HomeDAO
import io.github.sky130.miwu.ui.adapter.ItemAdapter
import kotlin.concurrent.thread

class DeviceFragment : BaseFragment() {

    private lateinit var binding: FragmentMainDeviceBinding

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainDeviceBinding.inflate(layoutInflater)
        if (HomeDAO.isInit()) {
            binding.recycler.adapter = ItemAdapter(HomeDAO.getHome()!!.deviceList).apply {
                setOnClickListener {
                    startDeviceActivity(list[it])
                }
            }
        } else {
            // 初始化失败
        }
        return binding.root
    }

}