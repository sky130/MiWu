package com.github.miwu.view

import android.os.Handler
import android.os.Looper
import android.os.Message

interface ViewExtra {


    fun runOnUiThread(block: () -> Unit) {
        com.github.miwu.view.Handler.handler.sendMessage(Message().apply {
            this.obj = block
        })
    }

    fun getDefaultValue(type: String): Any {
        return when (type) {
            "String" -> {
                ""
            }

            "Long" -> {
                0L
            }

            "Int" -> {
                0
            }

            "Float" -> {
                0f
            }

            "Double" -> {
                0.toDouble()
            }

            "Boolean" -> {
                false
            }

            else -> {
                ""
            }
        }
    }

}

object Handler {
    val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            (msg.obj as () -> Unit)()
        }
    }
}