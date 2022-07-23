package me.brunofelix.bingewatch.util

import me.brunofelix.bingewatch.BuildConfig
import timber.log.Timber
import java.sql.Time
import java.sql.Timestamp
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

fun convertFromTimestamp(timeStamp: Long): String {
    return try {
        val sdf = SimpleDateFormat(Constants.PATTERN_MM_DD_YYYY_K_MM_A, Locale.ENGLISH)
        val date = Date(Timestamp(timeStamp).time)
        sdf.format(date)
    } catch (e: Exception) {
        Constants.UNKNOWN_ERROR
    }
}

fun convertToTimestamp(date: String): Long {
    return try {
        val sdf = SimpleDateFormat(Constants.PATTERN_MM_DD_YYYY_K_MM_A, Locale.ENGLISH)
        val parsedDate = sdf.parse(date) ?: return 0
        Timestamp(parsedDate.time).time
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}

fun getDuration(timeStamp: Long): Long {
    return try {
        val diff = abs(timeStamp - System.currentTimeMillis())
        TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS)
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}

fun getDiffBetweenDates(timeStamp: Long): Long {
    return try {
        Timestamp(timeStamp).time.minus(System.currentTimeMillis())
    } catch (e: Exception) {
        0
    }
}

fun getTime(hr: Int, min: Int): String? {
    return try {
        val tme = Time(hr, min, 0)
        val formatter: Format
        formatter = SimpleDateFormat(Constants.PATTERN_H_MM_A, Locale.ENGLISH)
        formatter.format(tme)
    } catch (e: Exception) {
        Constants.UNKNOWN_ERROR
    }
}

fun initDebugLog() {
    if (BuildConfig.DEBUG) {
        Timber.plant(Timber.DebugTree())
    }
}