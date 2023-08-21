package io.github.sky130.miwu.ui.framgent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.sky130.miwu.R
import io.github.sky130.miwu.databinding.FragmentMainSettingsBinding
import io.github.sky130.miwu.logic.dao.UserDAO
import io.github.sky130.miwu.startActivity
import io.github.sky130.miwu.ui.LoginActivity
import io.github.sky130.miwu.ui.SwitchHomeActivity
import io.github.sky130.miwu.util.GlideUtils
import io.github.sky130.miwu.util.TextUtils.toast
import io.github.sky130.miwu.util.ViewUtils.addTouchScale
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