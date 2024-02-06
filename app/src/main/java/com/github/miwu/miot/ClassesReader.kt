package com.github.miwu.miot

import android.content.Context
import dalvik.system.DexFile
import java.io.File
import java.io.IOException

object ClassesReader {

    fun applicationDexFile(packageCodePath: String): Set<DexFile> {
        val dexFiles: MutableSet<DexFile> = HashSet()
        val dir = File(packageCodePath).parentFile!!
        val files = dir.listFiles()!!
        for (file in files) {
            try {
                val absolutePath = file.absolutePath
                if (!absolutePath.contains(".")) continue
                val suffix = absolutePath.substring(absolutePath.lastIndexOf("."))
                if (suffix != ".apk") continue
                val dexFile = createDexFile(file.absolutePath) ?: continue
                dexFiles.add(dexFile)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return dexFiles
    }

    fun createDexFile(path: String?): DexFile? {
        return try {
            DexFile(path)
        } catch (e: IOException) {
            null
        }
    }

    fun reader(packageName: String, context: Context): List<Class<*>> {
        return reader(packageName, context.packageCodePath)
    }

    fun reader(packageName: String, packageCodePath: String): List<Class<*>> {
        val classes: MutableList<Class<*>> = ArrayList()
        val dexFiles = applicationDexFile(packageCodePath)
        val classLoader = Thread.currentThread().contextClassLoader
        for (dexFile in dexFiles) {
            val entries = dexFile.entries()
            while (entries.hasMoreElements()) {
                try {
                    val currentClassPath = entries.nextElement()
                    if (currentClassPath == null || currentClassPath.isEmpty() || currentClassPath.indexOf(
                            packageName
                        ) != 0
                    ) continue
                    val entryClass =
                        Class.forName(currentClassPath, true, classLoader) ?: continue
                    classes.add(entryClass)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return classes
    }
}



