package io.github.sky130.miwu.ui.framgent

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.GONE
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.VISIBLE
import io.github.sky130.miwu.databinding.FragmentMainDeviceBinding
import io.github.sky130.miwu.logic.dao.HomeDAO
import io.github.sky130.miwu.ui.adapter.DeviceItemAdapter
import io.github.sky130.miwu.util.TextUtils.toast
import kotlin.concurrent.thread


class DeviceFragment : BaseFragment(){

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
            if (HomeDAO.getHome(0)!!.deviceList.size > 0) binding.empty.visibility =
                GONE else binding.empty.visibility = VISIBLE
            binding.recycler.adapter = DeviceItemAdapter(0).apply {
                setOnClickListener {
                    startDeviceActivity(list[it])
                }
            }
            refreshList()
        }
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
     override fun refreshList() {
        thread {
            HomeDAO.resetDeviceOnline {
                runOnUiThread {
                    if (it) {
                        binding.recycler.adapter!!.notifyDataSetChanged()
                        if (binding.swipe.isRefreshing) {
                            binding.swipe.isRefreshing = false
                            "刷新完成".toast()
                        }
                    }else{
                        if (binding.swipe.isRefreshing) {
                            binding.swipe.isRefreshing = false
                            "刷新失败".toast()
                        }
                    }
                }

            }
        }
    }

}