package com.github.miwu.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.github.miwu.R
import com.github.miwu.databinding.ActivityMainBinding
import com.github.miwu.logic.dao.UserDAO
import com.github.miwu.ui.adapter.AppFragmentPageAdapter
import com.github.miwu.ui.framgent.BaseFragment
import com.github.miwu.ui.framgent.DeviceFragment
import com.github.miwu.ui.framgent.SceneFragment
import com.github.miwu.ui.framgent.SettingsFragment

class MainActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var list: ArrayList<FragmentItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        binding = ActivityMainBinding.inflate(layoutInflater)
        list = arrayListOf(
            FragmentItem(getString(R.string.app_name), DeviceFragment()),
            FragmentItem(getString(R.string.scene), SceneFragment()),
            FragmentItem(getString(R.string.settings_bar), SettingsFragment()),
        )
        binding.viewPager.adapter = AppFragmentPageAdapter(supportFragmentManager, list)
        binding.viewPager.addOnPageChangeListener(this)
//        thread {
//            HomeDAO.resetAll {
//                if (it) {
//                    refreshList()
//                }
//            }
//        }
        setContentView(binding.root)
    }

    override fun onPageSelected(position: Int) { // 页面滚动
        setTitle(list[position].title)
        binding.indicator.setIndex(position)
    }

    override fun onResume() {
        super.onResume()
        if(UserDAO.getLocalUser().userId.isEmpty()) finish()
        refreshList()
    }

    private fun refreshList() {
        list.forEach {
            it.fragment.refreshList()
        }
    }

    data class FragmentItem(val title: String, val fragment: BaseFragment)

    fun setTitle(str: String) {
        binding.title.setTitle(str)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageScrollStateChanged(state: Int) {}

}