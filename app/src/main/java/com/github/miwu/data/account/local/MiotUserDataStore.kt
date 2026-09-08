package com.github.miwu.data.account.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import com.github.miwu.data.account.local.serializer.LegacyMiotUserMigration
import com.github.miwu.data.account.local.serializer.MiotUserSerializer
import miwu.miot.model.MiotUser

typealias MiotUserDataStore = DataStore<MiotUser>

val Context.miotUserStore: MiotUserDataStore by dataStore(
    fileName = "miot_user_v2",
    serializer = MiotUserSerializer,
    corruptionHandler = ReplaceFileCorruptionHandler { MiotUserSerializer.defaultValue },
    produceMigrations = { context -> listOf(LegacyMiotUserMigration(context)) },
)
