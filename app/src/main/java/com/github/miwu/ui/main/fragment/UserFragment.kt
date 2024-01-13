package com.github.miwu.ui.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.miwu.databinding.FragmentMainDeviceBinding
import com.github.miwu.databinding.FragmentMainSettingsBinding
import com.github.miwu.ui.home.HomeActivity
import com.github.miwu.viewmodel.MainViewModel
import kndroidx.activity.ViewActivityX
import kndroidx.extension.start
import kndroidx.fragment.ViewFragmentX

class UserFragment : ViewFragmentX<FragmentMainSettingsBinding, MainViewModel>() {
    override fun init() {
        viewModel.loadInfo()
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestFocus()
    }

    fun startHomeActivity(){
        requireContext().start<HomeActivity>()
    }
}