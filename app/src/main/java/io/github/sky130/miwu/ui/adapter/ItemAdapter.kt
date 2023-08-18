package io.github.sky130.miwu.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.sky130.miwu.R
import io.github.sky130.miwu.logic.model.mi.MiDevice
import io.github.sky130.miwu.util.GlideUtils
import io.github.sky130.miwu.util.ViewUtils.addTouchScale

class ItemAdapter(val list: List<MiDevice>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    private var block: ((Int) -> Unit)? = null
    private var blockLong: ((Int) -> Unit)? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val room: TextView = view.findViewById(R.id.room)
        val desc: TextView = view.findViewById(R.id.desc)
        val img: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_mi_device, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isOnline = list[position].isOnline
        holder.title.text = list[position].deviceName
        holder.desc.text = if (isOnline) {
            "可用"
        } else {
            "设备不在线"
        }
        val url = list[position].iconUrl
        if (url.isNotEmpty())
            GlideUtils.loadImg(url, holder.img)
        holder.room.text = list[position].roomName
        holder.itemView.addTouchScale()
        holder.itemView.setOnClickListener {
            if (isOnline)
                block?.invoke(position)
        }
        holder.itemView.setOnLongClickListener {
            if (isOnline)
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