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
 * @date 2023/08/28 18:22
 * Description:
 */
class ButtonFanItemAdapter(
    val list: List<PropertiesValue>,
    val type: String,
    val context: Context
) :
    RecyclerView.Adapter<ButtonFanItemAdapter.ViewHolder>() {
    private var block: ((Int) -> Unit)? = null
    private var blockLong: ((Int) -> Unit)? = null
    private var index: Int? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val img: ImageView = view.findViewById(R.id.miSwitchButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_mi_mode, parent, false)
        //list.sortBy { !it.isOnline } 不能在这里调用
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        var title = getTypeTitle(position)

        holder.img.background = ContextCompat.getDrawable(
            context,
            if (position == if (ContentTypeFan.FAN_LEVEL.value == type) (index
                    ?: 0) - 1 else index
            ) R.drawable.bg_swicth_button_on else R.drawable.bg_switch_button_gray
        )
        holder.title.text = title
//        holder.itemView.addTouchScale()
        holder.itemView.setOnClickListener {
            block?.invoke(if (ContentTypeFan.FAN_LEVEL.value == type) position + 1 else position)
        }
        holder.itemView.setOnLongClickListener {
            blockLong?.invoke(if (ContentTypeFan.FAN_LEVEL.value == type) position + 1 else position)
            true
        }
    }

    fun setIndex(index: Int, result: (title: String) -> Unit) {
        this.index = index
        var title = getTypeTitle(if (ContentTypeFan.FAN_LEVEL.value == type) index - 1 else index)
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
            ContentTypeFan.MODE.value -> {
                try {
                    context.getString(
                        context.resources.getIdentifier(
                            ContentFanMode.fromValue(list[position].description).toString(),
                            "string",
                            context.packageName
                        )
                    )
                } catch (e: Exception) {
                    list[position].description
                }
            }

            ContentTypeFan.FAN_LEVEL.value -> {
                try {
                    context.getString(
                        context.resources.getIdentifier(
                            ContentFanLevel.fromValue(list[position].description)
                                .toString(),
                            "string",
                            context.packageName
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

enum class ContentTypeFan(val value: String) {
    MODE("mode"),
    FAN_LEVEL("fan-level");
}

enum class ContentFanLevel(val value: String) {
    Level1("Level1"),
    Level2("Level2"),
    Level3("Level3"),
    Level4("Level4");

    companion object {
        private val map = ContentFanLevel.values().associateBy(ContentFanLevel::value)

        fun fromValue(value: String): ContentFanLevel? = map[value]
    }
}

enum class ContentFanMode(val value: String) {
    Straight("Straight Wind"),
    Natural("Natural Wind"),
    Smart("Smart"),
    Sleep("Sleep");

    companion object {
        private val map = ContentFanMode.values().associateBy(ContentFanMode::value)

        fun fromValue(value: String): ContentFanMode? = map[value]
    }
}