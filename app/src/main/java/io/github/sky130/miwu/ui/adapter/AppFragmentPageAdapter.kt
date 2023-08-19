@file:Suppress("DEPRECATION")

package io.github.sky130.miwu.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.github.sky130.miwu.ui.MainActivity
import io.github.sky130.miwu.ui.framgent.BaseFragment

class AppFragmentPageAdapter(fm: FragmentManager?, private var mFragmentList: ArrayList<MainActivity.FragmentItem>) :
    FragmentPagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment {
        return mFragmentList[position].fragment
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }
}