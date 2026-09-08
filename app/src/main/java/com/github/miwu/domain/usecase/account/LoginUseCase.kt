package com.github.miwu.domain.usecase.account

import com.github.miwu.di.IoDispatcher
import com.github.miwu.domain.repository.AccountRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import miwu.miot.model.MiotUser
import miwu.miot.provider.MiotLoginProvider
import org.koin.core.annotation.Factory

@Factory
class LoginUseCase(
    private val loginProvider: MiotLoginProvider,
    private val accountRepository: AccountRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun loginByPassword(user: String, password: String): Result<MiotUser> =
        withContext(ioDispatcher) {
            loginProvider.login(user, password).rethrowCancellation().saveAuthenticatedUser()
        }

    suspend fun generateQrCode(): Result<QrCodeData> = withContext(ioDispatcher) {
        loginProvider.generateLoginQrCode().rethrowCancellation().mapCatching { response ->
            val qrCode = response.toQrCode() ?: error("generate login qrcode failure")
            QrCodeData(qrCode.data, qrCode.loginUrl)
        }
    }

    suspend fun loginByQrCode(loginUrl: String): Result<MiotUser> = withContext(ioDispatcher) {
        loginProvider.loginByQrCode(loginUrl).rethrowCancellation().saveAuthenticatedUser()
    }

    private suspend fun Result<MiotUser>.saveAuthenticatedUser(): Result<MiotUser> {
        val user = getOrElse { return Result.failure(it) }
        return try {
            Result.success(accountRepository.saveUser(user))
        } catch (error: CancellationException) {
            throw error
        } catch (error: Throwable) {
            Result.failure(error)
        }
    }

    private fun <T> Result<T>.rethrowCancellation(): Result<T> =
        onFailure { if (it is CancellationException) throw it }

    data class QrCodeData(
        val data: String,
        val loginUrl: String,
    )
}
