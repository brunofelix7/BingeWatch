package me.brunofelix.bingewatch.data

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import me.brunofelix.bingewatch.util.Constants

class SeriesRepositoryImpl constructor(
    private val dao: SeriesDao
) : SeriesRepository {

    override suspend fun insert(series: Series): Result<Long> {
        return try {
            val result = dao.insert(series)

            if (result > 0) {
                Result.Success(result)
            } else {
                Result.Error(Constants.GENERIC_ERROR)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: Constants.GENERIC_ERROR)
        }
    }

    override suspend fun update(series: Series): Result<Unit> {
        return try {
            dao.update(series)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: Constants.GENERIC_ERROR)
        }
    }

    override suspend fun delete(series: Series): Result<Unit> {
        return try {
            dao.delete(series)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: Constants.GENERIC_ERROR)
        }
    }

    override suspend fun findById(id: Long): Result<Series> {
        return try {
            val series = dao.findById(id)

            if (series != null) {
                Result.Success(series)
            } else {
                Result.Error(Constants.NOT_FOUND_ERROR)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: Constants.GENERIC_ERROR)
        }
    }

    override fun findAll(query: SupportSQLiteQuery) = dao.findAll(query)

    override fun createQuery(sortBy: String?): SupportSQLiteQuery {
        val defaultQuery = StringBuilder("SELECT * FROM series")

        when (sortBy) {
            SeriesSortByEnum.NAME.value -> {
                defaultQuery.append(" ORDER BY name ASC")
            }
            SeriesSortByEnum.START_DATE.value -> {
                defaultQuery.append(" ORDER BY start_date ASC")
            }
        }
        return SimpleSQLiteQuery(defaultQuery.toString())
    }
}