package io.github.sky130.miwu.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.sky130.miwu.R
import io.github.sky130.miwu.databinding.ActivityDeviceBinding
import io.github.sky130.miwu.ui.miot.BaseFragment
import io.github.sky130.miwu.ui.miot.DeviceUtils

class DeviceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeviceBinding
    lateinit var model: String
    lateinit var did: String
    lateinit var name: String
    lateinit var specType: String

    private lateinit var mFragment: BaseFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        binding = ActivityDeviceBinding.inflate(layoutInflater)
        addFragment()
        setContentView(binding.root)
    }

    private fun init() {
        model = intent.getStringExtra("model") ?: ""
        did = intent.getStringExtra("did") ?: ""
        name = intent.getStringExtra("name") ?: ""
        specType = intent.getStringExtra("specType") ?: ""
    }

    private fun addFragment() {
        binding.title.setTitle("加载中")
        DeviceUtils.getDeviceFragment(model, specType) {
            runOnUiThread {
                mFragment = it
                binding.title.setActivity(this)
                binding.title.setTitle(name)
                this.supportFragmentManager.beginTransaction().apply {
                    add(R.id.fragment_container, mFragment)
                    attach(mFragment)
                    commit()
                }
                this.supportFragmentManager.executePendingTransactions()
            }
        }
    }
}