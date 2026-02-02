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
    .ifEmpty { "3.0.0" }

fun getVersionInt(ver: String): Int = ver
    .split("-")
    .first()
    .split(".")
    .map(String::toInt)
    .reversed()
    .withIndex()
    .map { (i, v) -> v * 100f.pow(i + 1) }
    .sumOf(Float::toInt)