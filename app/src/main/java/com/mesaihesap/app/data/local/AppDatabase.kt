package com.mesaihesap.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mesaihesap.app.data.local.dao.SalarySettingsDao
import com.mesaihesap.app.data.local.dao.WorkDayDao
import com.mesaihesap.app.data.local.entity.SalarySettingsEntity
import com.mesaihesap.app.data.local.entity.WorkDayEntity

@Database(
    entities = [WorkDayEntity::class, SalarySettingsEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workDayDao(): WorkDayDao
    abstract fun salarySettingsDao(): SalarySettingsDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE salary_settings ADD COLUMN themeMode TEXT NOT NULL DEFAULT 'SYSTEM'")
            }
        }
    }
}
