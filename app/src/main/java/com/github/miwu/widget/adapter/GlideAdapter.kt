package com.github.miwu.widget.adapter

import android.util.ArrayMap
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.github.miwu.R
import com.github.miwu.ui.main.fragment.MainFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import miot.kotlin.helper.getIconUrl
import miot.kotlin.model.miot.MiotDevices
import miot.kotlin.model.miot.MiotScenes

val map = ArrayMap<String, String>()

@BindingAdapter(value = ["device", "fragment"])
fun miotIcon(imageView: ImageView, device: MiotDevices.Result.Device, fragment: MainFragment) {
    Glide.with(imageView.context)
        .load(R.drawable.mi_icon_small)
        .into(imageView)
    val url = map[device.model]
    if (url == null) {
        fragment.viewModel.viewModelScope.launch(Dispatchers.IO) {
            device.getIconUrl()?.also {
                map[device.model] = it
                withContext(Dispatchers.Main) {
                    loadImageUrl(imageView, it)
                }
            }.let {
                if (it == null) {
                    map[device.model] = ""
                }
            }
        }
    } else {
        if (url.isNotEmpty())
            loadImageUrl(imageView, url)
    }
}

@BindingAdapter(value = ["url"])
fun loadImg(imageView: ImageView, url: String?) {
    if (url != null)
        loadImageUrl(imageView, url)
}

@BindingAdapter(value = ["scene"])
fun loadImg(imageView: ImageView, scene: MiotScenes.Result.Scene) {
    if (scene.icon.isNotEmpty())
        loadImageUrl(imageView, scene.icon)
    else{
        loadImageRes(imageView,R.drawable.ic_mi_scene)
    }
}

fun loadImageUrl(imageView: ImageView, url: String) {
    Glide.with(imageView.context)
        .load(url)
        .placeholder(R.drawable.mi_icon_small)
        .error(R.drawable.mi_icon_small)
        .into(imageView)
}

fun loadImageRes(imageView: ImageView, @DrawableRes res: Int) {
    Glide.with(imageView.context)
        .load(res)
        .placeholder(R.drawable.mi_icon_small)
        .error(R.drawable.mi_icon_small)
        .into(imageView)
}