package com.github.miwu.ui.smart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.github.miwu.databinding.ItemMiwuSmartBinding
import com.github.miwu.databinding.ItemMiwuSmartDescBinding
import com.github.miwu.databinding.ItemMiwuSmartDeviceBinding
import com.github.miwu.logic.database.model.MiwuDevice
import com.github.miwu.logic.repository.DeviceArrayList
import com.github.miwu.logic.repository.model.SmartHome
import com.github.miwu.logic.repository.model.SmartHome.Companion.toBase
import com.github.miwu.ui.smart.adapter.SmartAdapter.BaseViewHolder.*

class SmartAdapter : RecyclerView.Adapter<SmartAdapter.BaseViewHolder>() {


    sealed class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        class TitleViewHolder(val binding: ItemMiwuSmartBinding) : BaseViewHolder(binding.root)
        class DeviceViewHolder(val binding: ItemMiwuSmartDeviceBinding) :
            BaseViewHolder(binding.root)
    }

    var list: List<SmartHome.Base> = arrayListOf()

    fun updateList(newList: List<SmartHome>) {
        val new = arrayListOf<SmartHome.Base>().apply {
            newList.forEach {
                add(it.toBase())
                it.deviceList.forEach { device ->
                    add(device.toBase())
                }
            }
        }
        val diffResult = DiffUtil.calculateDiff(DiffCallback(list, new))
        diffResult.dispatchUpdatesTo(this)
        this.list = new
    }

    inline fun <reified VB : ViewBinding> viewBinding(parent: ViewGroup): VB {
        val inflate = VB::class.java.getDeclaredMethod(
            "inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java
        )
        return inflate.invoke(
            null, LayoutInflater.from(
                parent.context
            ), parent, false
        ) as VB
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is SmartHome.Device -> 0
            is SmartHome.Home -> 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            0 -> DeviceViewHolder(viewBinding(parent))
            else -> TitleViewHolder(viewBinding(parent))
        }
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = list[position]
        when (holder) {
            is DeviceViewHolder -> {
                item as SmartHome.Device
                holder.binding.item = item.value
            }

            is TitleViewHolder -> {
                item as SmartHome.Home
                holder.binding.item = item
            }
        }
    }

    inner class DiffCallback(
        private val oldList: List<SmartHome.Base>,
        private val newList: List<SmartHome.Base>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val old = oldList[oldItemPosition]
            val new = newList[newItemPosition]
            if (old is SmartHome.Home && new is SmartHome.Home && old.value.name == old.value.name && old.value.textList == new.value.textList) return true
            if (old is SmartHome.Device && new is SmartHome.Device && old.value == new.value) return true
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}