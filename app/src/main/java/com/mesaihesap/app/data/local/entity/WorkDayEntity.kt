package com.mesaihesap.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "work_days")
data class WorkDayEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,           // ISO format: 2026-06-07
    val startTime: String? = null,      // HH:mm
    val endTime: String? = null,        // HH:mm
    val breakMinutes: Int = 0,
    val overtimeHours: Double = 0.0,
    val isSunday: Boolean = false,
    val isHoliday: Boolean = false,
    val isNightShift: Boolean = false,
    val isLeave: Boolean = false,
    val isReport: Boolean = false,
    val didNotWork: Boolean = false,
    val notes: String = "",
    val workplaceId: Long = 1
)
