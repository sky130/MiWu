package io.github.sky130.miwu.base

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    private inline fun <reified T> startActivity(block: Intent.() -> Unit) {
        val intent = Intent(this, T::class.java)
        intent.block()
        startActivity(intent)
    }

    private inline fun <reified T> startActivity() {
        val intent = Intent(this, T::class.java)
        startActivity(intent)
    }

}