package com.github.miwu.ui.main.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter
import com.github.miwu.R
import com.github.miwu.ui.main.fragment.DeviceFragment
import com.github.miwu.ui.main.fragment.MiWuFragment
import com.github.miwu.ui.main.fragment.SceneFragment
import com.github.miwu.ui.main.fragment.UserFragment
import kndroidx.extension.string

@Suppress("DEPRECATION")
class MainViewPagerAdapter(activity: AppCompatActivity) :
    FragmentPagerAdapter(activity.supportFragmentManager) {

    val list = listOf(
        R.string.app_name.string to MiWuFragment(),
        R.string.device.string to DeviceFragment(),
        R.string.scene.string to SceneFragment(),
        R.string.more.string to UserFragment(),
        )

    override fun getCount() = list.size
    override fun getItem(position: Int) = list[position].second
}