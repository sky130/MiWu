@file:Suppress("DEPRECATION")

package io.github.sky130.miwu

import android.app.Activity
import android.content.Intent

/**
 * 一个Activity辅助启动工具，让Activity启动更加便携
 */
inline fun <reified T> Activity.startActivity(block: Intent.() -> Unit) {
    val intent = Intent(this, T::class.java)
    intent.block()
    startActivity(intent)
}

inline fun <reified T> Activity.startActivity() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}


