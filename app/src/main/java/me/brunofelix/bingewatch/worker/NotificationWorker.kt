package me.brunofelix.bingewatch.worker

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import me.brunofelix.bingewatch.R
import me.brunofelix.bingewatch.extension.sendNotification

class NotificationWorker constructor(
    context: Context,
    workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val notificationKey = applicationContext.getString(R.string.key_notification)
        val seriesIdKey = applicationContext.getString(R.string.key_series_id)
        val seriesNameKey = applicationContext.getString(R.string.key_series_name)
        val seriesId = inputData.getLong(seriesIdKey, 0)
        val seriesName = inputData.getString(seriesNameKey) ?: "{series}"
        val isNotify = prefs.getBoolean(notificationKey, true)

        if (isNotify) {
            applicationContext.sendNotification(seriesId, seriesName)
        }
        return Result.success()
    }
}