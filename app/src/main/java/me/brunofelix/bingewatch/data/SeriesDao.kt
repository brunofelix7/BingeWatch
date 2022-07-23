package me.brunofelix.bingewatch.data

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import org.jetbrains.annotations.NotNull

@Dao
interface SeriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(series: Series): Long

    @Update
    suspend fun update(series: Series)

    @Delete
    suspend fun delete(series: Series)

    @Query("SELECT * FROM series WHERE id = :id")
    suspend fun findById(id: Long): Series?

    @RawQuery(observedEntities = [Series::class])
    fun findAll(@NotNull query: SupportSQLiteQuery): PagingSource<Int, Series>
}