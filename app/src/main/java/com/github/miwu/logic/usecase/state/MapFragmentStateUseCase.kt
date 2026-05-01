package com.github.miwu.logic.usecase.state

import com.github.miwu.logic.repository.entity.MiotHomeData
import com.github.miwu.ui.main.state.FragmentState
import com.github.miwu.ui.main.state.FragmentState.Empty
import com.github.miwu.ui.main.state.FragmentState.Error
import com.github.miwu.ui.main.state.FragmentState.Loading
import com.github.miwu.ui.main.state.FragmentState.Normal
import fr.haan.resultat.Resultat
import fr.haan.resultat.fold
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MapFragmentStateUseCase {

    operator fun invoke(
        source: Flow<Resultat<MiotHomeData>>,
        selector: (MiotHomeData) -> Boolean
    ): Flow<FragmentState> {
        return source.map { resultat ->
            resultat.fold(
                onSuccess = { if (selector(it)) Empty else Normal },
                onFailure = { Error },
                onLoading = { Loading }
            )
        }
    }

    operator fun invoke(source: Flow<List<*>>): Flow<FragmentState> {
        return source.map { if (it.isEmpty()) Empty else Normal }
    }
}
