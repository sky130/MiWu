package io.github.sky130.miwu.ui

import android.annotation.SuppressLint
import android.os.Bundle
import io.github.sky130.miwu.MainApplication.Companion.loginMsg
import io.github.sky130.miwu.base.BaseActivity
import io.github.sky130.miwu.logic.dao.HomeDAO
import io.github.sky130.miwu.startActivity
import io.github.sky130.miwu.util.TextUtils.log
import kotlin.concurrent.thread

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thread {
            val start = System.currentTimeMillis()
            HomeDAO.init() // 加载米家信息
            "加载完成 ${(System.currentTimeMillis() - start) / 1000}".log()
            startActivity()
        }
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
            startActivity<MainActivity>()
            finish() //关闭当前活动
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