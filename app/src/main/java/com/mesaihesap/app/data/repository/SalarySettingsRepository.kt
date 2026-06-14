package com.mesaihesap.app.data.repository

import com.mesaihesap.app.data.local.dao.SalarySettingsDao
import com.mesaihesap.app.data.mapper.toDomain
import com.mesaihesap.app.data.mapper.toEntity
import com.mesaihesap.app.domain.model.SalarySettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SalarySettingsRepository @Inject constructor(private val dao: SalarySettingsDao) {

    fun getSettings(): Flow<SalarySettings> =
        dao.getSettings().map { it?.toDomain() ?: SalarySettings() }

    suspend fun getSettingsOnce(): SalarySettings =
        dao.getSettingsOnce()?.toDomain() ?: SalarySettings()

    suspend fun saveSettings(settings: SalarySettings) =
        dao.upsertSettings(settings.toEntity())
}
