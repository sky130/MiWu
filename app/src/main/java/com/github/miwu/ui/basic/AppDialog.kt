package com.github.miwu.ui.basic

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.annotation.StyleRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.github.miwu.R
import java.lang.reflect.ParameterizedType
import androidx.core.graphics.drawable.toDrawable

abstract class AppDialog<VB : ViewBinding, VM : ViewModel> : DialogFragment() {
    val viewModel: VM by lazy(mode = LazyThreadSafetyMode.NONE) {
        createViewModel<VM>().apply {
            initViewModel(this)
        }
    }
    private var _binding: VB? = null
    val binding get() = _binding!!
    private var block = {}

    @StyleRes
    open val themeRes = R.style.Theme_MiHome

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), themeRes)
        dialog.window?.apply {
            setBackgroundDrawable(Color.BLACK.toDrawable())
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        val fadeAnimation = AlphaAnimation(0.0f, 1.0f)
        fadeAnimation.duration = 300
        view.startAnimation(fadeAnimation)
        init()
    }

    open fun init() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = getViewDataBinding(layoutInflater)
        viewModel
        return binding.root
    }

    fun exit() {
        try {
            val fadeAnimation = AlphaAnimation(1.0f, 0.0f)
            fadeAnimation.duration = 300
            view?.startAnimation(fadeAnimation)
            Handler(Looper.getMainLooper()).postDelayed(
                { dismiss(); block() },
                fadeAnimation.duration
            )
        } catch (_: Exception) {

        }
    }

    fun exit(block: () -> Unit) {
        try {
            val fadeAnimation = AlphaAnimation(1.0f, 0.0f)
            fadeAnimation.duration = 300
            view?.startAnimation(fadeAnimation)
            Handler(Looper.getMainLooper()).postDelayed(
                { dismiss(); block() },
                fadeAnimation.duration
            )
        } catch (_: Exception) {

        }
    }

    fun setOnDialogDismiss(block: () -> Unit) {
        this.block = block
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initViewModel(viewModel: ViewModel) {
        if (binding !is ViewDataBinding) return
        try {
            val vbClass =
                (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
            val vmClass =
                (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VM>>()
            val set = vbClass[0].getMethod("setViewModel", vmClass[1])
            set.invoke(binding, viewModel)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        (binding as ViewDataBinding).lifecycleOwner = viewLifecycleOwner
        try {
            val vbClass =
                (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
            val set = vbClass[0].getMethod("setDialog", this::class.java)
            set.invoke(binding, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <VB : ViewBinding> getViewDataBinding(inflater: LayoutInflater): VB {
        val vbClass =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
        val inflate = vbClass[0].getDeclaredMethod("inflate", LayoutInflater::class.java)
        return inflate.invoke(null, inflater) as VB
    }

    @Suppress("UNCHECKED_CAST")
    private fun <VM : ViewModel> createViewModel(): VM {
        val vmClass =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<*>>()
        val viewModel = vmClass[1] as Class<VM>
        return ViewModelProvider(requireActivity())[viewModel]
    }

    fun show(supportFragmentManager: FragmentManager) = show(
        supportFragmentManager, this.javaClass.name
    )
}
