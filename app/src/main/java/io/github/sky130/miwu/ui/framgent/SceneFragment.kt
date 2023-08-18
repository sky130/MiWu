package io.github.sky130.miwu.ui.framgent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        if (HomeDAO.isInit()) {
            val list = HomeDAO.getHome()!!.sceneList
            binding.recycler.adapter = SceneItemAdapter(list).apply {
                setOnClickListener {
                    "「${list[it].sceneName}」已执行".toast()
                    thread {
                        MiotService.runScene(list[it])
                    }
                }
            }
        }

        return binding.root
    }

}