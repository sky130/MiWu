package com.github.miwu.miot.manager

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.LinearLayout
import com.github.miwu.MainApplication.Companion.miot
import com.github.miwu.miot.widget.MiotBaseWidget
import kndroidx.extension.RunnableX
import kndroidx.extension.dp
import kndroidx.extension.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miot.kotlin.helper.Action
import miot.kotlin.helper.GetAtt
import miot.kotlin.helper.SetAtt
import miot.kotlin.model.att.DeviceAtt
import miot.kotlin.model.miot.MiotDevices
import miot.kotlin.utils.call
import miot.kotlin.utils.onSuccess

class MiotDeviceManager(
    internal val device: MiotDevices.Result.Device,
    private val miotLayout: ViewGroup,
) {

    private val job = Job()
    private val scope = CoroutineScope(job)
    private val handler = Handler(Looper.getMainLooper())
    private var delayMillis = 0L
    private val runnable = RunnableX {
        refresh()
        delay()
    }

    @get:Synchronized
    private val viewList = arrayListOf<MiotBaseWidget<*>>()

    fun addView(view: MiotBaseWidget<*>, index: Int = -1) {
        view.setManager(this)
        viewList.add(view)
        miotLayout.apply {
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, // 宽度为 MATCH_PARENT（填满父容器）
                ViewGroup.LayoutParams.WRAP_CONTENT // 高度为 WRAP_CONTENT（根据内容自适应）
            ).apply {
                setMargins(0, 0, 0, 10.dp)
            }
            if (index != -1) {
                addView(view, index, params)
            } else {
                addView(view, params)
            }
            requestLayout()
            invalidate()
        }
    }


    inline fun <reified V : MiotBaseWidget<*>> createView(
        layout: ViewGroup,
        siid: Int = -1,
        piid: Int = -1,
    ) = layout.context.let { context ->
        V::class.java.getDeclaredConstructor(
            Context::class.java
        ).newInstance(context).apply {
            this.siid = siid
            this.piid = piid
            this.setManager(this@MiotDeviceManager)
        }
    }

    fun post(delayMillis: Long) {
        if (delayMillis < 350) return
        this.delayMillis = delayMillis
        runnable.run()
        for (i in viewList) {
            i.init()
        }
    }

    fun stopRefresh() {
        job.cancelChildren()
        handler.removeCallbacks(runnable)
    }

    fun delay(millis: Long = this.delayMillis) {
        handler.postDelayed(runnable, millis)
    }

    fun destroy() {
        delayMillis = 0
        handler.removeCallbacks(runnable)
        job.cancel()
    }

    fun putValue(value: Any, siid: Int, piid: Int) {
        job.cancelChildren()
        handler.removeCallbacks(runnable)
        scope.launch(Dispatchers.IO) {
            miot.setDeviceAtt(device, arrayOf(SetAtt(siid, piid, value)))
            delay()
        }
    }

    fun doAction(siid: Int, aiid: Int, isOut: Boolean = false, vararg `in`: Any) {
        scope.launch(Dispatchers.IO) {
            miot.doAction(device, siid, aiid, *`in`).call().apply action@{
                onSuccess {
                    it.result?.out?.apply {
                        if (!isOut) return@apply
                        for (view in viewList) {
                            if ((siid in view.actions.map { it.first } && aiid in view.actions.map { it.second.iid })) {
                                withContext(Dispatchers.Main) {
                                    view.onActionFinish(siid, aiid, this)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun refresh() {
        scope.launch(Dispatchers.IO) {
            val attList = arrayListOf<GetAtt>()
            for (i in viewList) {
                if (i.piid != -1) i.apply {
                    attList.add(GetAtt(siid, piid))
                }

                if (i.properties.isNotEmpty()) {
                    attList.addAll(i.properties.filter { "read" in it.second.access }
                        .map { GetAtt(it.first, it.second.iid) })
                }
            }
            if (attList.isEmpty()) return@launch
            miot.getDeviceAtt(device, attList.toTypedArray()).call().apply {
                onSuccess {
                    refreshValue(it.result ?: return@onSuccess)
                }
            }
        }
    }

    private suspend fun refreshValue(list: List<DeviceAtt.Att>) = withContext(Dispatchers.Main) {
        for (att in list) {
            for (view in viewList) {
                if ((att.siid == view.siid && att.piid == view.piid)) {
                    view.onValueChange(att.value ?: continue)
                }
                if ((att.siid in view.properties.map { it.first } && att.piid in view.properties.map { it.second.iid })) {
                    view.onValueChange(att.siid, att.piid, att.value ?: continue)
                }
            }
        }
    }
}

