package me.brunofelix.bingewatch.ui.main

import me.brunofelix.bingewatch.data.Series

interface MainClickListener {
    fun onItemClick(series: Series)
}