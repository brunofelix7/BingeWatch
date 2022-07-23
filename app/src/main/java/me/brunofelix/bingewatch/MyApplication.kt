package me.brunofelix.bingewatch

import android.app.Application
import me.brunofelix.bingewatch.extension.changeAppTheme
import me.brunofelix.bingewatch.extension.createNotificationChannel
import me.brunofelix.bingewatch.util.initDebugLog

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initDebugLog()
        createNotificationChannel()
        changeAppTheme()
    }
}