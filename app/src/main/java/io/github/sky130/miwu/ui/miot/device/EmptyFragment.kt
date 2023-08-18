package io.github.sky130.miwu.ui.miot.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.sky130.miwu.databinding.DeviceEmptyBinding
import io.github.sky130.miwu.ui.miot.BaseFragment

class EmptyFragment : BaseFragment() {
    private lateinit var binding: DeviceEmptyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?,
    ): View {
        binding = DeviceEmptyBinding.inflate(inflater)
        return binding.root
    }



}