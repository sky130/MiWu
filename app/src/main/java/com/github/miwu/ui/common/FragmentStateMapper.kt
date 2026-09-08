package com.github.miwu.ui.common

import com.github.miwu.domain.model.HomeData
import com.github.miwu.ui.main.state.FragmentState
import fr.haan.resultat.Resultat
import fr.haan.resultat.fold
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun Flow<Resultat<HomeData>>.mapFragmentState(
    isEmpty: (HomeData) -> Boolean,
): Flow<FragmentState> = map { result ->
    result.fold(
        onSuccess = { if (isEmpty(it)) FragmentState.Empty else FragmentState.Normal },
        onFailure = { FragmentState.Error },
        onLoading = { FragmentState.Loading },
    )
}

fun Flow<List<*>>.mapFragmentState(): Flow<FragmentState> =
    map { if (it.isEmpty()) FragmentState.Empty else FragmentState.Normal }
