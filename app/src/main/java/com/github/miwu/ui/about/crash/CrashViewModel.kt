package com.github.miwu.ui.about.crash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.miwu.logic.handler.CrashHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class CrashViewModel : ViewModel() {

    val crashText = MutableLiveData<String>("")
    val crashPath get() = CrashHandler.PATH

    fun load() {
//        viewModelScope.launch(Dispatchers.IO) {
//            File(CrashHandler.PATH).apply {
//                if (isDirectory) {
//                    val list = listFiles() ?: return@apply
//                    list.sortedBy { it.nameWithoutExtension }
//                    crashText.postValue(list.last().readText())
//                }
//            }
//        }
    }


}