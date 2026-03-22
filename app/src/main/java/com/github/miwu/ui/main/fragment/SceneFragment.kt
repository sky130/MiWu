package com.github.miwu.ui.main.fragment

import com.github.miwu.ui.main.MainViewModel
import kndroidx.fragment.ViewFragmentX
import miwu.miot.model.miot.MiotScenes
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.github.miwu.databinding.FragmentMainSceneBinding as Binding

class SceneFragment : ViewFragmentX<Binding>(Binding::inflate) {
    override val viewModel: MainViewModel by viewModel()

    fun onItemClick(item: Any?) {
        item as MiotScenes.Result.Scene
        viewModel.miotRepository.runScene(item)
    }

    fun onItemLongClick(item: Any?): Boolean {
        item as MiotScenes.Result.Scene
        // TODO("暂未实现")
        return true
    }

}
