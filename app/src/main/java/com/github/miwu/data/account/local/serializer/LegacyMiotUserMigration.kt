package com.github.miwu.data.account.local.serializer

import android.content.Context
import androidx.datastore.core.DataMigration
import kotlinx.serialization.json.Json
import miwu.miot.model.MiotUser
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

class LegacyMiotUserMigration(
    context: Context,
) : DataMigration<MiotUser> {
    private val legacyFile = context.filesDir.resolve("datastore/miot_user.json")
    private var migrated = false

    override suspend fun shouldMigrate(currentData: MiotUser): Boolean = legacyFile.exists()

    override suspend fun migrate(currentData: MiotUser): MiotUser {
        if (currentData.hasCredentials()) {
            migrated = true
            return currentData
        }
        val migratedUser = runCatching {
            val bytes = legacyFile.readBytes()
            require(bytes.size > IV_LENGTH)
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER).apply { load(null) }
            val key = (keyStore.getEntry(LEGACY_KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
            val cipher = Cipher.getInstance(LEGACY_TRANSFORMATION).apply {
                init(Cipher.DECRYPT_MODE, key, IvParameterSpec(bytes.copyOfRange(0, IV_LENGTH)))
            }
            Json.decodeFromString<MiotUser>(
                cipher.doFinal(bytes.copyOfRange(IV_LENGTH, bytes.size)).decodeToString()
            )
        }.getOrNull()
        migrated = migratedUser != null
        return migratedUser ?: MiotUserSerializer.defaultValue
    }

    override suspend fun cleanUp() {
        if (!migrated) return
        legacyFile.delete()
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER).apply { load(null) }
        if (keyStore.containsAlias(LEGACY_KEY_ALIAS)) keyStore.deleteEntry(LEGACY_KEY_ALIAS)
    }

    private fun MiotUser.hasCredentials(): Boolean =
        userId.isNotEmpty() && cUserId.isNotEmpty() && passToken.isNotEmpty()

    private companion object {
        const val LEGACY_TRANSFORMATION = "AES/CBC/PKCS7Padding"
        const val ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore"
        const val LEGACY_KEY_ALIAS = "miot_user_datastore_key_v1"
        const val IV_LENGTH = 16
    }
}
