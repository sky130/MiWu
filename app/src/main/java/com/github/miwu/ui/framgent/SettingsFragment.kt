package com.github.miwu.ui.framgent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.miwu.R
import com.github.miwu.databinding.FragmentMainSettingsBinding
import com.github.miwu.logic.dao.UserDAO
import com.github.miwu.startActivity
import com.github.miwu.ui.LoginActivity
import com.github.miwu.ui.SwitchHomeActivity
import com.github.miwu.util.GlideUtils
import com.github.miwu.util.TextUtils.toast
import com.github.miwu.util.ViewUtils.addTouchScale
import java.util.concurrent.Executors

class SettingsFragment : BaseFragment() {
    private lateinit var binding: FragmentMainSettingsBinding
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainSettingsBinding.inflate(layoutInflater)
        val avatar = binding.avatarImage
        val uid = binding.uidText
        val name = binding.nicknameText
        executor.execute {
            val info = UserDAO.getLocalUserInfo()
            val userInfo = if (info.uid.isEmpty()) UserDAO.getLatestUserInfo() else info
            // 更新UI需要切换到主线程
            runOnUiThread {
                if (userInfo.avatar.isNotEmpty()) {
                    GlideUtils.loadImg(userInfo.avatar, avatar)
                } else {
                    avatar.setImageResource(R.drawable.mi_icon_small)
                }
                uid.text = userInfo.uid
                name.text = userInfo.nickname
            }
        }
        binding.switchHome.addTouchScale()
        binding.switchHome.setOnClickListener {
            requireActivity().startActivity<SwitchHomeActivity>()
        }
        binding.exitAccount.addTouchScale()
        binding.exitAccount.setOnClickListener {
            UserDAO.logout()
            "登出成功".toast()
            requireActivity().startActivity<LoginActivity>()
            requireActivity().finish()
        }
        return binding.root
    }

}