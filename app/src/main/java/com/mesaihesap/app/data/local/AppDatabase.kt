package com.mesaihesap.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mesaihesap.app.data.local.dao.SalarySettingsDao
import com.mesaihesap.app.data.local.dao.WorkDayDao
import com.mesaihesap.app.data.local.entity.SalarySettingsEntity
import com.mesaihesap.app.data.local.entity.WorkDayEntity

@Database(
    entities = [WorkDayEntity::class, SalarySettingsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workDayDao(): WorkDayDao
    abstract fun salarySettingsDao(): SalarySettingsDao
}
