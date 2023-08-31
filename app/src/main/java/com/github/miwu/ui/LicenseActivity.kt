package com.github.miwu.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.miwu.databinding.ActivityLicenseBinding

/**
 * @author ch.hu
 * @date 2023/08/31 10:11
 * Description:
 */
class LicenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLicenseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLicenseBinding.inflate(layoutInflater)
        binding.title.setBackListener(null, this)
        setContentView(binding.root)
    }
}