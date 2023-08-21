package com.github.miwu.widget

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.github.miwu.widget.Handler.handler

interface ViewExtra {


    fun runOnUiThread(block: () -> Unit) {
        handler.sendMessage(Message().apply {
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