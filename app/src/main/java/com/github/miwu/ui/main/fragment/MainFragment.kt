package com.github.miwu.ui.main.fragment

import androidx.fragment.app.Fragment
import com.github.miwu.databinding.FragmentMainDeviceBinding
import com.github.miwu.viewmodel.MainViewModel
import kndroidx.activity.ViewActivityX
import kndroidx.fragment.ViewFragmentX

class MainFragment : ViewFragmentX<FragmentMainDeviceBinding, MainViewModel>() {
    override fun onResume() {
        super.onResume()
        binding.recycler.requestFocus()
    }
}