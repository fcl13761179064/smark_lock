package com.room.database.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Door")
data class Door(
    @PrimaryKey(autoGenerate = false)
    var time: String,
    var data: String,
    var isType: String,
) : Parcelable