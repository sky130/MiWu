package io.github.sky130.miwu.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Scene
import androidx.viewpager.widget.ViewPager
import io.github.sky130.miwu.databinding.ActivityMainBinding
import io.github.sky130.miwu.logic.dao.HomeDAO
import io.github.sky130.miwu.ui.adapter.AppFragmentPageAdapter
import io.github.sky130.miwu.ui.framgent.BaseFragment
import io.github.sky130.miwu.ui.framgent.DeviceFragment
import io.github.sky130.miwu.ui.framgent.SceneFragment
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {
    private lateinit var binding: ActivityMainBinding
    private val list = arrayListOf(
        DeviceFragment(), SceneFragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.title.setBack(false)
        binding.viewPager.adapter = AppFragmentPageAdapter(supportFragmentManager, list)
        binding.viewPager.addOnPageChangeListener(this)
        setContentView(binding.root)
    }

    override fun onPageSelected(position: Int) { // 页面滚动
        when (position) {
            0 -> {
                setTitle("设备")
            }

            1 -> {
                setTitle("场景")
            }
        }
    }

    fun setTitle(str: String) {
        binding.title.setTitle(str)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageScrollStateChanged(state: Int) {}

}