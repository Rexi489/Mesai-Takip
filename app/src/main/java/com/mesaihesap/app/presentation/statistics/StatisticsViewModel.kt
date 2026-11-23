package com.mesaihesap.app.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mesaihesap.app.data.repository.SalarySettingsRepository
import com.mesaihesap.app.data.repository.WorkDayRepository
import com.mesaihesap.app.domain.model.MonthlySummary
import com.mesaihesap.app.domain.model.WorkDay
import com.mesaihesap.app.domain.usecase.CalculateSalaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class WeeklyData(val label: String, val hours: Double)
data class MonthlyData(val label: String, val salary: Double, val hours: Double)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val workDayRepo: WorkDayRepository,
    private val settingsRepo: SalarySettingsRepository,
    private val calculateSalary: CalculateSalaryUseCase
) : ViewModel() {

    val allWorkDays: StateFlow<List<WorkDay>> =
        workDayRepo.getAllWorkDays().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val last12MonthsSummaries: StateFlow<List<MonthlySummary>> = combine(
        allWorkDays, settingsRepo.getSettings()
    ) { days, settings ->
        val now = YearMonth.now()
        (0..11).map { offset ->
            val ym = now.minusMonths(offset.toLong())
            val key = ym.format(DateTimeFormatter.ofPattern("yyyy-MM"))
            val monthDays = days.filter { it.date.toString().startsWith(key) }
            calculateSalary(monthDays, settings, key)
        }.reversed()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weeklyData: StateFlow<List<WeeklyData>> = allWorkDays.map { days ->
        val today = LocalDate.now()
        val weekStart = today.with(DayOfWeek.MONDAY)
        (0..6).map { offset ->
            val d = weekStart.plusDays(offset.toLong())
            val hours = days.firstOrNull { it.date == d }?.workedHours ?: 0.0
            WeeklyData(d.format(DateTimeFormatter.ofPattern("EEE", java.util.Locale("tr"))), hours)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val averageDailyHours: StateFlow<Double> = allWorkDays.map { days ->
        val worked = days.filter { it.isWorked }
        if (worked.isEmpty()) 0.0 else worked.sumOf { it.workedHours } / worked.size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
}
