package io.github.sky130.miwu.ui.framgent

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.sky130.miwu.MainApplication.Companion.loginMsg
import io.github.sky130.miwu.R
import io.github.sky130.miwu.logic.network.MiotService
import io.github.sky130.miwu.logic.network.miot.UserService
import io.github.sky130.miwu.util.GlideUtils
import java.util.concurrent.Executors

class SettingsFragment : BaseFragment() {
    private val executor = Executors.newSingleThreadExecutor()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            executor.execute {
                val UserInfo = UserService.getUserInfo(loginMsg.userId)
                // 更新UI需要切换到主线程
                activity?.runOnUiThread {
                    if (UserInfo != null) {
                        Log.d("uid", UserInfo.uid)
                        Log.d("avatar", UserInfo.avatar)
                        Log.d("nickname", UserInfo.nickname)
                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_settings, container, false)
    }

}