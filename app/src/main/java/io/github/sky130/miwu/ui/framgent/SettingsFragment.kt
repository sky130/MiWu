package io.github.sky130.miwu.ui.framgent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.sky130.miwu.MainApplication.Companion.loginMsg
import io.github.sky130.miwu.R
import io.github.sky130.miwu.databinding.FragmentMainSettingsBinding
import io.github.sky130.miwu.logic.network.miot.UserService
import io.github.sky130.miwu.util.GlideUtils
import java.util.concurrent.Executors

class SettingsFragment : BaseFragment() {
    private lateinit var binding: FragmentMainSettingsBinding
    private val executor = Executors.newSingleThreadExecutor()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainSettingsBinding.inflate(layoutInflater)
        val avatar = binding.avatarImage
        val uid = binding.uidText
        val name = binding.nicknameText
        executor.execute {
            val UserInfo = UserService.getUserInfo(loginMsg.userId)
            // 更新UI需要切换到主线程
            activity?.runOnUiThread {
                if (UserInfo != null) {
                    if (UserInfo.avatar.isNotEmpty()){
                        GlideUtils.loadImg(UserInfo.avatar, avatar)
                    } else {
                        avatar.setImageResource(R.drawable.mi_icon_small)
                    }
                    uid.text = UserInfo.uid
                    name.text = UserInfo.nickname
                }
            }
        }
        return binding.root
    }

}