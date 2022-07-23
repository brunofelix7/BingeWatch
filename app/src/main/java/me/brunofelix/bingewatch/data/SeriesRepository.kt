package me.brunofelix.bingewatch.data

import androidx.paging.PagingSource
import androidx.sqlite.db.SupportSQLiteQuery

interface SeriesRepository {

    suspend fun insert(series: Series): Result<Long>

    suspend fun update(series: Series): Result<Unit>

    suspend fun delete(series: Series): Result<Unit>

    suspend fun findById(id: Long): Result<Series>

    fun findAll(query: SupportSQLiteQuery): PagingSource<Int, Series>

    fun createQuery(sortBy: String?): SupportSQLiteQuery
}