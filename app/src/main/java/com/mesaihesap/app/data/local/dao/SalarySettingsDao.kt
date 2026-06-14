package com.mesaihesap.app.data.local.dao

import androidx.room.*
import com.mesaihesap.app.data.local.entity.SalarySettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SalarySettingsDao {
    @Query("SELECT * FROM salary_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<SalarySettingsEntity?>

    @Query("SELECT * FROM salary_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsOnce(): SalarySettingsEntity?

    @Upsert
    suspend fun upsertSettings(settings: SalarySettingsEntity)
}
