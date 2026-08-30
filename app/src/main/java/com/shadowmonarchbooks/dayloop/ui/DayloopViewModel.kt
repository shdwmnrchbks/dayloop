package com.shadowmonarchbooks.dayloop.ui

import androidx.lifecycle.ViewModel
import com.shadowmonarchbooks.dayloop.data.PackStore
import com.shadowmonarchbooks.dayloop.data.PacksState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

/** Read-only app state holder; the progress layer (Phase 3) extends from here. */
@HiltViewModel
class DayloopViewModel @Inject constructor(
    private val store: PackStore,
) : ViewModel() {

    val state: StateFlow<PacksState> = store.state

    fun selectPack(slug: String) = store.select(slug)

    fun moveCurrent(delta: Int) = store.moveCurrent(delta)
}
