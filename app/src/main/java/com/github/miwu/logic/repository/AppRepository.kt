package com.github.miwu.logic.repository

import com.github.miwu.logic.state.LoginState
import fr.haan.resultat.Resultat
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotScene
import miwu.miot.model.miot.MiotUserInfo


interface AppRepository {

    val miotUser: MiotUser?

    val homes: StateFlow<Resultat<List<MiotHome>>>

    val devices: StateFlow<Resultat<List<MiotDevice>>>

    val scenes: StateFlow<Resultat<List<MiotScene>>>

    val loginStatus: StateFlow<LoginState>

    val userInfo: StateFlow<MiotUserInfo.UserInfo>

    fun refreshAll()

    fun refreshHomes(): Job

    fun refreshDevices(): Job

    fun refreshScenes(): Job

    fun refreshUserInfo(): Job

    fun setActiveHome(home: MiotHome)

    suspend fun runScene(homeId: Long, ownerUid: Long, scene: MiotScene): Result<Unit>

    suspend fun getUserInfo(): Result<MiotUserInfo>
}