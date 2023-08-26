package com.github.miwu.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.miwu.R
import com.github.miwu.logic.dao.HomeDAO
import com.github.miwu.logic.model.mi.MiDevice
import com.github.miwu.util.GlideUtils
import com.github.miwu.util.ViewUtils.addTouchScale

class DeviceItemAdapter() :
    RecyclerView.Adapter<DeviceItemAdapter.ViewHolder>() {
    private var block: ((Int) -> Unit)? = null
    private var blockLong: ((Int) -> Unit)? = null
    val list: ArrayList<MiDevice>
        get() = HomeDAO.getHome(HomeDAO.getHomeIndex())!!.deviceList

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.device_item_icon)
        val title: TextView = view.findViewById(R.id.device_item_name)
        val room: TextView = view.findViewById(R.id.device_item_room)
        val desc: TextView = view.findViewById(R.id.device_item_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_mi_device, parent, false)
        //list.sortBy { !it.isOnline } 不能在这里调用
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val isOnline = list[position].isOnline
        holder.title.text = list[position].deviceName
        if (isOnline) {
            holder.desc.text = context.getString(R.string.device_online)
            holder.itemView.isEnabled = true
        } else {
            holder.desc.text = context.getString(R.string.device_offline)
            holder.itemView.isEnabled = false
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