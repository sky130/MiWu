package com.github.miwu.ui.manager

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.VISIBLE
import com.github.miwu.logic.network.DeviceService
import com.github.miwu.widget.MiButtonCard
import com.github.miwu.widget.MiIndicatorsCard
import com.github.miwu.widget.MiRoundSeekBarCard
import com.github.miwu.widget.MiSeekBarCard
import com.github.miwu.widget.MiSwitchCard
import com.github.miwu.widget.MiTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MiWidgetManager {

    private val piidViewMap = HashMap<String, PiidViewItem>()
    private val aiidViewMap = HashMap<String, AiidViewItem>()
    private val notifyBlockMap = HashMap<String, MiWidgetManager.() -> Unit>()
    private val job = Job()
    private val scope = CoroutineScope(job)
    private lateinit var did: String
    private val handler = Handler(Looper.getMainLooper())
    private var time = 0L
    private val runnable = Runnable { update() }

    private data class PiidViewItem(
        val view: View,
        val siid: Int,
        val piid: Int,
        val defaultValue: Any,
        val max: Int = 0,
        val min: Int = 0,
        val step: Int = 0,
    )

    private data class AiidViewItem(
        val view: View,
        val siid: Int,
        val aiid: Int,
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
        piidViewMap[tag] = PiidViewItem(view, siid, piid, defaultValue, max, min, step)
    }

    fun addView(view: View, tag: String, siid: Int, aiid: Int) {
        aiidViewMap[tag] = AiidViewItem(view, siid, aiid)
    }

    // 默认异步操作
    fun addNotifyBlock(tag: String, block: MiWidgetManager.() -> Unit) {
        notifyBlockMap[tag] = block
    }

    fun setDid(str: String) {
        did = str
    }

    fun init() {
        for (i in piidViewMap.values) {
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

                is MiIndicatorsCard -> {
                    view.setProgressMax(i.max)
                    view.setOnProgressChangerListener {
                        launch {
                            DeviceService.setDeviceATT(did, i.siid, i.piid, it)
                        }
                    }
                }

                is MiButtonCard->{
                    view.setOnClickListener {
                        launch {
                            DeviceService.setDeviceATT(did, i.siid, i.piid, i.defaultValue)
                        }
                    }
                }
            }
        }
        for (i in aiidViewMap.values) {
            val view = i.view
            view.visibility = VISIBLE
            view.setOnClickListener {
                launch {
                    DeviceService.doAction(did, i.siid, i.aiid)
                }
            }
        }
    }

    fun notify(time: Long) {
        if (time < 1000) return
        cancelNotify()
        this.time = time
        handler.postDelayed(runnable, time)
    }

    fun cancelNotify() {
        time = 0
        handler.removeCallbacks(runnable)
    }


    @Synchronized
    fun update() {
        val jobs = mutableListOf<Deferred<Unit>>()
        for (i in piidViewMap.values) {
            jobs.add(async {
                val att = DeviceService.getDeviceATT(did, i.siid, i.piid) ?: return@async
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
                            view.setChecked(value as Boolean, false)
                        }

                        is MiTextView -> {
                            view.setText(value.toString())
                        }

                        is MiIndicatorsCard -> {
                            val on = DeviceService.getDeviceATT(did, i.siid, i.piid)
                                ?: return@runOnUiThread
                            val boolean = on.value as Boolean
                            if (boolean) {
                                view.setProgress(0, false)
                            } else {
                                view.setProgress((value as Number).toInt(), false)
                            }
                        }
                    }
                }
            })
        }
        for (i in notifyBlockMap.values) {
            jobs.add(async {
                i()
            })
        }
        scope.launch {
            jobs.awaitAll()
            notify(time)
        }
    }

    fun async(block: () -> Unit): Deferred<Unit> { // 用于网络请求
        return scope.async {
            withContext(Dispatchers.IO) {
                block()
            }
        }
    }

    fun launch(block: () -> Unit) { // 用于网络请求
        scope.launch {
            handler.removeCallbacks(runnable)
            withContext(Dispatchers.IO) {
                block()
            }
            notify(time)
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
        piidViewMap.remove(tag)
        aiidViewMap.remove(tag)
    }

    fun removeNotify(tag: String) {
        notifyBlockMap.remove(tag)
    }

}