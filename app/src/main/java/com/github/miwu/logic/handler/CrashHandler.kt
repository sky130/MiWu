package com.github.miwu.logic.handler

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import com.github.miwu.logic.setting.AppSetting
import kndroidx.KndroidX.context
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.lang.reflect.InvocationTargetException

class CrashHandler private constructor() : Thread.UncaughtExceptionHandler {
    private var mDefaultCrashHandler: Thread.UncaughtExceptionHandler? = null
    private lateinit var mContext: Context

    fun init(context: Context) {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
        mContext = context.applicationContext
    }

    override fun uncaughtException(thread: Thread, ex: Throwable) {
        try {
            AppSetting.isCrash.value = true
            Thread.sleep(1000L)
            dumpExceptionToSDCard(ex)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Process.killProcess(Process.myPid())
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun dumpExceptionToSDCard(e: Throwable) {
        val dir = File(PATH)
        if (!dir.exists()) dir.mkdirs()
        val current = System.currentTimeMillis()
        val file = File("${PATH}${current}.log")
        try {
            e.printStackTrace()
            PrintWriter(BufferedWriter(FileWriter(file))).use { pw ->
                pw.println(e::class.java.name)
                pw.println("----------------")
                dumpPhoneInfo(pw)
                pw.println("----------------")
                e.printStackTrace(pw)
                if (e is InvocationTargetException){
                    pw.println("----------------")
                    e.cause?.printStackTrace(pw)
                }
            }
        } catch (_: Exception) {
        }
    }

    private fun dumpPhoneInfo(pw: PrintWriter) {
        val pm = mContext.packageManager
        val pi = pm.getPackageInfo(mContext.packageName, PackageManager.GET_ACTIVITIES)
        pw.apply {
            print("App Version: ")
            print(pi.versionName)
            print("_")
            println(pi.versionCode)
            print("OS Version: ")
            print(Build.VERSION.RELEASE)
            print("_")
            println(Build.VERSION.SDK_INT)
            print("Vendor: ")
            println(Build.MANUFACTURER)
            print("Model: ")
            println(Build.MODEL)
            print("CPU ABI: ")
            println(Build.SUPPORTED_ABIS.contentToString())
        }
    }

    data class LogFile(val title: String)

    companion object {
        val PATH by lazy { context.getExternalFilesDir("")!!.path + "/Crash/" }
        @SuppressLint("StaticFieldLeak")
        val instance = CrashHandler()
    }
}
