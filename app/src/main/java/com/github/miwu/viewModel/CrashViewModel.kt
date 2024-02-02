package com.github.miwu.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.github.miwu.MainApplication.Companion.miot
import com.github.miwu.logic.handler.CrashHandler
import com.github.miwu.logic.preferences.AppPreferences
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.ui.crash.model.CrashItem
import kndroidx.KndroidX.context
import kndroidx.extension.log
import kndroidx.extension.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miot.kotlin.Miot
import miot.kotlin.model.miot.MiotDevices
import miot.kotlin.model.miot.MiotHomes
import miot.kotlin.model.miot.MiotUserInfo
import java.io.File

class CrashViewModel : ViewModel() {

    val crashText = MutableLiveData<String>("")
    val crashPath get() = CrashHandler.PATH

    fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            File(CrashHandler.PATH).apply {
                if (isDirectory) {
                    val list = listFiles() ?: return@apply
                    list.sortedBy { it.nameWithoutExtension }
                    crashText.postValue(list.last().readText())
                }
            }
        }
    }


}