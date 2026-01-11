package com.github.miwu.ui.help

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kndroidx.kndroidx
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class HelpViewModel : ViewModel() {
    val list = liveData {
        kndroidx {
            Json.decodeFromString<Help>(
                String(context.assets.open("help/index.json").readBytes())
            ).help.apply {
                emit(this)
            }
        }
    }

    @Serializable
    data class Help(@SerialName("help") val help: ArrayList<Item>) {
        @Serializable
        data class Item(
            @SerialName("title") var title: String,
            @SerialName("content") val content: String
        )
    }
}