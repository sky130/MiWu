package com.github.miwu.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.miwu.MainApplication
import com.github.miwu.R
import com.github.miwu.databinding.ActivityLoginBinding
import com.github.miwu.logic.dao.UserDAO
import com.github.miwu.logic.network.miot.UserService
import com.github.miwu.startActivity
import com.github.miwu.util.TextUtils.toast
import com.github.miwu.util.ViewUtils.addTouchScale
import kotlin.concurrent.thread


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.btnLogin.addTouchScale()
        binding.btnLogin.setOnClickListener {
            it.isEnabled = false
            val user = binding.editUser.text.toString()
            val pwd = binding.editPwd.text.toString()
            if (user.isEmpty() || pwd.isEmpty()) {
                R.string.toast_user_pwd_not_null.toast()
                it.isEnabled = true
                return@setOnClickListener
            }
            thread {
                val loginMsg = UserService.login(user, pwd)
                if (loginMsg == null) {
                    loginFailure()
                    return@thread
                }
                if (loginMsg.isError) {
                    loginFailure()
                    return@thread
                }
                runOnUiThread {
                    R.string.toast_logging_in.toast()
                }
                MainApplication.loginMsg = loginMsg
                UserDAO.saveUser(loginMsg)
                runOnUiThread {
                    R.string.toast_login_success.toast()
                    startActivity<SplashActivity>()
                    this.finish()
                }
            }
        }
        binding.gotoLicense.addTouchScale()
        binding.gotoLicense.setOnClickListener {
            startActivity<LicenseActivity>()
        }
        setContentView(binding.root)
    }

    private fun loginFailure() {
        runOnUiThread {
            binding.btnLogin.isEnabled = true
            R.string.toast_login_failure.toast()
        }
    }
}