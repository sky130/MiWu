package com.github.miwu.ui.main.fragment

import com.github.miwu.ui.main.MainViewModel
import kndroidx.fragment.ViewFragmentX
import miwu.miot.model.miot.MiotScene
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.github.miwu.databinding.FragmentMainSceneBinding as Binding

class SceneFragment : ViewFragmentX<Binding>(Binding::inflate) {
    override val viewModel: MainViewModel by viewModel()

    fun onItemClick(item: Any?) {
        val scene = item as? MiotScene ?: return
        viewModel.runScene(scene)
    }

    fun onItemLongClick(item: Any?): Boolean {
        val scene = item as? MiotScene ?: return false
        // TODO("暂未实现")
        return true
    }

}
