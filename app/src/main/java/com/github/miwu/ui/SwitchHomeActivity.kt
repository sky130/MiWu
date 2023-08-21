package com.github.miwu.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.miwu.R
import com.github.miwu.databinding.ActivitySwitchHomeBinding
import com.github.miwu.logic.dao.HomeDAO
import com.github.miwu.ui.adapter.HomeItemAdapter

class SwitchHomeActivity : AppCompatActivity() {
    lateinit var binding: ActivitySwitchHomeBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySwitchHomeBinding.inflate(layoutInflater)
        binding.title.setBackListener(null,this)
        binding.title.setTitle(getString(R.string.switch_home))
        binding.recycler.adapter = HomeItemAdapter().apply {
            setOnClickListener {
                HomeDAO.setHomeIndex(it)
                this.notifyDataSetChanged()
            }
        }
        setContentView(binding.root)
    }
}