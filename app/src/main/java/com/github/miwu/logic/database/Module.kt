package com.github.miwu.logic.database

import androidx.room.Room
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            get(), AppDatabase::class.java, "app_latest_database"
        ).fallbackToDestructiveMigration(false).build()
    }
}