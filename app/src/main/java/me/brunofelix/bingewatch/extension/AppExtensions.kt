package me.brunofelix.bingewatch.extension

import android.app.*
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import me.brunofelix.bingewatch.R
import me.brunofelix.bingewatch.data.SeriesSortByEnum
import me.brunofelix.bingewatch.ui.details.DetailsActivity
import me.brunofelix.bingewatch.worker.NotificationWorker
import java.util.concurrent.TimeUnit

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun View.snackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

fun Context.changeAppTheme() {
    val prefs = PreferenceManager.getDefaultSharedPreferences(this)
    val key = getString(R.string.key_theme)
    val defaultValue = getString(R.string.value_auto)

    when (prefs.getString(key, defaultValue)) {
        getString(R.string.value_auto) -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        getString(R.string.value_night_off) -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        getString(R.string.value_night_on) -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}

fun Context.createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = getString(R.string.channel_name)
        val descriptionText = resources.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(getString(R.string.channel_id), name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun Context.sendNotification(seriesId: Long, seriesName: String) {
    val intent = Intent(this, DetailsActivity::class.java).apply {
        putExtra(getString(R.string.key_series_id), seriesId)
    }
    val pendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
        addNextIntentWithParentStack(intent)
        getPendingIntent(0, FLAG_UPDATE_CURRENT)
    }

    val builder = NotificationCompat.Builder(this, getString(R.string.channel_id))
        .setSmallIcon(R.drawable.ic_movie_open)
        .setContentTitle(seriesName)
        .setColor(ContextCompat.getColor(this, R.color.primary_color))
        .setContentText(getString(R.string.notify_content))
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(this)) {
        notify(seriesId.toInt(), builder.build())
    }
}

fun Context.scheduleWorker(seriesId: Long, seriesName: String, duration: Long) {
    val inputData = Data.Builder().apply {
        putLong(getString(R.string.key_series_id), seriesId)
        putString(getString(R.string.key_series_name), seriesName)
    }

    val notificationWork = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
        .setInitialDelay(duration, TimeUnit.MINUTES)
        .setInputData(inputData.build())
        .addTag(seriesId.toString())
        .build()
    WorkManager.getInstance(this).enqueue(notificationWork)
}

fun Context.cancelWorker(seriesId: Long) {
    WorkManager.getInstance(this).cancelAllWorkByTag(seriesId.toString())
}

fun Context.putSortBy(value: String) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(this)
    prefs.edit().apply {
        putString(getString(R.string.key_sort_by), value)
        apply()
    }
}

fun Context.getSortBy(): String? {
    val prefs = PreferenceManager.getDefaultSharedPreferences(this)
    return prefs.getString(getString(R.string.key_sort_by), SeriesSortByEnum.NAME.value)
}

fun Activity.hideKeyboard() {
    val view = this.currentFocus
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
