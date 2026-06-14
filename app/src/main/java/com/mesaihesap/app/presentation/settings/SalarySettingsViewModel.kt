package com.mesaihesap.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mesaihesap.app.data.repository.SalarySettingsRepository
import com.mesaihesap.app.domain.model.SalarySettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SalarySettingsViewModel @Inject constructor(
    private val repo: SalarySettingsRepository
) : ViewModel() {

    val settings = repo.getSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SalarySettings())

    fun save(settings: SalarySettings) {
        viewModelScope.launch { repo.saveSettings(settings) }
    }
}
