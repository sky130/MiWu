package com.github.miwu.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.github.miwu.R
import com.github.miwu.databinding.MiButtonIndicatorsCardBinding
import com.github.miwu.logic.model.miot.PropertiesValue
import com.github.miwu.ui.adapter.ModeItemAdapter

/**
 * @author ch.hu
 * @date 2023/08/28 17:55
 * Description:
 */
class MiButtonIndicatorsCard(
    context: Context,
    attr: AttributeSet,
) : ConstraintLayout(context, attr) {

    private var binding: MiButtonIndicatorsCardBinding
    private val listeners = ArrayList<(Int) -> Unit>()
    private lateinit var datas: List<PropertiesValue>
    private lateinit var type: String
    private lateinit var modeItemAdapter: ModeItemAdapter

    init {
        binding = MiButtonIndicatorsCardBinding.inflate(LayoutInflater.from(context), this, true)
        context.obtainStyledAttributes(attr, R.styleable.MiButtonIndicatorsCard).apply {
            setTitle(getString(R.styleable.MiButtonIndicatorsCard_title).toString())
            recycle()
        }
    }

    fun setTitle(title: String) {
        binding.title.text = title
    }

    fun setDatas(datas: List<PropertiesValue>, type: String) {
        this.type = type
        this.datas = datas
        val gridLayoutManager = GridLayoutManager(context, datas.size)
        binding.recycler.layoutManager = gridLayoutManager
        if (!::modeItemAdapter.isInitialized) {
            modeItemAdapter = ModeItemAdapter(datas, type, context).apply {
                setButtonOnClickListener { level ->
                    listeners.forEach { it(level) }
                }
            }
        }
        binding.recycler.adapter = modeItemAdapter
    }

    fun setButtonOnClickListener(block: (Int) -> Unit) {
        listeners.add(block)
    }

    @SuppressLint("SetTextI18n")
    fun setIndex(index: Int) {
        if (::modeItemAdapter.isInitialized) {
            modeItemAdapter.setIndex(index) {
                binding.value.text = it
            }
        }
    }
}