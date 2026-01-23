package com.github.miwu.logic.state

sealed interface LoginState {
    object Loading : LoginState
    object Success : LoginState
    data class NetworkError(val message: String) : LoginState
    data class Failure(val message: String, val cause: Throwable? = null) : LoginState
}