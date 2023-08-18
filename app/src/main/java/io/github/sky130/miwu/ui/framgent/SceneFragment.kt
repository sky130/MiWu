package io.github.sky130.miwu.ui.framgent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.sky130.miwu.databinding.FragmentMainSceneBinding
import io.github.sky130.miwu.logic.dao.HomeDAO
import io.github.sky130.miwu.logic.network.MiotService
import io.github.sky130.miwu.ui.adapter.SceneItemAdapter
import kotlin.concurrent.thread

class SceneFragment : BaseFragment() {

    private lateinit var binding: FragmentMainSceneBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainSceneBinding.inflate(layoutInflater)
        binding.recycler.adapter = SceneItemAdapter(HomeDAO.getHome()!!.sceneList).apply {
            setOnClickListener {
                "「${list[it].sceneName}」已执行"
                thread {
                    MiotService.runScene(list[it])
                }
            }
        }
        return binding.root
    }

}