package com.github.miwu.domain.repository

import com.github.miwu.domain.model.LoginState
import kotlinx.coroutines.flow.StateFlow
import miwu.miot.model.MiotUser

interface AccountRepository {
    val currentUser: MiotUser?
    val loginState: StateFlow<LoginState>

    suspend fun saveUser(user: MiotUser): MiotUser

    suspend fun logout()
}
