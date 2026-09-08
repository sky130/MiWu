package com.github.miwu.di

import android.content.Context
import androidx.room.Room
import com.github.miwu.data.account.AccountRepositoryImpl
import com.github.miwu.data.account.local.MiotUserDataStore
import com.github.miwu.data.account.local.miotUserStore
import com.github.miwu.data.favorite.FavoriteDeviceRepositoryImpl
import com.github.miwu.data.home.HomeDataLoader
import com.github.miwu.data.home.HomeRepositoryImpl
import com.github.miwu.data.local.database.AppDatabase
import com.github.miwu.data.metadata.DeviceIconRepositoryImpl
import com.github.miwu.data.metadata.DeviceMetadataRepositoryImpl
import com.github.miwu.data.miot.JvmMiotClientFactory
import com.github.miwu.data.settings.SettingsRepositoryImpl
import com.github.miwu.domain.gateway.MiotClientFactory
import com.github.miwu.domain.repository.AccountRepository
import com.github.miwu.domain.repository.DeviceIconRepository
import com.github.miwu.domain.repository.DeviceMetadataRepository
import com.github.miwu.domain.repository.FavoriteDeviceRepository
import com.github.miwu.domain.repository.HomeRepository
import com.github.miwu.domain.repository.SettingsRepository
import io.ktor.client.HttpClient
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

private const val databaseName = "app_database_v3"

val dataModule = module {
    single<AppDatabase> {
        Room.databaseBuilder<AppDatabase>(get(), databaseName)
            .addMigrations(AppDatabase.MIGRATION_2_3)
            .build()
    }
    single<MiotUserDataStore> {
        get<Context>().miotUserStore
    }
    single<HttpClient> { HttpClient() }
    single<JvmMiotClientFactory>().bind<MiotClientFactory>()
    single<SettingsRepositoryImpl>().bind<SettingsRepository>()
    single<DeviceMetadataRepositoryImpl>().bind<DeviceMetadataRepository>()
    single<AccountRepositoryImpl>().bind<AccountRepository>()
    single<FavoriteDeviceRepositoryImpl>().bind<FavoriteDeviceRepository>()
    single<DeviceIconRepositoryImpl>().bind<DeviceIconRepository>()
    single<HomeDataLoader>()
    single<HomeRepositoryImpl>().bind<HomeRepository>()
}
