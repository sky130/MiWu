package miwu.android.wrapper.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import miwu.android.icon.AndroidIcon
import miwu.support.api.Controller
import miwu.support.base.MiwuWidget
import miwu.icon.NoneIcon

abstract class BaseMiwuWrapper<T>(val context: Context, val widget: MiwuWidget<T>) : Controller {

    private var canUpdate = true
    abstract val view: View
    val description get() = widget.description
    val descriptionTranslation get() = widget.descriptionTranslation
    val translateHelper get() = widget.translateHelper
    val actionName get() = widget.actionName
    val serviceName get() = widget.serviceName
    val propertyName get() = widget.propertyName
    val siid get() = widget.siid
    val piid get() = widget.piid
    val aiid get() = widget.aiid
    val icon get() = widget.icon
    val valueList get() = widget.valueList
    val valueRange get() = widget.valueRange
    val valueStep get() = widget.valueStep
    val valueOriginUnit get() = widget.valueOriginUnit
    val valueUnit get() = widget.valueUnit
    val defaultValue get() = widget.defaultValue

    val Pair<T, T>.from get() = first
    val Pair<T, T>.to get() = second

    abstract fun onUpdateValue(value: T)

    fun AndroidIcon(block: AndroidIcon.() -> Unit) {
        if (icon is AndroidIcon) (icon as AndroidIcon).block()
    }

    fun NoneIcon(block: NoneIcon.() -> Unit) {
        if (icon is NoneIcon) (icon as NoneIcon).block()
    }

    fun stopUpdate() {
        canUpdate = false
    }

    fun continueUpdate() {
        canUpdate = true
    }

    @Suppress("UNCHECKED_CAST")
    override fun onUpdateValue(
        siid: Int, piid: Int, value: Any
    ) {
        if (siid != this.siid || piid != this.piid) return
        if (!canUpdate) return
        onUpdateValue(value as T)
    }

    fun update(value: T) {
        widget.update(value)
    }

    init {
        widget.bind(this)
    }

    open fun init() {
        initWrapper()
    }

    abstract fun initWrapper()

    protected inline fun <reified VB : ViewBinding> viewBinding(crossinline inflate: (LayoutInflater) -> VB) = lazy { inflate(LayoutInflater.from(context)) }

    @Suppress("UNCHECKED_CAST")
    @Deprecated("avoid unchecked cast", replaceWith= ReplaceWith("viewBinding(inflate: (LayoutInflater) -> VB)"))
    protected inline fun <reified VB : ViewBinding> viewBinding() =
        lazy {
            (VB::class.java.getDeclaredMethod(
                "inflate",
                LayoutInflater::class.java,
            ).invoke(null, LayoutInflater.from(context)) as VB)
        }

}