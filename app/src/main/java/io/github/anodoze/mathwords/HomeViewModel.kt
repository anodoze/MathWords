package io.github.anodoze.mathwords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val db: MathWordsDatabase) : ViewModel() {
    val cardsByOperation: StateFlow<Map<Operation, List<Card>>> =
        flow {
            val map = Operation.entries.associateWith { op ->
                db.cardDao().getIntroducedCards(op)
            }
            emit(map)
        }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())
}