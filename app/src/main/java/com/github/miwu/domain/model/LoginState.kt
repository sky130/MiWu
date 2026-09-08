package com.github.miwu.domain.model

sealed interface LoginState {
    data object Loading : LoginState
    data object LoggedOut : LoginState
    data object Success : LoginState
    data class NetworkError(val message: String) : LoginState
    data class Failure(val message: String, val cause: Throwable? = null) : LoginState
}
