package com.mesaihesap.app.data.repository

import com.mesaihesap.app.data.local.dao.WorkDayDao
import com.mesaihesap.app.data.mapper.toDomain
import com.mesaihesap.app.data.mapper.toEntity
import com.mesaihesap.app.domain.model.WorkDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkDayRepository @Inject constructor(private val dao: WorkDayDao) {

    fun getWorkDaysByMonth(yearMonth: String): Flow<List<WorkDay>> =
        dao.getWorkDaysByMonth(yearMonth).map { list -> list.map { it.toDomain() } }

    suspend fun getWorkDayByDate(date: LocalDate): WorkDay? =
        dao.getWorkDayByDate(date.toString())?.toDomain()

    fun getAllWorkDays(): Flow<List<WorkDay>> =
        dao.getAllWorkDays().map { list -> list.map { it.toDomain() } }

    fun getDistinctMonths(): Flow<List<String>> = dao.getDistinctMonths()

    suspend fun saveWorkDay(workDay: WorkDay) = dao.upsertWorkDay(workDay.toEntity())

    suspend fun deleteWorkDay(workDay: WorkDay) = dao.deleteWorkDay(workDay.toEntity())

    suspend fun deleteWorkDaysByMonth(yearMonth: String) = dao.deleteWorkDaysByMonth(yearMonth)

    fun getWorkDaysBetween(start: LocalDate, end: LocalDate): Flow<List<WorkDay>> =
        dao.getWorkDaysBetween(start.toString(), end.toString()).map { it.map { e -> e.toDomain() } }
}
