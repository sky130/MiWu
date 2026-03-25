package com.github.miwu.logic.repository

import com.github.miwu.logic.repository.entity.MiotHomeData
import com.github.miwu.logic.state.LoginState
import fr.haan.resultat.Resultat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotScene
import miwu.miot.model.miot.MiotUserInfo

typealias ResultatState<T> = StateFlow<Resultat<T>>
typealias MutableResultatState<T> = MutableStateFlow<Resultat<T>>

interface MiotRepository {

    val user: MiotUser?
    val userInfo: StateFlow<MiotUserInfo.UserInfo>
    val loginStatus: StateFlow<LoginState>

    val homes: ResultatState<List<MiotHome>>

    val currentHome: ResultatState<MiotHomeData>

    fun setActiveHome(home: MiotHome)

    fun runScene(scene: MiotScene)

    fun refreshHomes()

    fun refreshCurrentHome()
}