package io.github.sky130.miwu.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import io.github.sky130.miwu.R
import io.github.sky130.miwu.databinding.ActivityMainBinding
import io.github.sky130.miwu.ui.adapter.AppFragmentPageAdapter
import io.github.sky130.miwu.ui.framgent.BaseFragment
import io.github.sky130.miwu.ui.framgent.DeviceFragment
import io.github.sky130.miwu.ui.framgent.SceneFragment
import io.github.sky130.miwu.ui.framgent.SettingsFragment

class MainActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {
    private lateinit var binding: ActivityMainBinding
    private val list = arrayListOf(
        FragmentItem(getString(R.string.app_name), DeviceFragment()),
        FragmentItem(getString(R.string.scene), SceneFragment()),
        FragmentItem(getString(R.string.settings_bar), SettingsFragment()),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.viewPager.adapter = AppFragmentPageAdapter(supportFragmentManager, list)
        binding.viewPager.addOnPageChangeListener(this)
        setContentView(binding.root)
    }

    override fun onPageSelected(position: Int) { // 页面滚动
        setTitle(list[position].title)
    }

    data class FragmentItem(val title: String, val fragment: BaseFragment)

    fun setTitle(str: String) {
        binding.title.setTitle(str)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageScrollStateChanged(state: Int) {}

}