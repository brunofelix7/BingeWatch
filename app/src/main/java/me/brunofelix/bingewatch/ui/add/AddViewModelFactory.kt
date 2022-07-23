package me.brunofelix.bingewatch.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import me.brunofelix.bingewatch.data.SeriesRepository
import me.brunofelix.bingewatch.util.ResourcesProvider
import java.lang.IllegalArgumentException

class AddViewModelFactory constructor(
    private val repository: SeriesRepository,
    private val dispatcher: CoroutineDispatcher,
    private val resources: ResourcesProvider
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
            AddViewModel(repository, dispatcher, resources) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
    }
}