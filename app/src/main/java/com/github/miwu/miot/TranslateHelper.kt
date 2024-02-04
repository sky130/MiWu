package com.github.miwu.miot

import com.github.miwu.R
import kndroidx.extension.string
import miot.kotlin.model.att.SpecAtt

fun SpecAtt.Service.Property.translate() {
    valueList?.let {
        for (i in it) {
            i.description = i.description.translate()
        }
    }
    description = description.translate()
}

fun SpecAtt.Service.Action.translate() {
    description = description.translate()
}

private fun String.translate(): String = when (this) {
    "Auto" -> R.string.Auto.string
    "Cool" -> R.string.Cool.string
    "Dry" -> R.string.Dry.string
    "Heat" -> R.string.Heat.string
    "Fan" -> R.string.Fan.string
    "Normal" -> R.string.Normal.string
    "Low" -> R.string.LowFood.string
    "Empty" -> R.string.EmptyFood.string
    else -> this
}