package com.github.miwu.util

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.github.miwu.MainApplication.Companion.context


/**
 * Glide工具类
 * @author llw
 * @from https://juejin.cn/post/7228050022250774587
 */
object GlideUtils {

    /**
     * 显示网络Url图片
     * @param url
     * @param imageView
     */
    fun loadImg(url: String?, imageView: ImageView?) {
        Glide.with(context).load(url).into(imageView!!)
    }

    /**
     * 显示资源图片
     * @param recourseId 资源图片
     * @param imageView
     */
    fun loadImg(recourseId: Int?, imageView: ImageView?) {
        Glide.with(context).load(recourseId).into(imageView!!)
    }

    /**
     * 显示bitmap图片
     * @param bitmap
     * @param imageView
     */
    fun loadImg(bitmap: Bitmap?, imageView: ImageView?) {
        Glide.with(context).load(bitmap).into(imageView!!)
    }

    /**
     * 显示drawable图片
     * @param drawable
     * @param imageView
     */
    fun loadImg(drawable: Drawable?, imageView: ImageView?) {
        Glide.with(context).load(drawable).into(imageView!!)
    }
}
