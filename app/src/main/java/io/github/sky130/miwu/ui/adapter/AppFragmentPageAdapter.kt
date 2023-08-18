@file:Suppress("DEPRECATION")

package io.github.sky130.miwu.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class AppFragmentPageAdapter(fm: FragmentManager?, private var mFragmentList: List<Fragment>?) :
    FragmentPagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment {
        return mFragmentList!![position]
    }

    override fun getCount(): Int {
        return if (mFragmentList == null) 0 else mFragmentList!!.size
    }
}