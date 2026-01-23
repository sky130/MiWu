package com.github.miwu.logic.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.github.miwu.logic.datastore.serializer.MiotUserSerializer
import kotlinx.coroutines.flow.first
import miwu.miot.model.MiotUser
import org.koin.dsl.module

typealias MiotUserDataStore = DataStore<MiotUser>

suspend fun MiotUserDataStore.isLogin(): Boolean = runCatching {
    data.first().run {
        listOf(
            userId,
            cUserId,
            ssecurity,
            psecurity,
            passToken,
            serviceToken
        ).all(String::isNotEmpty) && nonce != -1L
    }
}.getOrNull() ?: false

private val Context.miotUserStore: MiotUserDataStore by dataStore(
    fileName = "miot_user.json",
    serializer = MiotUserSerializer,
)

val dataStoreModule = module {
    single<MiotUserDataStore> {
        get<Context>().miotUserStore
    }
}