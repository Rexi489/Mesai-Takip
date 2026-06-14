package com.mesaihesap.app.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mesaihesap.app.data.repository.SalarySettingsRepository
import com.mesaihesap.app.data.repository.WorkDayRepository
import com.mesaihesap.app.domain.model.MonthlySummary
import com.mesaihesap.app.domain.model.WorkDay
import com.mesaihesap.app.domain.usecase.CalculateSalaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val workDayRepo: WorkDayRepository,
    private val settingsRepo: SalarySettingsRepository,
    private val calculateSalary: CalculateSalaryUseCase
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow<String?>(null)

    val months: StateFlow<List<String>> =
        workDayRepo.getDistinctMonths().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val summaries: StateFlow<List<MonthlySummary>> = combine(
        months, settingsRepo.getSettings(), workDayRepo.getAllWorkDays()
    ) { monthList, settings, allDays ->
        monthList.map { ym ->
            val days = allDays.filter { it.date.toString().startsWith(ym) }
            calculateSalary(days, settings)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedMonthDays: StateFlow<List<WorkDay>> = _selectedMonth.flatMapLatest { ym ->
        if (ym == null) flowOf(emptyList())
        else workDayRepo.getWorkDaysByMonth(ym)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectMonth(ym: String?) { _selectedMonth.value = ym }
    fun isSelected(ym: String) = _selectedMonth.value == ym
}
