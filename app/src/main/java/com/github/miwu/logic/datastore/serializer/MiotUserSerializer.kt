package com.github.miwu.logic.datastore.serializer

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.github.miwu.MainApplication
import kotlinx.serialization.json.Json
import miwu.miot.model.MiotUser
import okio.buffer
import okio.cipherSink
import okio.cipherSource
import okio.sink
import okio.source
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

object MiotUserSerializer : Serializer<MiotUser> {
    private const val TRANSFORMATION = "AES/CBC/PKCS7Padding"
    private const val ALGORITHM = "AES"
    private const val IV_LENGTH = 16
    private const val ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val KEY_ALIAS = "miot_user_datastore_key_v1"
    private val androidId get() = MainApplication.androidId
    private val secureRandom by lazy { SecureRandom() }

    override val defaultValue: MiotUser = MiotUser("", "", -1L, "", "", "", "", "")

    override suspend fun readFrom(input: InputStream) = runCatching {
        val key = getOrCreateSecretKey()
        val source = input.source().buffer()
        val ivBytes = source.readByteArray(IV_LENGTH.toLong())
        val ivSpec = IvParameterSpec(ivBytes)
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, key, ivSpec)
        }
        val decryptedJson = source.cipherSource(cipher).buffer().readUtf8()
        Json.decodeFromString<MiotUser>(decryptedJson).injectAndroidId()
    }.getOrElse {
        throw CorruptionException("Unable to read MiotUser", it)
    }

    override suspend fun writeTo(t: MiotUser, output: OutputStream) {
        val key = getOrCreateSecretKey()
        val ivBytes = ByteArray(IV_LENGTH).also { secureRandom.nextBytes(it) }
        val ivSpec = IvParameterSpec(ivBytes)
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, key, ivSpec)
        }
        output.sink().buffer().use { sink ->
            sink.write(ivBytes)
            val jsonString = Json.encodeToString(t)
            sink.cipherSink(cipher).buffer().use { cipheredSink ->
                cipheredSink.writeUtf8(jsonString)
            }
        }
    }

    private fun MiotUser.injectAndroidId() = copy(deviceId = androidId)

    private fun getOrCreateSecretKey(): SecretKey {
        return runCatching {
            KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER)
                .apply { load(null) }
                .takeIf { it.containsAlias(KEY_ALIAS) }
                ?.getEntry(KEY_ALIAS, null)
                ?.let { it as KeyStore.SecretKeyEntry }
                ?.secretKey
        }.getOrNull() ?: createNewKey()
    }

    private fun createNewKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE_PROVIDER
        )
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            setRandomizedEncryptionRequired(false)
            setKeySize(256)
        }.build()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }
}