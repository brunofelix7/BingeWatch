package me.brunofelix.bingewatch.ui.main

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import me.brunofelix.bingewatch.data.Series
import me.brunofelix.bingewatch.data.SeriesRepository

class MainViewModel constructor(
    private val repository: SeriesRepository
) : ViewModel() {

    fun findAll(sortBy: String?): Flow<PagingData<Series>> {
        return Pager(PagingConfig(pageSize = 20, enablePlaceholders = false)) {
            val query = repository.createQuery(sortBy)

            repository.findAll(query)
        }.flow
    }
}