package me.brunofelix.bingewatch.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SeriesDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var appContext: Context
    private lateinit var database: AppDatabase
    private lateinit var dao: SeriesDao

    @Before
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext

        database = Room.inMemoryDatabaseBuilder(
            appContext,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.seriesDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertTest() = runBlockingTest {
        val series = Series(
            id = 0,
            name = "Foundation",
            startDate = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis(),
            duration = 5)

        dao.insert(series)

        val seriesInserted = dao.findById(1)

        assertThat(seriesInserted?.id).isEqualTo(1)
    }

    @Test
    fun deleteTest() = runBlockingTest {
        val series = Series(
            id = 1,
            name = "Foundation",
            startDate = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis(),
            duration = 5)

        dao.insert(series)
        dao.delete(series)

        val seriesInserted = dao.findById(1)

        assertThat(seriesInserted).isNull()
    }
}