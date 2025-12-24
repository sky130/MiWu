package com.github.miwu.ktx

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T> Logger() = LoggerFactory.getLogger(T::class.java)!!

fun Any.Logger(onCreate: (Logger.() -> Unit) = Logger::onCreated) =
    LoggerFactory.getLogger(this::class.java)!!.apply(onCreate)

@Suppress("FunctionName")
fun Any.LazyLogger(onCreate: (Logger.() -> Unit) = Logger::onCreated) = lazy { Logger(onCreate) }

fun Logger.onCreated() = info("{} initialized", this.name)