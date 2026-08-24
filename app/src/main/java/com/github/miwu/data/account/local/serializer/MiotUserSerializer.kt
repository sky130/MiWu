package com.github.miwu.data.account.local.serializer

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.json.Json
import miwu.miot.model.MiotUser
import java.io.InputStream
import java.io.OutputStream
import java.security.InvalidAlgorithmParameterException
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object MiotUserSerializer : Serializer<MiotUser> {
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val KEY_ALIAS = "miot_user_datastore_key_v2"
    private const val ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val NONCE_LENGTH = 12
    private val header = byteArrayOf('M'.code.toByte(), 'W'.code.toByte(), 'U'.code.toByte(), 2)
    private val secureRandom by lazy { SecureRandom() }

    override val defaultValue: MiotUser = MiotUser("", "", -1L, "", "", "", "", "")

    override suspend fun readFrom(input: InputStream): MiotUser = try {
        val bytes = input.readBytes()
        require(
            bytes.size >= header.size + NONCE_LENGTH &&
                bytes.copyOfRange(0, header.size).contentEquals(header)
        ) { "Unsupported MiotUser storage format" }

        val nonceStart = header.size
        val nonce = bytes.copyOfRange(nonceStart, nonceStart + NONCE_LENGTH)
        val encrypted = bytes.copyOfRange(nonceStart + NONCE_LENGTH, bytes.size)
        require(encrypted.size >= GCM_TAG_LENGTH) { "Invalid MiotUser ciphertext" }
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), GCMParameterSpec(128, nonce))
            updateAAD(header)
        }
        Json.decodeFromString(cipher.doFinal(encrypted).decodeToString())
    } catch (e: Exception) {
        throw CorruptionException("Unable to read MiotUser", e)
    }

    override suspend fun writeTo(t: MiotUser, output: OutputStream) {
        val (nonce, encrypted) = try {
            encrypt(t, getOrCreateSecretKey())
        } catch (_: InvalidAlgorithmParameterException) {
            // Key parameters are immutable; replace a v2 key created before caller IVs were allowed.
            deleteSecretKey()
            encrypt(t, getOrCreateSecretKey())
        }
        output.apply {
            write(header)
            write(nonce)
            write(encrypted)
        }
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER).loadNull()
        val entry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        return entry?.secretKey ?: createSecretKey()
    }

    private fun createSecretKey() =
        KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE_PROVIDER)
            .apply { init(buildKeyGenParams()) }
            .generateKey()

    private fun encrypt(user: MiotUser, key: SecretKey): Pair<ByteArray, ByteArray> {
        val nonce = ByteArray(NONCE_LENGTH).also(secureRandom::nextBytes)
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(128, nonce))
            updateAAD(header)
        }
        val encrypted = Json.encodeToString(user)
            .encodeToByteArray()
            .let(cipher::doFinal)
        return nonce to encrypted
    }

    private fun deleteSecretKey() {
        KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER).loadNull().let { keyStore ->
            if (keyStore.containsAlias(KEY_ALIAS)) keyStore.deleteEntry(KEY_ALIAS)
        }
    }

    private fun buildKeyGenParams() = KeyGenParameterSpec.Builder(
        KEY_ALIAS,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
    )
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setRandomizedEncryptionRequired(false)
        .setKeySize(256)
        .build()

    private fun KeyStore.loadNull() = apply { load(null) }

    private const val GCM_TAG_LENGTH = 16
}
