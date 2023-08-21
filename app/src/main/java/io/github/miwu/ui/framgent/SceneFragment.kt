package io.github.sky130.miwu.ui.framgent

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.sky130.miwu.databinding.FragmentMainSceneBinding
import io.github.sky130.miwu.logic.dao.HomeDAO
import io.github.sky130.miwu.logic.network.MiotService
import io.github.sky130.miwu.ui.adapter.SceneItemAdapter
import io.github.sky130.miwu.util.TextUtils.toast
import kotlin.concurrent.thread

class SceneFragment : BaseFragment() {

    private lateinit var binding: FragmentMainSceneBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainSceneBinding.inflate(layoutInflater)
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
            binding.recycler.adapter = SceneItemAdapter().apply {
                setOnClickListener {
//                    "「${list[it].sceneName}」已执行".toast()
                    thread {
                        MiotService.runScene(list[it])
                    }
                }
            }
            refreshList()
        }
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun refreshList() {
        thread {
            HomeDAO.resetScene {
                runOnUiThread {
                    if (it) {
                        updateLayoutVisibility()
                        binding.recycler.adapter!!.notifyDataSetChanged()
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
                }

            }
        }
    }

    fun updateLayoutVisibility() {
        val home = HomeDAO.getHome(HomeDAO.getHomeIndex())
        binding.empty.visibility = if (home!!.sceneList.isEmpty()) View.VISIBLE else View.GONE
    }
}