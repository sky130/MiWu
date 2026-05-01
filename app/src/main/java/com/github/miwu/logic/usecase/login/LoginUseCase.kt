package com.github.miwu.logic.usecase.login

import com.github.miwu.MainApplication
import com.github.miwu.logic.datastore.MiotUserDataStore
import com.github.miwu.utils.Logger
import miwu.miot.model.MiotUser
import miwu.miot.provider.MiotLoginProvider

class LoginUseCase(
    private val loginProvider: MiotLoginProvider,
    private val dataStore: MiotUserDataStore,
) {
    private val logger = Logger()

    suspend fun loginByPassword(user: String, password: String): Result<MiotUser> {
        return loginProvider.login(user, password)
            .onSuccess { saveUser(it) }
    }

    suspend fun generateQrCode(): Result<QRCodeData> {
        return loginProvider.generateLoginQrCode()
            .mapCatching { response ->
                val qrcode = response.toQrCode()
                    ?: error("generate login qrcode failure, response=${response}")
                logger.info(
                    "generate login qrcode successfully, qrcode data: {}, login url: {}",
                    qrcode.data,
                    qrcode.loginUrl
                )
                QRCodeData(qrcode.data, qrcode.loginUrl)
            }
    }

    suspend fun loginByQrCode(loginUrl: String): Result<MiotUser> {
        return loginProvider.loginByQrCode(loginUrl)
            .onSuccess { saveUser(it) }
    }

    private suspend fun saveUser(user: MiotUser) {
        logger.info("login successfully")
        val updatedUser = user.copy(deviceId = MainApplication.androidId)
        dataStore.updateData { updatedUser }
    }

    data class QRCodeData(
        val data: String,
        val loginUrl: String,
    )
}
