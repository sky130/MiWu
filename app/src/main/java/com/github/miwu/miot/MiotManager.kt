package com.github.miwu.miot

import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import com.github.miwu.MainApplication.Companion.miot
import com.github.miwu.widget.miot.MiotBaseWidget
import kndroidx.extension.RunnableX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miot.kotlin.helper.GetAtt
import miot.kotlin.model.att.DeviceAtt
import miot.kotlin.model.miot.MiotDevices

class MiotManager(
    private val device: MiotDevices.Result.Device,
    private val miotLayout: ViewGroup,
) {

    private val job = Job()
    private val scope = CoroutineScope(job)
    private val handler = Handler(Looper.getMainLooper())
    private var delayMillis = 0L
    private val runnable = RunnableX {
        refresh()
        post(delayMillis)
    }

    @get:Synchronized
    private val viewList = arrayListOf<MiotBaseWidget<*>>()

    fun addView(view: MiotBaseWidget<*>) {
        viewList.add(view)
    }

    fun post(delayMillis: Long) {
        if (delayMillis < 350) return
        this.delayMillis = delayMillis
        handler.postDelayed(runnable, delayMillis)
    }

    fun cancel() {
        delayMillis = 0
        handler.removeCallbacks(runnable)
    }

    fun refresh() {
        val attList = arrayListOf<GetAtt>()
        for (i in viewList) {
            i.apply {
                attList.add(GetAtt(siid, piid))
            }
        }
        scope.launch(Dispatchers.IO) {
            miot.getDeviceAtt(device, attList.toTypedArray())?.let {
                refreshValue(it.result)
            }
        }
    }

    private suspend fun refreshValue(list: List<DeviceAtt.Att>) = withContext(Dispatchers.IO) {
        for (att in list) {
            viewLoop@ for (view in viewList) {
                if (att.siid == view.siid && att.piid == view.piid) {
                    view.onValueChange(att.value)
                    break@viewLoop
                }
            }
        }
    }
}