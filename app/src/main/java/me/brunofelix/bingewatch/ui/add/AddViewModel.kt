package me.brunofelix.bingewatch.ui.add

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
import me.brunofelix.bingewatch.extension.scheduleWorker
import me.brunofelix.bingewatch.util.Constants
import me.brunofelix.bingewatch.util.ResourcesProvider

class AddViewModel constructor(
    private val repository: SeriesRepository,
    private val dispatcher: CoroutineDispatcher,
    private val resources: ResourcesProvider
) : ViewModel() {

    private val _liveData = MutableLiveData<UiState>()
    val liveData: LiveData<UiState> get() = _liveData

    fun addNewSeries(series: Series) {
        _liveData.value = UiState.Loading

        viewModelScope.launch(dispatcher) {
            when (val result = repository.insert(series)) {
                is Result.Success -> {
                    resources.getContext().scheduleWorker(result.data!!, series.name, series.duration)

                    withContext(Dispatchers.Main) {
                        _liveData.value = UiState.Success(Constants.SUCCESS_ADD)
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

    fun editSeries(series: Series) {
        _liveData.value = UiState.Loading

        viewModelScope.launch(dispatcher) {
            when (val result = repository.update(series)) {
                is Result.Success -> {
                    withContext(Dispatchers.Main) {
                        resources.getContext().cancelWorker(series.id)
                        resources.getContext().scheduleWorker(series.id, series.name, series.duration)

                        _liveData.value = UiState.Success(Constants.SUCCESS_EDIT)
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

    sealed class UiState {
        object Loading: UiState()
        class Success(val successMsg: String): UiState()
        class Error(val errorMsg: String): UiState()
    }
}