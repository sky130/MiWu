package com.github.miwu.ui.main

import androidx.lifecycle.ViewModel
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.github.miwu.databinding.ActivityMainBinding as Binding
import com.github.miwu.ui.main.adapter.MainViewPagerAdapter
import com.github.miwu.ui.main.fragment.DeviceFragment
import com.github.miwu.ui.main.fragment.MiWuFragment
import com.github.miwu.ui.main.fragment.SceneFragment
import com.github.miwu.ui.main.fragment.UserFragment
import kndroidx.activity.ViewActivityX
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ViewActivityX<Binding>(Binding::inflate), OnPageChangeListener {

    override val viewModel: MainViewModel by viewModel()
    private val adapter = MainViewPagerAdapter(this)

    override fun init() {
        binding.viewPager.apply {
            adapter = this@MainActivity.adapter
            addOnPageChangeListener(this@MainActivity)
        }
        viewModel.init()
    }

    override fun onPageScrolled(position: Int, offset: Float, offsetPixle: Int) = Unit

    override fun onPageSelected(position: Int) {
        adapter.list[position].second.apply {
            runCatching {
                when (this) {
                    is DeviceFragment -> binding.recycler.requestFocus()
                    is SceneFragment -> binding.recycler.requestFocus()
                    is MiWuFragment -> binding.recycler.requestFocus()
                    is UserFragment -> binding.scroll.requestFocus()
                }
            }
        }
        binding.title.setTitle(adapter.list[position].first)
    }

    override fun onPageScrollStateChanged(state: Int) = Unit
}