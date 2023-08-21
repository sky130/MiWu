@file:Suppress("DEPRECATION")

package com.github.miwu.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.github.miwu.ui.MainActivity
import com.github.miwu.ui.framgent.BaseFragment

class AppFragmentPageAdapter(fm: FragmentManager?, private var mFragmentList: ArrayList<MainActivity.FragmentItem>) :
    FragmentPagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment {
        return mFragmentList[position].fragment
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }
}