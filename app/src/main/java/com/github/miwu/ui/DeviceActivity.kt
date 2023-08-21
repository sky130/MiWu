package com.github.miwu.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.miwu.R
import com.github.miwu.databinding.ActivityDeviceBinding
import com.github.miwu.ui.miot.BaseFragment
import com.github.miwu.ui.miot.DeviceUtils

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
        binding.title.setBackListener(null,this)
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
        binding.title.setTitle(getString(R.string.loading))
        DeviceUtils.getDeviceFragment(model, specType) { fragment ->
            runOnUiThread {
                fragment.let { deviceFragment ->
                    mFragment = deviceFragment
                    binding.title.setTitle(name)
                    supportFragmentManager.apply {
                        if (!isDestroyed) {
                            beginTransaction().apply {
                                add(R.id.fragment_container, mFragment)
                                attach(mFragment)
                                commitAllowingStateLoss()
                                executePendingTransactions()
                            }
                        }
                    }
                }
            }
        }
    }
}