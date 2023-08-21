package com.github.miwu.ui

import android.annotation.SuppressLint
import android.os.Bundle
import com.github.miwu.MainApplication.Companion.loginMsg
import com.github.miwu.base.BaseActivity
import com.github.miwu.logic.dao.HomeDAO
import com.github.miwu.startActivity
import com.github.miwu.util.TextUtils.log
import kotlin.concurrent.thread

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        startActivity()
    }

    private fun startActivity() {
        runOnUiThread {
            if (loginMsg.userId.isEmpty()) {
                startLoginActivity()
            } else {
                startMainActivity()
            }
        }
    }

    private fun startMainActivity() {
        try {
            thread {
                val start = System.currentTimeMillis()
                HomeDAO.init() // 加载米家信息
                "加载完成 ${(System.currentTimeMillis() - start) / 1000}".log()
                startActivity<MainActivity>()
                finish() //关闭当前活动
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startLoginActivity() {
        try {
            Thread.sleep(100)
            startActivity<LoginActivity>()
            finish() //关闭当前活动
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}