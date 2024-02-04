package com.github.miwu.miot.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.miwu.R
import com.github.miwu.databinding.MiotWidgetItemFeederPlanBinding
import com.github.miwu.widget.CustomLinearLayoutManager
import kndroidx.extension.compareTo
import kndroidx.extension.log
import kndroidx.extension.string
import com.github.miwu.databinding.MiotWidgetFeederPlanBinding as Binding
import com.github.miwu.databinding.MiotWidgetItemFeederPlanBinding as ItemBinding

// only for mmgg.feeder.inland
class FeederPlanList(context: Context) : MiotBaseWidget<Binding>(context) {
    val action by lazy { actions.first().second }
    private val itemList = arrayListOf<Item>()
    val adapter by lazy { ItemAdapter(itemList) }

    override fun init() {
        doAction(siid, action.iid, true, 0)
        doAction(siid, action.iid, true, 1)
        binding.recycler.layoutManager= CustomLinearLayoutManager(context)
        binding.recycler.adapter = adapter
    }

    override fun onValueChange(value: Any) {

    }

    override fun onActionFinish(siid: Int, aiid: Int, value: Any) {
        value as ArrayList<*>
        val list = value.first() as String
        value.first().log.d()
        list.split(",").map { it.toInt() }.apply {
            subList(0, 5).log.d()
            subList(20, 25).log.d()
            subList(0, 5).toItem().add()
            subList(5, 10).toItem().add()
            subList(10, 15).toItem().add()
            subList(15, 20).toItem().add()
            subList(20, 25).toItem().add()
        }
        itemList.sortBy { it.hour * 100 + it.minute }
        adapter.notifyItemRangeChanged(0, list.length)
    }

    fun Item.add() {
        if (hour <= 24 && minute <= 60)
            itemList.add(this)
    }

    fun List<Int>.toItem() = Item(this[0], this[1], this[2], this[3], this[4].getItemStatus())

    class ItemAdapter(val list: List<Item>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
        class ViewHolder(val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
            ItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding) {
            val item = list[position]
            status.setImageResource(
                when (item.status) {
                    Status.Success -> {
                        num <= "出粮成功"
                        R.drawable.ic_radio_succes
                    }

                    Status.Failed -> {
                        num <= "出粮失败"
                        R.drawable.ic_radio_failed
                    }

                    Status.Wait -> {
                        num <= R.string.feed_num.string.format(item.num, item.num * 5)
                        R.drawable.ic_radio_wait
                    }
                }
            )
            time <= "${item.hour.toTimeString()}:${item.minute.toTimeString()}"
            return@with
        }

        fun Int.toTimeString() = if (this < 10) {
            "0$this"
        } else {
            this.toString()
        }
    }

    private fun Int.getItemStatus() = when (this) {
        1 -> Status.Failed
        0 -> Status.Success
        255 -> Status.Wait
        else -> Status.Wait
    }

    enum class Status {
        Success,
        Failed,
        Wait,
    }

    data class Item(
        val index: Int,
        val hour: Int,
        val minute: Int,
        val num: Int,
        val status: Status
    )
}