package com.github.miwu.platform.crash

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import com.github.miwu.domain.repository.CrashLogRepository
import com.github.miwu.domain.repository.SettingsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.lang.reflect.InvocationTargetException
import org.koin.core.annotation.Named
import org.koin.core.annotation.Provided

class CrashHandler(
    @Provided context: Context,
    private val settingsRepository: SettingsRepository,
    @Named("app_io_dispatcher") private val ioDispatcher: CoroutineDispatcher,
) : Thread.UncaughtExceptionHandler, CrashLogRepository {
    private val appContext = context.applicationContext
    private var defaultHandler: Thread.UncaughtExceptionHandler? = null

    override val path: String by lazy {
        appContext.getExternalFilesDir("")!!.resolve("Crash").path + File.separator
    }

    fun install() {
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override suspend fun readLatest(): String = withContext(ioDispatcher) {
        try {
            File(path)
                .listFiles()
                ?.maxByOrNull(File::lastModified)
                ?.readText()
                .orEmpty()
        } catch (error: CancellationException) {
            throw error
        } catch (_: Throwable) {
            ""
        }
    }

    override fun uncaughtException(thread: Thread, error: Throwable) {
        try {
            settingsRepository.hasPendingCrash = true
            dumpException(error)
        } catch (_: IOException) {
            defaultHandler?.uncaughtException(thread, error)
            return
        }
        Process.killProcess(Process.myPid())
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun dumpException(error: Throwable) {
        val directory = File(path).apply { mkdirs() }
        val file = directory.resolve("${System.currentTimeMillis()}.log")
        PrintWriter(BufferedWriter(FileWriter(file))).use { writer ->
            writer.println(error::class.java.name)
            writer.println("----------------")
            dumpPhoneInfo(writer)
            writer.println("----------------")
            error.printStackTrace(writer)
            if (error is InvocationTargetException) {
                writer.println("----------------")
                error.cause?.printStackTrace(writer)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun dumpPhoneInfo(writer: PrintWriter) {
        val packageInfo = appContext.packageManager.getPackageInfo(
            appContext.packageName,
            PackageManager.GET_ACTIVITIES,
        )
        writer.apply {
            println("App Version: ${packageInfo.versionName}_${packageInfo.versionCode}")
            println("OS Version: ${Build.VERSION.RELEASE}_${Build.VERSION.SDK_INT}")
            println("Vendor: ${Build.MANUFACTURER}")
            println("Model: ${Build.MODEL}")
            println("CPU ABI: ${Build.SUPPORTED_ABIS.contentToString()}")
        }
    }
}
