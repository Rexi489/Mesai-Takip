package com.mesaihesap.app.presentation.calendar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mesaihesap.app.data.repository.SalarySettingsRepository
import com.mesaihesap.app.data.repository.WorkDayRepository
import com.mesaihesap.app.domain.model.SalarySettings
import com.mesaihesap.app.domain.model.WorkDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DayDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val workDayRepo: WorkDayRepository,
    private val settingsRepo: SalarySettingsRepository
) : ViewModel() {

    private val dateStr: String = savedStateHandle["date"] ?: LocalDate.now().toString()
    val date: LocalDate = LocalDate.parse(dateStr)

    private val _workDay = MutableStateFlow<WorkDay?>(null)
    val workDay = _workDay.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved = _saved.asStateFlow()

    val settings = settingsRepo.getSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SalarySettings())

    init {
        viewModelScope.launch {
            _workDay.value = workDayRepo.getWorkDayByDate(date)
                ?: WorkDay(date = date, isSunday = date.dayOfWeek.value == 7)
        }
    }

    // Ayarlardaki giriş/çıkış saatlerini döner
    fun normalGunSaatleri(): Pair<String, String> {
        return settings.value.defaultStartTime to settings.value.defaultEndTime
    }

    fun save(workDay: WorkDay) {
        viewModelScope.launch {
            workDayRepo.saveWorkDay(workDay)
            _saved.value = true
        }
    }

    fun delete() {
        viewModelScope.launch {
            _workDay.value?.let { workDayRepo.deleteWorkDay(it) }
            _saved.value = true
        }
    }
}
