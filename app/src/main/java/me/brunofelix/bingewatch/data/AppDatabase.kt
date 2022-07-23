package me.brunofelix.bingewatch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Series::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun seriesDao(): SeriesDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
            }
            return INSTANCE as AppDatabase
        }
    }
}