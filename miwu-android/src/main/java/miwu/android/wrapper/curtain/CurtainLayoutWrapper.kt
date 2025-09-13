package miwu.android.wrapper.curtain

import android.content.Context
import miwu.android.databinding.MiotWidgetListButtonBinding
import miwu.android.icon.generated.icon.AndroidIcons
import miwu.android.wrapper.base.MiwuLayoutWrapper
import miwu.annotation.Wrapper
import miwu.support.base.MiwuWidget
import miwu.widget.CurtainLayout

@Wrapper(CurtainLayout::class)
class CurtainLayoutWrapper(context: Context, widget: MiwuWidget<Int>) :
    MiwuLayoutWrapper<Int>(context, widget) {

    override fun onUpdateValue(value: Int) = Unit

    override fun initWrapper() {
        view(MiotWidgetListButtonBinding::inflate) {
            on.setIcon(AndroidIcons.CurtainOpen)
            desc.text = valueList[1].descriptionTranslation
            on.setOnClickListener {
                update(1)
            }
        }
        view(MiotWidgetListButtonBinding::inflate) {
            on.setIcon(AndroidIcons.CurtainClose)
            desc.text = valueList[0].descriptionTranslation
            on.setOnClickListener {
                update(0)
            }
        }
    }

}