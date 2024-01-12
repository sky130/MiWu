package com.github.miwu.miot

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.ViewGroup
import com.github.miwu.MainApplication.Companion.miot
import com.github.miwu.miot.widget.MiotBaseWidget
import kndroidx.extension.RunnableX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miot.kotlin.helper.GetAtt
import miot.kotlin.helper.SetAtt
import miot.kotlin.model.att.DeviceAtt
import miot.kotlin.model.miot.MiotDevices

class MiotDeviceManager(
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
        view.setManager(this)
        viewList.add(view)
        miotLayout.apply {
            addView(
                view,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, // 宽度为 MATCH_PARENT（填满父容器）
                    ViewGroup.LayoutParams.WRAP_CONTENT // 高度为 WRAP_CONTENT（根据内容自适应）
                )
            )
            requestLayout()
            invalidate()
        }
    }

    inline fun <reified V : MiotBaseWidget<*>> createView(
        viewGroup: ViewGroup,
        siid: Int,
        piid: Int,
    ) = createView<V>(viewGroup.context, siid, piid)

    inline fun <reified V : MiotBaseWidget<*>> createView(context: Context, siid: Int, piid: Int) =
        context.let {
            V::class.java.getDeclaredConstructor(
                Context::class.java
            ).newInstance(it).apply {
                this.siid = siid
                this.piid = piid
                this.setManager(this@MiotDeviceManager)
            }
        }

    fun post(delayMillis: Long) {
        if (delayMillis < 350) return
        this.delayMillis = delayMillis
        handler.postDelayed(runnable, delayMillis)
    }

    fun destroy() {
        delayMillis = 0
        handler.removeCallbacks(runnable)
        job.cancel()
    }

    fun putValue(value: Any, siid: Int, piid: Int) {
        handler.removeCallbacks(runnable)
        job.cancelChildren()
        scope.launch(Dispatchers.IO) {
            miot.setDeviceAtt(device, arrayOf(SetAtt(siid, piid, value)))
            post(delayMillis)
        }
    }

    fun refresh() {
        val attList = arrayListOf<GetAtt>()
        for (i in viewList) {
            i.apply {
                attList.add(GetAtt(siid, piid))
            }
        }
        if (attList.isEmpty()) return
        scope.launch(Dispatchers.IO) {
            miot.getDeviceAtt(device, attList.toTypedArray())?.let {
                refreshValue(it.result ?: return@let)
            }
        }
    }

    private suspend fun refreshValue(list: List<DeviceAtt.Att>) = withContext(Dispatchers.Main) {
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

