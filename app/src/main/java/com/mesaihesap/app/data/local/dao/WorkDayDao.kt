package com.mesaihesap.app.data.local.dao

import androidx.room.*
import com.mesaihesap.app.data.local.entity.WorkDayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkDayDao {
    @Query("SELECT * FROM work_days WHERE date LIKE :yearMonth || '%' ORDER BY date ASC")
    fun getWorkDaysByMonth(yearMonth: String): Flow<List<WorkDayEntity>>

    @Query("SELECT * FROM work_days WHERE date = :date LIMIT 1")
    suspend fun getWorkDayByDate(date: String): WorkDayEntity?

    @Query("SELECT * FROM work_days ORDER BY date DESC")
    fun getAllWorkDays(): Flow<List<WorkDayEntity>>

    @Query("SELECT DISTINCT substr(date, 1, 7) FROM work_days ORDER BY date DESC")
    fun getDistinctMonths(): Flow<List<String>>

    @Upsert
    suspend fun upsertWorkDay(workDay: WorkDayEntity)

    @Delete
    suspend fun deleteWorkDay(workDay: WorkDayEntity)

    @Query("DELETE FROM work_days WHERE date = :date")
    suspend fun deleteByDate(date: String)

    @Query("SELECT * FROM work_days WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getWorkDaysBetween(startDate: String, endDate: String): Flow<List<WorkDayEntity>>

    @Query("DELETE FROM work_days WHERE date LIKE :yearMonth || '%'")
    suspend fun deleteWorkDaysByMonth(yearMonth: String)
}
