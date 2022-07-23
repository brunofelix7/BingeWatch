package me.brunofelix.bingewatch.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.brunofelix.bingewatch.data.SeriesRepository
import java.lang.IllegalArgumentException

class MainViewModelFactory constructor(
    private val repository: SeriesRepository
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(repository) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
    }
}