package com.dh.myapplication.core.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "flash")
data class Flash(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0L,
    @ColumnInfo(name = "index") val index: Int,
    @ColumnInfo(name = "isFlashOn") val flashOn: Boolean,
    @ColumnInfo(name = "time_trigger") val time_trigger: Long,
    @ColumnInfo(name = "verification_value") val verification_flash: Boolean,
    @ColumnInfo(name = "verification_time") val verification_time: Long,
)