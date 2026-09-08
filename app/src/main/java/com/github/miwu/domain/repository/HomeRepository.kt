package com.github.miwu.domain.repository

import com.github.miwu.domain.model.HomeData
import fr.haan.resultat.Resultat
import kotlinx.coroutines.flow.StateFlow
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotScene
import miwu.miot.model.miot.UserInfo

typealias ResultState<T> = StateFlow<Resultat<T>>

interface HomeRepository {
    val userInfo: StateFlow<UserInfo>
    val homes: ResultState<List<MiotHome>>
    val currentHome: ResultState<HomeData>

    suspend fun selectHome(home: MiotHome): Result<Unit>

    suspend fun runScene(scene: MiotScene): Result<Unit>

    suspend fun refreshHomes()

    suspend fun refreshCurrentHome()
}
