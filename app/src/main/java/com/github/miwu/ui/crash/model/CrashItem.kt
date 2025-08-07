package com.github.miwu.ui.crash.model

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

data class CrashItem(val title: String, val message: String, val time: String, val all: String) {
    companion object {
        @SuppressLint("SimpleDateFormat")
        fun fromString(time: String, str: String): CrashItem {
            val timeFormat = SimpleDateFormat("yy/MM/dd HH:mm").format(Date(time.toLong()))
            val list = str.split("\n")
            val title = list[0]
            val message = list.subList(9, list.size).joinToString().trimIndent()
            return CrashItem(title, message, timeFormat, str)
        }
    }
}

fun main() {
    try {
        throw Exception("test")
    } catch (e: Exception) {
        throw e
    }
}