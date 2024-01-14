package com.github.miwu.ui.main

import com.github.miwu.databinding.ActivityMainBinding
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.ui.main.adapter.MainViewPagerAdapter
import com.github.miwu.viewmodel.MainViewModel
import kndroidx.activity.ViewActivityX

class MainActivity : ViewActivityX<ActivityMainBinding, MainViewModel>() {

    override fun init() {
        binding.viewPager.adapter = MainViewPagerAdapter(this)
        AppRepository.loadHomes()
        AppRepository.loadDevice()
        AppRepository.loadScene()
    }

}