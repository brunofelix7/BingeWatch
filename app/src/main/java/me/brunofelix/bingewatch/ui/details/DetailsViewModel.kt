package me.brunofelix.bingewatch.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.brunofelix.bingewatch.data.Result
import me.brunofelix.bingewatch.data.Series
import me.brunofelix.bingewatch.data.SeriesRepository
import me.brunofelix.bingewatch.extension.cancelWorker
import me.brunofelix.bingewatch.util.Constants
import me.brunofelix.bingewatch.util.ResourcesProvider

class DetailsViewModel constructor(
    private val repository: SeriesRepository,
    private val dispatcher: CoroutineDispatcher,
    private val resources: ResourcesProvider
) : ViewModel() {

    private val _liveData = MutableLiveData<UiState>()
    val liveData: LiveData<UiState> get() = _liveData

    fun findSeriesById(id: Long) {
        _liveData.value = UiState.Loading

        viewModelScope.launch(dispatcher) {
            when (val result = repository.findById(id)) {
                is Result.Success -> {
                    withContext(Dispatchers.Main) {
                        _liveData.value = UiState.Success(result.data, false)
                    }
                }
                is Result.Error -> {
                    withContext(Dispatchers.Main) {
                        _liveData.value = UiState.Error(result.message ?: Constants.GENERIC_ERROR)
                    }
                }
            }
        }
    }

    fun deleteSeries(series: Series) {
        _liveData.value = UiState.Loading

        viewModelScope.launch(dispatcher) {
            resources.getContext().cancelWorker(series.id)

            repository.delete(series)

            withContext(Dispatchers.Main) {
                _liveData.value = UiState.Success(null, true)
            }
        }
    }

    sealed class UiState {
        object Loading: UiState()
        class Success(val series: Series?, val isDeleted: Boolean): UiState()
        class Error(val errorMsg: String): UiState()
    }
}