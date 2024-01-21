package com.github.miwu.miot.manager

import android.os.Handler
import android.os.Looper
import com.github.miwu.MainApplication.Companion.miot
import com.github.miwu.logic.preferences.AppPreferences
import com.github.miwu.miot.quick.MiotBaseQuick
import com.github.miwu.service.QuickActionTileService
import kndroidx.extension.RunnableX
import kndroidx.extension.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miot.kotlin.helper.GetAtt

object MiotQuickManager {
    private val job = Job()
    private val scope = CoroutineScope(job)
    private val handler = Handler(Looper.getMainLooper())
    private const val delayMillis = 10 * 60 * 1000L
    private val runnable = RunnableX {
        if (quickList.isNotEmpty()) refresh()
        delay()
    }
    val quickList = arrayListOf<MiotBaseQuick>()

    init {
        quickList.addAll(AppPreferences.getQuicks())
    }

    fun addQuick(quick: MiotBaseQuick) {
        if (quickList.size >= 4) return
        quickList.add(quick)
        runnable.run()
        AppPreferences.putQuicks(quickList)
        QuickActionTileService.update()
    }

    fun doQuick(position: Int) {
        quickList[position].initValue()
        job.cancelChildren()
        scope.launch(Dispatchers.IO) {
            quickList[position].doAction()
        }
    }

    fun delay() {
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, delayMillis)
    }

    fun refresh(must: Boolean = false) {
        var isValueChanged = false
        scope.launch(Dispatchers.IO) {
            for (i in quickList) {
                when (i) {
                    is MiotBaseQuick.DeviceQuick<*> -> {
                        launch {
                            miot.getDeviceAtt(i.device, arrayOf(GetAtt(i.siid, i.piid)))?.let {
                                it.result?.let { att ->
                                    att[0].value?.let { it1 ->
                                        isValueChanged = i.value != it1
                                        i.putValue(it1)
                                    }
                                }
                            }
                        }
                    }

                    else -> {

                    }
                }
            }
            "isValueChanged ${isValueChanged || must}".log.d()
            if (must || isValueChanged) withContext(Dispatchers.Main) {
                QuickActionTileService.update()
            }
        }
    }


}