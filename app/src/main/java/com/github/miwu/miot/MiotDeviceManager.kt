package com.github.miwu.miot

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.marginBottom
import com.github.miwu.MainApplication.Companion.miot
import com.github.miwu.miot.widget.MiotBaseWidget
import kndroidx.extension.RunnableX
import kndroidx.extension.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miot.kotlin.helper.GetAtt
import miot.kotlin.helper.SetAtt
import miot.kotlin.model.att.DeviceAtt
import miot.kotlin.model.att.SpecAtt
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
        delay()
    }

    @get:Synchronized
    private val viewList = arrayListOf<MiotBaseWidget<*>>()

    fun addView(view: MiotBaseWidget<*>) {
        view.setManager(this)
        viewList.add(view)
        view.init()
        miotLayout.apply {
            addView(
                view,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, // 宽度为 MATCH_PARENT（填满父容器）
                    ViewGroup.LayoutParams.WRAP_CONTENT // 高度为 WRAP_CONTENT（根据内容自适应）
                ).apply {
                    setMargins(0, 0, 0, 10.dp)
                }
            )
            requestLayout()
            invalidate()
        }
    }

    inline fun <reified V : MiotBaseWidget<*>> createAddView(
        context: Context,
        siid: Int,
        piid: Int,
        obj: Any? = null,
    ) = createView<V>(context, siid, piid, obj).also { addView(it) }

    inline fun <reified V : MiotBaseWidget<*>> createAddView(
        viewGroup: ViewGroup,
        siid: Int,
        piid: Int,
        obj: Any? = null,
    ) = createView<V>(viewGroup.context, siid, piid, obj).also { addView(it) }

    inline fun <reified V : MiotBaseWidget<*>> createView(
        viewGroup: ViewGroup,
        siid: Int,
        piid: Int,
        obj: Any? = null,
    ) = createView<V>(viewGroup.context, siid, piid, obj)

    inline fun <reified V : MiotBaseWidget<*>> createView(
        context: Context,
        siid: Int,
        piid: Int,
        obj: Any? = null,
    ) =
        context.let {
            V::class.java.getDeclaredConstructor(
                Context::class.java
            ).newInstance(it).apply {
                this.siid = siid
                this.piid = piid
                if (obj is SpecAtt.Service.Property) {
                    this.property = obj
                }
                if (obj is SpecAtt.Service.Action) {
                    this.action = obj
                }
                this.setManager(this@MiotDeviceManager)
            }
        }

    fun post(delayMillis: Long) {
        if (delayMillis < 350) return
        this.delayMillis = delayMillis
        runnable.run()
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

    fun refresh() {
        scope.launch(Dispatchers.IO) {
            val attList = arrayListOf<GetAtt>()
            for (i in viewList) {
                i.apply {
                    attList.add(GetAtt(siid, piid))
                }
            }
            if (attList.isEmpty()) return@launch
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

