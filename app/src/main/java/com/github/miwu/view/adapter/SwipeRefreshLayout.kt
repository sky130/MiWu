package com.github.miwu.view.adapter

import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.miwu.ui.main.state.FragmentState
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
    this.isRefreshing = resultat.isLoading
}

@BindingAdapter("isRefreshing")
fun SwipeRefreshLayout.isRefreshing(state: FragmentState) {
    this.isRefreshing = state == FragmentState.Loading
}