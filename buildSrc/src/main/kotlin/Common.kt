package miwu

import java.io.BufferedReader
import kotlin.math.pow

val latestGitTag = ProcessBuilder("git", "describe", "--tags", "--abbrev=0")
    .start()
    .inputStream
    .bufferedReader()
    .use(BufferedReader::readText)
    .replace("v", "")
    .trim()
    .ifEmpty { "0.0.1" }

fun getVersionInt(ver: String): Int = ver
    .split("-")
    .first()
    .split(".")
    .map(String::toInt)
    .reversed()
    .withIndex()
    .map { (i, v) -> v * 100f.pow(i + 1) }
    .sumOf(Float::toInt)