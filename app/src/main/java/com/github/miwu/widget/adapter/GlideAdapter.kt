package com.github.miwu.widget.adapter

import android.util.ArrayMap
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.github.miwu.R
import com.github.miwu.logic.database.model.MiwuDevice
import com.github.miwu.ui.main.fragment.DeviceFragment
import com.github.miwu.ui.main.fragment.MiWuFragment
import kndroidx.activity.ViewActivityX
import kndroidx.fragment.ViewFragmentX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miot.kotlin.MiotManager
import miot.kotlin.helper.getIconUrl
import miot.kotlin.model.miot.MiotDevices
import miot.kotlin.model.miot.MiotScenes
import org.json.JSONObject

@get:Synchronized
val iconMap = ArrayMap<String, String>()

@BindingAdapter(value = ["device", "fragment"])
fun miotIcon(imageView: ImageView, device: MiotDevices.Result.Device, fragment: DeviceFragment) {
    Glide.with(imageView.context).load(R.drawable.ic_miwu_placeholder).into(imageView)
    val url = iconMap[device.model]
    if (url == null) {
        fragment.viewModel.viewModelScope.launch(Dispatchers.IO) {
            device.getIconUrl().apply {
                withContext(Dispatchers.Main) {
                    if (this@apply == null) {
                        iconMap[device.model] = ""
                    } else {
                        iconMap[device.model] = this@apply
                        loadImageUrl(imageView, this@apply)
                    }
                }
            }
        }
    } else if (url.isNotEmpty()) {
        loadImageUrl(imageView, url)
    }
}

@BindingAdapter(value = ["deviceItem"])
fun miotIcon(imageView: ImageView, device: MiotDevices.Result.Device) {
    Glide.with(imageView.context).load(R.drawable.ic_miwu_placeholder).into(imageView)
    val url = iconMap[device.model]
    if (url == null) {
        (imageView.context as ViewActivityX<*, *>).viewModel.viewModelScope.launch(Dispatchers.IO) {
            device.getIconUrl().apply {
                withContext(Dispatchers.Main) {
                    if (this@apply == null) {
                        iconMap[device.model] = ""
                    } else {
                        iconMap[device.model] = this@apply
                        loadImageUrl(imageView, this@apply)
                    }
                }
            }
        }
    } else if (url.isNotEmpty()) {
        loadImageUrl(imageView, url)
    }
}

fun ImageView.loadMiotIcon(device: MiwuDevice, viewModel: ViewModel) {
    Glide.with(context).load(R.drawable.ic_miwu_placeholder).into(this)
    val url = iconMap[device.model]
    if (url == null) {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            device.getIconUrl().apply {
                withContext(Dispatchers.Main) {
                    if (this@apply == null) {
                        iconMap[device.model] = ""
                    } else {
                        iconMap[device.model] = this@apply
                        loadImageUrl(this@loadMiotIcon, this@apply)
                    }
                }
            }
        }
    } else if (url.isNotEmpty()) {
        loadImageUrl(this, url)
    }
}

@BindingAdapter(value = ["url"])
fun loadImg(imageView: ImageView, url: String?) {
    if (url != null) loadImageUrl(imageView, url)
}

@BindingAdapter(value = ["scene"])
fun loadImg(imageView: ImageView, scene: MiotScenes.Result.Scene) {
    if (scene.icon.isNotEmpty()) loadImageUrl(imageView, scene.icon)
    else {
        loadImageRes(imageView, R.drawable.ic_miot_scene)
    }
}

fun loadImageUrl(imageView: ImageView, url: String) {
    Glide.with(imageView.context).load(url).placeholder(R.drawable.ic_miwu_placeholder)
        .error(R.drawable.ic_miwu_placeholder).into(imageView)
}

fun loadImageRes(imageView: ImageView, @DrawableRes res: Int) {
    Glide.with(imageView.context).load(res).placeholder(R.drawable.ic_miwu_placeholder)
        .error(R.drawable.ic_miwu_placeholder).into(imageView)
}

suspend fun MiwuDevice.getIconUrl(): String? = withContext(Dispatchers.IO) {
    try {
        val url =
            "https://home.mi.com/cgi-op/api/v1/baike/v2/product?model=${this@getIconUrl.model}"
        val json = MiotManager.get(url)
        val jsonObject = JSONObject(json)
        if (jsonObject.getInt("code") != 0) return@withContext null
        return@withContext jsonObject.getJSONObject("data").getString("realIcon")
    } catch (_: Exception) {
        return@withContext null
    }
}