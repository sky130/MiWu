package com.github.miwu.di

import android.content.Context
import androidx.room.Room
import com.github.miwu.data.account.local.MiotUserDataStore
import com.github.miwu.data.account.local.miotDataStore
import com.github.miwu.data.local.database.AppDatabase
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.engine.okhttp.OkHttpEngine
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

private const val APP_DATABASE_NAME = "app_database_v3"

@Module
class DataModule {
    @Singleton
    fun httpClientEngine(): HttpClientEngine = OkHttpEngine(OkHttpConfig())

    @Singleton
    fun httpClient(): HttpClient = HttpClient()

    @Singleton
    fun database(context: Context): AppDatabase =
        Room.databaseBuilder<AppDatabase>(context, APP_DATABASE_NAME)
            .addMigrations(AppDatabase.MIGRATION_2_3)
            .build()

    @Singleton
    fun favoriteDeviceDao(database: AppDatabase) = database.favoriteDeviceDao()

    @Singleton
    fun crashDao(database: AppDatabase) = database.crashDao()

    @Singleton
    fun datastore(context: Context): MiotUserDataStore = miotDataStore(context)
}
