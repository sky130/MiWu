package com.github.miwu.ui.main

import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.github.miwu.databinding.ActivityMainBinding
import com.github.miwu.logic.preferences.AppPreferences
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.ui.main.adapter.MainViewPagerAdapter
import com.github.miwu.ui.main.fragment.DeviceFragment
import com.github.miwu.ui.main.fragment.MiWuFragment
import com.github.miwu.ui.main.fragment.SceneFragment
import com.github.miwu.ui.main.fragment.UserFragment
import com.github.miwu.viewmodel.MainViewModel
import kndroidx.activity.ViewActivityX

class MainActivity : ViewActivityX<ActivityMainBinding, MainViewModel>(), OnPageChangeListener {

    private val adapter = MainViewPagerAdapter(this)
    override fun init() {
        binding.viewPager.adapter = adapter
        binding.viewPager.addOnPageChangeListener(this)
        AppRepository.updateScene()
        AppRepository.updateDevice()
        AppRepository.updateHome()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.homeId != AppPreferences.homeId) {
            AppRepository.updateScene()
            AppRepository.updateDevice()
            viewModel.homeId = AppPreferences.homeId
        }
    }

    override fun onPageScrolled(position: Int, offset: Float, offsetPixle: Int) = Unit

    override fun onPageSelected(position: Int) {
        adapter.list[position].second.apply {
            try {
                when(this){
                    is DeviceFragment ->{
                        binding.recycler.requestFocus()
                    }
                    is MiWuFragment ->{
                        binding.recycler.requestFocus()
                    }
                    is SceneFragment ->{
                        binding.recycler.requestFocus()
                    }
                    is UserFragment ->{
                        binding.scroll.requestFocus()
                    }
                }
            }catch (_:Exception){}
        }
        binding.title.setTitle(adapter.list[position].first)
    }

    override fun onPageScrollStateChanged(state: Int) = Unit

}