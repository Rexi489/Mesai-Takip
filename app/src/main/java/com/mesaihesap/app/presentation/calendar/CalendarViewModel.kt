package com.mesaihesap.app.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mesaihesap.app.data.repository.WorkDayRepository
import com.mesaihesap.app.domain.model.WorkDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val workDayRepo: WorkDayRepository
) : ViewModel() {

    private val _currentYearMonth = MutableStateFlow(YearMonth.now())
    val currentYearMonth = _currentYearMonth.asStateFlow()

    val workDays: StateFlow<Map<String, WorkDay>> = _currentYearMonth.flatMapLatest { ym ->
        val key = ym.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        workDayRepo.getWorkDaysByMonth(key).map { list -> list.associateBy { it.date.toString() } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun previousMonth() { _currentYearMonth.value = _currentYearMonth.value.minusMonths(1) }
    fun nextMonth() { _currentYearMonth.value = _currentYearMonth.value.plusMonths(1) }

    fun deleteCurrentMonth() {
        viewModelScope.launch {
            val key = _currentYearMonth.value.format(DateTimeFormatter.ofPattern("yyyy-MM"))
            workDayRepo.deleteWorkDaysByMonth(key)
        }
    }
}
