package com.mesaihesap.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mesaihesap.app.data.repository.SalarySettingsRepository
import com.mesaihesap.app.data.repository.WorkDayRepository
import com.mesaihesap.app.domain.model.MonthlySummary
import com.mesaihesap.app.domain.usecase.CalculateSalaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val workDayRepo: WorkDayRepository,
    private val settingsRepo: SalarySettingsRepository,
    private val calculateSalary: CalculateSalaryUseCase
) : ViewModel() {

    private val _currentYearMonth = MutableStateFlow(YearMonth.now())
    val currentYearMonth = _currentYearMonth.asStateFlow()

    val summary: StateFlow<MonthlySummary> = combine(
        _currentYearMonth,
        settingsRepo.getSettings()
    ) { ym, settings ->
        Pair(ym, settings)
    }.flatMapLatest { (ym, settings) ->
        val key = ym.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        workDayRepo.getWorkDaysByMonth(key).map { days -> calculateSalary(days, settings) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MonthlySummary(
        yearMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    ))

    val daysInMonth: Int get() = _currentYearMonth.value.lengthOfMonth()

    fun previousMonth() {
        _currentYearMonth.value = _currentYearMonth.value.minusMonths(1)
    }

    fun nextMonth() {
        val next = _currentYearMonth.value.plusMonths(1)
        if (!next.isAfter(YearMonth.now())) _currentYearMonth.value = next
    }
}
