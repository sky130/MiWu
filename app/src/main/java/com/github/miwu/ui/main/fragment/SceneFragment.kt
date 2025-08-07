package com.github.miwu.ui.main.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.miwu.databinding.FragmentMainSceneBinding as Binding
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.ui.main.FragmentState
import com.github.miwu.ui.main.FragmentState.*
import com.github.miwu.ui.main.MainViewModel
import fr.haan.resultat.onFailure
import fr.haan.resultat.onLoading
import fr.haan.resultat.onSuccess
import kndroidx.fragment.ViewFragmentX
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import miwu.miot.model.miot.MiotScenes
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SceneFragment : ViewFragmentX<Binding>(Binding::inflate) {
    override val viewModel: MainViewModel by viewModel()

    fun onItemClick(item: Any?) {
        item as MiotScenes.Result.Scene
        viewModel.viewModelScope.launch {
            viewModel.appRepository.miotClient.Home.runScene(item)
        }
    }

    fun onItemLongClick(item: Any?): Boolean {
        item as MiotScenes.Result.Scene
        TODO("暂未实现")
        return true
    }

}
