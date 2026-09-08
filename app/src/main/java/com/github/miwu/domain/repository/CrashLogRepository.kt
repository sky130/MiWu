package com.github.miwu.domain.repository

interface CrashLogRepository {
    val path: String

    suspend fun readLatest(): String
}
