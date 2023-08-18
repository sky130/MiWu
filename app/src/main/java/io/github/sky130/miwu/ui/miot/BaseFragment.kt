@file:Suppress("UNCHECKED_CAST")

package io.github.sky130.miwu.ui.miot

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.fragment.app.Fragment
import io.github.sky130.miwu.ui.DeviceActivity

open class BaseFragment(private var mTitle: String = "") : Fragment() {
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            (msg.obj as () -> Unit)()
        }
    }
    fun runOnUiThread(block: () -> Unit) {
        handler.sendMessage(Message().apply {
            this.obj = block
        })
    }

    fun getTitle() = mTitle


    fun setTitle(str: String) {
        mTitle = str
    }

    fun getDid(): String {
        return (context as DeviceActivity).did
    }

    fun getModel(): String {
        return (context as DeviceActivitiy).model 
    }



}