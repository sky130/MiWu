package com.github.miwu.ui.main.fragment

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.miwu.databinding.FragmentMainMiwuBinding as Binding
import com.github.miwu.ui.main.MainViewModel
import com.github.miwu.ui.main.adapter.MiWuAdapter
import fr.haan.resultat.Resultat
import kndroidx.fragment.ViewFragmentX
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MiWuFragment : ViewFragmentX<Binding>(Binding::inflate) {
    override val viewModel: MainViewModel by viewModel()

    private val adapter by lazy { MiWuAdapter(viewModel,viewModel.appRepository) }
    val isEmpty = MutableLiveData(false)

    @SuppressLint("NotifyDataSetChanged")
    override fun init() {
        binding.recycler.adapter = adapter

        viewModel.deviceRepository.deviceList.onEach {
            val list = ArrayList(it)
            isEmpty.value = list.isEmpty()
            list.sortBy { it.position }
            adapter.updateList(list)
        }.launchIn(viewModel.viewModelScope)
        viewModel.appRepository.homes.onEach {
            if (it is Resultat.Success) adapter.notifyDataSetChanged()
        }.launchIn(viewModel.viewModelScope)

//        adapter.onLongClickBlock = {
//            requireActivity().start<EditFavoriteActivity>()
//        }
//        adapter.onClickBlock = {
//            requireContext().startDeviceActivity(adapter.list[it].toMiot())
//        }
    }

}