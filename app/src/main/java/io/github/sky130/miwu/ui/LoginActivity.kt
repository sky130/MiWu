package io.github.sky130.miwu.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.sky130.miwu.MainApplication
import io.github.sky130.miwu.R
import io.github.sky130.miwu.databinding.ActivityLoginBinding
import io.github.sky130.miwu.logic.dao.UserDAO
import io.github.sky130.miwu.logic.network.miot.UserService
import io.github.sky130.miwu.startActivity
import io.github.sky130.miwu.util.TextUtils.toast
import io.github.sky130.miwu.util.ViewUtils.addTouchScale
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
                MainApplication.loginMsg = loginMsg
                UserDAO.saveUser(loginMsg)
                runOnUiThread {
                    R.string.toast_login_success.toast()
                    startActivity<MainActivity>()
                    this.finish()
                }
            }
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