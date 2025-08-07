package com.github.miwu.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.ArrayMap
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.github.miwu.R
import com.github.miwu.logic.database.model.MiwuDatabaseDevice
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.ui.main.fragment.DeviceFragment
import kndroidx.activity.ViewActivityX
import kndroidx.extension.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miwu.miot.MiotClient
import miwu.miot.helper.MiotIconHelper
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotDevices
import miwu.miot.model.miot.MiotScenes
import kotlin.apply


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
        (imageView.context as ViewActivityX<*>).viewModel.viewModelScope.launch(Dispatchers.IO) {
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

fun ImageView.loadMiotIcon(device: MiwuDatabaseDevice, viewModel: ViewModel, miotClient: MiotClient) {
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

@BindingAdapter(value = ["drawable"])
fun loadImg(imageView: ImageView, drawable: Drawable?) {
    Glide.with(imageView).load(drawable).error(R.drawable.ic_miwu_placeholder).into(imageView)
}

var options = RequestOptions.bitmapTransform(RoundedCorners(15.dp))

@BindingAdapter(value = ["bitmap"])
fun loadImg(imageView: ImageView, bitmap: Bitmap?) {
    Glide.with(imageView).load(bitmap).apply(options).error(R.drawable.ic_miwu_placeholder)
        .into(imageView)
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

suspend fun MiwuDatabaseDevice.getIconUrl(): String? = withContext(Dispatchers.IO) {
    return@withContext MiotIconHelper.getIconUrl(this@getIconUrl.model)
}


suspend fun MiotDevice.getIconUrl(): String? = withContext(Dispatchers.IO) {
    return@withContext MiotIconHelper.getIconUrl(this@getIconUrl.model)
}