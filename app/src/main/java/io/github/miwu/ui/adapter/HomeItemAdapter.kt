package io.github.sky130.miwu.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.sky130.miwu.R
import io.github.sky130.miwu.logic.dao.HomeDAO
import io.github.sky130.miwu.logic.model.mi.MiDevice
import io.github.sky130.miwu.logic.model.mi.MiHome
import io.github.sky130.miwu.util.GlideUtils
import io.github.sky130.miwu.util.TextUtils.log
import io.github.sky130.miwu.util.ViewUtils.addTouchScale

class HomeItemAdapter() :
    RecyclerView.Adapter<HomeItemAdapter.ViewHolder>() {
    private var block: ((Int) -> Unit)? = null
    private var blockLong: ((Int) -> Unit)? = null
    val list: ArrayList<MiHome>
        get() = HomeDAO.getHomeList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.device_item_name)
        val type: TextView = view.findViewById(R.id.device_item_room)
        val desc: TextView = view.findViewById(R.id.device_item_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_mi_home, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = list[position].homeName
        if (position == HomeDAO.getHomeIndex()) {
            holder.desc.text = "当前选中"
        } else {
            holder.desc.text = ""
        }
        holder.type.text = if (list[position].isShareHome) {
            "共享家庭"
        } else {
            "主家庭"
        }
        holder.itemView.addTouchScale()
        holder.itemView.setOnClickListener {
            block?.invoke(position)
        }
        holder.itemView.setOnLongClickListener {
            blockLong?.invoke(position)
            true
        }
    }

    fun setOnClickListener(block: ((Int) -> Unit)) {
        this.block = block
    }

    fun setOnLongClickListener(block: ((Int) -> Unit)) {
        this.blockLong = block
    }

    override fun getItemCount(): Int {
        return list.size
    }
}