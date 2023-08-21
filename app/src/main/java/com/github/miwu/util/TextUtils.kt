package com.github.miwu.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.github.miwu.MainApplication.Companion.context
import java.util.Locale
import java.util.regex.Pattern

object TextUtils {
    val charList = listOf(
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z"
    )

    fun String.isUrl(): Boolean {
        val pattern = "^((https?|ftp|file)://)?([a-z0-9-]+\\.)+[a-z0-9]{2,4}.*$"
        val regex = Regex(pattern, RegexOption.IGNORE_CASE)
        return regex.matches(this)
    }

    fun getRandomChar(): String {
        return charList[kotlin.random.Random.nextInt(0, charList.size)]
    }

    fun analyzeText(s: String, separator: String): Array<String?>? {
        var str = s
        return if ("" != separator && "" != str) {
            if (separator == "\n") {
                str = exchangeText(str, "\r", "")
            }
            if (str.getTextRight(
                    separator.length
                ) == separator
            ) getTheText(
                separator + str, separator, separator
            ) else getTheText(separator + str + separator, separator, separator)
        } else {
            arrayOfNulls(0)
        }
    }

    fun String.log(tag: String = "mi_home") {
        Log.d(tag,this)
    }

    fun join(separator: String?, vararg elements: String?): StringBuilder? {
        val builder = StringBuilder()
        var hasAdded = false
        for (element in elements) {
            if (!element.isNullOrEmpty()) {
                if (hasAdded) {
                    builder.append(separator)
                }
                builder.append(element)
                hasAdded = true
            }
        }
        return builder
    }

    fun listToString(arrayList: ArrayList<String?>?): String? {
        if (arrayList == null) return ""
        if (arrayList.size == 0) return ""
        val sb = StringBuilder()
        for (item in arrayList) sb.append(item).append("\n")
        return sb.toString()
    }

    fun String.copyText(): Boolean {
        return try {
            val clipboard: ClipboardManager =
                (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?)!!
            val clipData = ClipData.newPlainText(null, this)
            clipboard.setPrimaryClip(clipData)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun String.checkUserName(): String {
        if (isEmpty()) return ""
        if (isValidEmail()) this
        else if (length == 11) return "+86$this"
        else if (length == 14) return this
        else if (this[0].toString() === "+") return this
        return ""
    }

    fun String.isValidEmail(): Boolean {
        return if (!isEmpty()) Pattern.matches(
            "^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$",
            this
        ) else false
    }

    fun String.isValidMac(): Boolean {
        val patternMac = "^[A-F0-9]{2}(:[A-F0-9]{2}){5}$"
        return Pattern.compile(patternMac).matcher(this).find()
    }

    fun String.fixMac(): String {
        return this.replace(" ".toRegex(), "").replace("：".toRegex(), ":")
            .uppercase(Locale.getDefault())
    }

    fun regexMatch(text: String, statement: String): Array<String?>? {
        val pn = Pattern.compile(statement, 40)
        val mr = pn.matcher(text)
        val list = ArrayList<String>()
        while (mr.find()) {
            list.add(mr.group())
        }
        val strings = arrayOfNulls<String>(list.size)
        return list.toArray(strings)
    }


    fun getTheText(str: String, left: String, right: String): Array<String?>? {
        return if ("" != str && "" != left && "" != right) regexMatch(
            str, "(?<=\\Q$left\\E).*?(?=\\Q$right\\E)"
        ) else arrayOfNulls(0)
    }

    fun String.isNullStr(): Boolean {
        if (replace(" ", "") == "") {
            return true
        }
        return false
    }

    fun String.lookFor(str2: String): Boolean {
        return (if ("" != this && "" != str2) this.indexOf(str2, 0) else -1) != -1
    }

    @Suppress("DEPRECATION")
    fun String.toast(int: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, this, int).apply {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                val toastView = this.view // 获取 Toast 的视图
                val textView = toastView?.findViewById<TextView>(android.R.id.message)
                textView?.setTextColor(Color.WHITE)
            }
            show()
        }
    }

    @Suppress("DEPRECATION")
    fun Int.toast(int: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, this, int).apply {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                val toastView = this.view // 获取 Toast 的视图
                val textView = toastView?.findViewById<TextView>(android.R.id.message)
                textView?.setTextColor(Color.WHITE)
            }
            show()
        }
    }
    fun String.getTextRight(len: Int): String {
        return if ("" != this && len > 0) {
            if (len > this.length) {
                this
            } else {
                val start = this.length - len
                this.substring(start)
            }
        } else {
            ""
        }
    }


    fun exchangeText(str: String, x: String, replace: String?): String {
        var find = x
        return if ("" != find && "" != str) {
            find = "\\Q$find\\E"
            str.replace(find.toRegex(), replace!!)
        } else {
            ""
        }
    }

}