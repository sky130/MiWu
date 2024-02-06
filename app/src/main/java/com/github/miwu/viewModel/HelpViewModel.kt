package com.github.miwu.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.miwu.MainApplication.Companion.gson
import com.google.gson.annotations.SerializedName
import kndroidx.kndroidx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HelpViewModel : ViewModel() {
    val list = MutableLiveData<ArrayList<Help.Item>>(arrayListOf())

    fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            kndroidx {
                gson.fromJson(
                    String(context.assets.open("help/index.json").readBytes()),
                    Help::class.java
                ).help.apply {
                    list.postValue(this)
                }
            }
        }
    }

    data class Help(@SerializedName("help") val help: ArrayList<Item>) {
        data class Item(
            @SerializedName("title") var title: String,
            @SerializedName("content") val content: String
        )
    }
}