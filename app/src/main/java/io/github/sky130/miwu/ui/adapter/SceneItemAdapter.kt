package io.github.sky130.miwu.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.sky130.miwu.R
import io.github.sky130.miwu.logic.model.mi.MiDevice
import io.github.sky130.miwu.logic.model.mi.MiScene
import io.github.sky130.miwu.util.GlideUtils
import io.github.sky130.miwu.util.ViewUtils.addTouchScale

class SceneItemAdapter(val list: List<MiScene>) :
    RecyclerView.Adapter<SceneItemAdapter.ViewHolder>() {
    private var block: ((Int) -> Unit)? = null
    private var blockLong: ((Int) -> Unit)? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val img: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_mi_scene, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = list[position].sceneName
        val url = list[position].icon
        if (url.isNotEmpty())
            GlideUtils.loadImg(url, holder.img)
        else
            holder.img.setImageResource(R.drawable.ic_mi_scene)
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