package io.github.sky130.miwu.ui.miot.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.sky130.miwu.databinding.DeviceLemeshLightWy0c03Binding as Binding
import io.github.sky130.miwu.ui.miot.BaseFragment

class lemesh_ight_wy0c03() : BaseFragment() {
    private lateinit var binding: Binding
    private lateinit var did: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = Binding.inflate(layoutInflater)
        did = getDid()
        return binding.root
    }

}