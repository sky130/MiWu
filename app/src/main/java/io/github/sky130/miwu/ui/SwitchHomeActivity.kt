package io.github.sky130.miwu.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.sky130.miwu.R
import io.github.sky130.miwu.databinding.ActivitySwitchHomeBinding
import io.github.sky130.miwu.logic.dao.HomeDAO
import io.github.sky130.miwu.ui.adapter.HomeItemAdapter

class SwitchHomeActivity : AppCompatActivity() {
    lateinit var binding: ActivitySwitchHomeBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySwitchHomeBinding.inflate(layoutInflater)
        binding.title.setBackListener(null,this)
        binding.title.setTitle("切换家庭")
        binding.recycler.adapter = HomeItemAdapter().apply {
            setOnClickListener {
                HomeDAO.setHomeIndex(it)
                this.notifyDataSetChanged()
            }
        }
        setContentView(binding.root)
    }
}