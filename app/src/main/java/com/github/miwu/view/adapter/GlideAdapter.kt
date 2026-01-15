package com.github.miwu.view.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.github.miwu.R
import com.github.miwu.logic.handler.DeviceMetadataHandler
import com.github.miwu.logic.repository.DeviceRepository
import kndroidx.extension.dp
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotScenes


@BindingAdapter(value = ["deviceModel", "deviceMetadataHandler"])
fun ImageView.miotIcon(model: String, handler: DeviceMetadataHandler) {
    loadImage(handler.getIcon(model))
}

@BindingAdapter(value = ["url"])
fun ImageView.loadUrl(url: String?) {
    loadImage(url)
}

@BindingAdapter(value = ["drawable"])
fun ImageView.loadDrawable(drawable: Drawable?) {
    Glide.with(this)
        .load(drawable)
        .error(R.drawable.ic_miwu_placeholder)
        .into(this)
}

var options = RequestOptions.bitmapTransform(RoundedCorners(15.dp))

@BindingAdapter(value = ["bitmap"])
fun ImageView.loadUrl(bitmap: Bitmap?) {
    Glide.with(this)
        .load(bitmap)
        .apply(options)
        .error(R.drawable.ic_miwu_placeholder)
        .into(this)
}

@BindingAdapter(value = ["scene"])
fun ImageView.loadUrl(scene: MiotScenes.Result.Scene) {
    Glide.with(this)
        .load(scene.iconUrl)
        .placeholder(R.drawable.ic_miwu_placeholder)
        .error(R.drawable.ic_miot_scene)
        .into(this)
}

fun ImageView.loadImage(url: String?) =
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.ic_miwu_placeholder)
        .error(R.drawable.ic_miwu_placeholder)
        .into(this)

fun ImageView.loadImage(@DrawableRes res: Int?) =
    Glide.with(this)
        .load(res)
        .placeholder(R.drawable.ic_miwu_placeholder)
        .error(R.drawable.ic_miwu_placeholder)
        .into(this)