package com.github.miwu.adapter

import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.miwu.ui.main.FragmentState
import com.github.miwu.ui.main.FragmentState.Empty
import com.github.miwu.ui.main.FragmentState.Normal
import fr.haan.resultat.Resultat
import fr.haan.resultat.onFailure
import fr.haan.resultat.onLoading
import fr.haan.resultat.onSuccess

@BindingAdapter("onRefresh")
fun SwipeRefreshLayout.onRefresh(onRefresh: SwipeRefreshLayout.OnRefreshListener?) {
    setOnRefreshListener(onRefresh)
}

@BindingAdapter("isRefreshing")
fun SwipeRefreshLayout.isRefreshing(isRefreshing: Boolean) {
    this.isRefreshing = isRefreshing
}

@BindingAdapter("isRefreshing")
fun SwipeRefreshLayout.isRefreshing(resultat: Resultat<*>) {
    resultat.onSuccess { devices ->
        isRefreshing = false
    }.onLoading {
        isRefreshing = true
    }.onFailure {
        isRefreshing = false
    }
}