package me.brunofelix.bingewatch.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Series(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "start_date")
    val startDate: Long,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "duration")
    val duration: Long
) : Parcelable