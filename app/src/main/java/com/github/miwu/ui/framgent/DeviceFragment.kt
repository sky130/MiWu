package com.github.miwu.ui.framgent

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.miwu.databinding.FragmentMainDeviceBinding
import com.github.miwu.logic.dao.HomeDAO
import com.github.miwu.ui.adapter.DeviceItemAdapter
import com.github.miwu.util.TextUtils.toast
import kotlin.concurrent.thread


class DeviceFragment : BaseFragment() {

    private lateinit var binding: FragmentMainDeviceBinding

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainDeviceBinding.inflate(layoutInflater)

        binding.swipe.setOnRefreshListener { // 刷新方法
            refreshList()
        }

        binding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() { // 判断冲突
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val topRowVerticalPosition = layoutManager.findFirstVisibleItemPosition()
                binding.swipe.isEnabled = topRowVerticalPosition == 0
            }
        })
        if (HomeDAO.isInit() && HomeDAO.homeSize() > 0) {
            updateLayoutVisibility()
            binding.recycler.adapter = DeviceItemAdapter().apply {
                setOnClickListener {
                    startDeviceActivity(list[it])
                }
            }
            refreshList()
        }
        binding.recycler.requestFocus()
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
     override fun refreshList() {
        thread {
            HomeDAO.resetDeviceOnline {
                runOnUiThread {
                    if (it) {
                        updateLayoutVisibility()
                        if (binding.swipe.isRefreshing) {
                            binding.swipe.isRefreshing = false
                            "刷新完成".toast()
                        }
                    } else {
                        if (binding.swipe.isRefreshing) {
                            binding.swipe.isRefreshing = false
                            "刷新失败".toast()
                        }
                    }
                    binding.recycler.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    fun updateLayoutVisibility() {
        val home = HomeDAO.getHome(HomeDAO.getHomeIndex())
        binding.empty.visibility = if (home!!.deviceList.isEmpty()) View.VISIBLE else View.GONE
    }
}