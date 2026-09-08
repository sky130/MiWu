package com.github.miwu.di

import android.content.Context
import androidx.room.Room
import com.github.miwu.data.account.local.datastore
import com.github.miwu.data.local.database.AppDatabase
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.engine.okhttp.OkHttpEngine
import org.koin.dsl.module
import org.koin.plugin.module.dsl.create


val dataModule = module {
    single { create(::httpClientEngine) }
    single { create(::datastore) }
    single { create(::httpClient) }
    single { create(::database) }
    single { create(::favoriteDeviceDao) }
    single { create(::crashDao) }
}

private const val APP_DATABASE_NAME = "app_database_v3"

private fun httpClientEngine(): HttpClientEngine = OkHttpEngine(OkHttpConfig())

private fun httpClient(): HttpClient = HttpClient()

private fun database(context: Context) =
    Room.databaseBuilder<AppDatabase>(context, APP_DATABASE_NAME)
        .addMigrations(AppDatabase.MIGRATION_2_3)
        .build()

private fun favoriteDeviceDao(database: AppDatabase) = database.favoriteDeviceDao()

private fun crashDao(database: AppDatabase) = database.crashDao()