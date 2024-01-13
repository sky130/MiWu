package com.github.miwu.widget.adapter

import android.util.ArrayMap
import android.widget.ImageView
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

fun loadImageUrl(imageView: ImageView, url: String) {
    Glide.with(imageView.context)
        .load(url)
        .placeholder(R.drawable.mi_icon_small)
        .error(R.drawable.mi_icon_small)
        .into(imageView)
}