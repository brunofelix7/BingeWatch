package me.brunofelix.bingewatch.util

import android.content.Context

class ResourcesProvider constructor(private val context: Context) {
    fun getContext(): Context = context.applicationContext
}