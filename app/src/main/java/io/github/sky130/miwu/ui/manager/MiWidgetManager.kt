package io.github.sky130.miwu.ui.manager

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import io.github.sky130.miwu.logic.network.DeviceService
import io.github.sky130.miwu.widget.MiRoundSeekBarCard
import io.github.sky130.miwu.widget.MiSeekBarCard
import io.github.sky130.miwu.widget.MiSwitchButton
import io.github.sky130.miwu.widget.MiSwitchCard
import io.github.sky130.miwu.widget.MiTextView
import io.github.sky130.miwu.widget.ViewExtra
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MiWidgetManager {

    private val viewMap = HashMap<String, ViewItem>()
    private val job = Job()
    private val scope = CoroutineScope(job)
    private lateinit var did: String
    private val handler = Handler(Looper.getMainLooper())
    private var time = 0L
    private val runnable = object : Runnable {
        override fun run() {
            update()
            handler.postDelayed(this, time)
        }
    }

    private data class ViewItem(
        val view: View,
        val siid: Int,
        val piid: Int,
        val defaultValue: Any,
        val max: Int = 0,
        val min: Int = 0,
        val step: Int = 0,
    )

    fun addView(
        view: View,
        tag: String,
        siid: Int,
        piid: Int,
        defaultValue: Any,
        max: Int = 0,
        min: Int = 0,
        step: Int = 0,
    ) { // 添加一个View实例来进行管理
        viewMap[tag] = ViewItem(view, siid, piid, defaultValue, max, min, step)
    }

    fun setDid(str: String) {
        did = str
    }

    fun init() {
        for (i in viewMap.values) {
            val view = i.view
            view.visibility = VISIBLE
            when (view) {
                is MiRoundSeekBarCard -> {
                    view.getSeekBar().apply {
                        setMaxProgress(i.max)
                        setMinProgress(i.min)
                        setOnProgressChanged(true) {
                            launch {
                                DeviceService.setDeviceATT(did, i.siid, i.piid, it)
                            }
                        }
                    }
                    view.setCurrentProgress(i.min)
                }

                is MiSeekBarCard -> {
                    view.getSeekBar().apply {
                        setMaxProgress(i.max)
                        setMinProgress(i.min)
                        setOnProgressChanged(true) {
                            launch {
                                DeviceService.setDeviceATT(did, i.siid, i.piid, it)
                            }
                        }
                    }
                    view.setCurrentProgress(i.min)
                }

                is MiSwitchCard -> {
                    view.setOnStatusChangedListener {
                        launch {
                            DeviceService.setDeviceATT(did, i.siid, i.piid, it)
                        }
                    }
                }

                is MiTextView -> {

                }
            }
        }
    }

    fun notify(time: Long) {
        if (time <= 500) return
        this.time = time
        handler.postDelayed(runnable, time)
    }

    fun cancelNotify(){
        handler.removeCallbacks(runnable)
    }

    @Synchronized
    fun update() {
        for (i in viewMap.values) {
            launch {
                val att =
                    DeviceService.getDeviceATT(did, i.siid, i.piid) ?: return@launch
                val view = i.view
                val value = att.value
                runOnUiThread {
                    when (view) {
                        is MiRoundSeekBarCard -> {
                            view.setCurrentProgress(value as Number)
                        }

                        is MiSeekBarCard -> {
                            view.setCurrentProgress(value as Number)
                        }

                        is MiSwitchCard -> {
                            view.setChecked(value as Boolean)
                        }

                        is MiTextView -> {
                            view.setText(value.toString())
                        }
                    }
                }
            }
        }
    }

    private fun launch(block: () -> Unit) { // 用于网络请求
        scope.launch {
            withContext(Dispatchers.IO) {
                block()
            }
        }
    }

    private fun runOnUiThread(block: () -> Unit) {
        scope.launch {
            withContext(Dispatchers.Main) {
                block()
            }
        }
    }

    fun removeView(tag: String) {
        viewMap.remove(tag)
    }

}