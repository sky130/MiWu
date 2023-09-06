package com.github.miwu.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.miwu.R
import com.github.miwu.logic.model.miot.PropertiesValue

/**
 * @author ch.hu
 * @date 2023/09/06 10:06
 * Description:
 */
class ButtonAirConditionerItemAdapter(
    val list: List<PropertiesValue>, val type: String, val context: Context
) : RecyclerView.Adapter<ButtonAirConditionerItemAdapter.ViewHolder>() {
    private var block: ((Int) -> Unit)? = null
    private var blockLong: ((Int) -> Unit)? = null
    private var index: Int? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val img: ImageView = view.findViewById(R.id.miSwitchButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mi_mode, parent, false)
        //list.sortBy { !it.isOnline } 不能在这里调用
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        var title = getTypeTitle(position)

        holder.img.background = ContextCompat.getDrawable(
            context,
            if (position == index) R.drawable.bg_swicth_button_on else R.drawable.bg_switch_button_gray
        )
        holder.title.text = title
//        holder.itemView.addTouchScale()
        holder.itemView.setOnClickListener {
            block?.invoke(position)
        }
        holder.itemView.setOnLongClickListener {
            blockLong?.invoke(position)
            true
        }
    }

    fun setIndex(index: Int, result: (title: String) -> Unit) {
        this.index = index
        var title = getTypeTitle(index)
        result(title)
        notifyDataSetChanged()
    }

    fun setButtonOnClickListener(block: ((Int) -> Unit)) {
        this.block = block
    }

    fun setButtonOnLongClickListener(block: ((Int) -> Unit)) {
        this.blockLong = block
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getTypeTitle(position: Int): String {
        return when (type) {
            ContentTypeAirConditioner.MODE.value -> {
                try {
                    context.getString(
                        context.resources.getIdentifier(
                            ContentAirConditionerMode.fromValue(list[position].description)
                                .toString(), "string", context.packageName
                        )
                    )
                } catch (e: Exception) {
                    list[position].description
                }
            }

            else -> {
                ""
            }
        }
    }
}

enum class ContentTypeAirConditioner(val value: String) {
    MODE("mode");
}

enum class ContentAirConditionerMode(val value: String) {
    Auto("Auto"), Cool("Cool"), Dry("Dry"), Heat("Heat"), Fan("Fan");

    companion object {
        private val map =
            ContentAirConditionerMode.values().associateBy(ContentAirConditionerMode::value)

        fun fromValue(value: String): ContentAirConditionerMode? = map[value]
    }
}