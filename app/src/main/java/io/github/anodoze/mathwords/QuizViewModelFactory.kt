package io.github.anodoze.mathwords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class QuizViewModelFactory(
    private val scheduler: Scheduler,
    private val confirmKey: Char
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return QuizViewModel(scheduler, confirmKey) as T
    }
}