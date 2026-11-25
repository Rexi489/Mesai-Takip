package com.mesaihesap.app.presentation.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mesaihesap.app.data.repository.SalarySettingsRepository
import com.mesaihesap.app.data.repository.WorkDayRepository
import com.mesaihesap.app.domain.model.SalarySettings
import com.mesaihesap.app.domain.usecase.CalculateSalaryUseCase
import com.mesaihesap.app.util.JsonBackup
import com.mesaihesap.app.util.PdfExporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SalarySettingsViewModel @Inject constructor(
    private val repo: SalarySettingsRepository,
    private val workDayRepo: WorkDayRepository,
    private val calculateSalary: CalculateSalaryUseCase
) : ViewModel() {

    val settings = repo.getSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SalarySettings())

    fun save(settings: SalarySettings) {
        viewModelScope.launch { repo.saveSettings(settings) }
    }

    fun exportCurrentMonthPdf(context: Context) {
        viewModelScope.launch {
            val currentYm = YearMonth.now()
            val key = currentYm.format(DateTimeFormatter.ofPattern("yyyy-MM"))
            val days = workDayRepo.getWorkDaysByMonth(key).first()
            val settingsVal = repo.getSettingsOnce()
            val summary = calculateSalary(days, settingsVal, key)
            val file = PdfExporter.export(context, summary, settingsVal, days)
            file?.let { PdfExporter.openPdf(context, it) }
        }
    }

    fun exportBackup(context: Context) {
        viewModelScope.launch {
            val allDays = workDayRepo.getAllWorkDays().first()
            val currentSettings = repo.getSettingsOnce()
            val file = JsonBackup.exportFile(context, currentSettings, allDays)
            JsonBackup.shareBackupFile(context, file)
        }
    }

    fun importBackupFromUri(context: Context, uri: Uri, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val jsonStr = context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.bufferedReader().readText()
                } ?: ""

                if (jsonStr.isBlank()) {
                    onResult(false, "Yedek dosyası okunamadı.")
                    return@launch
                }

                val backupData = JsonBackup.importFromJson(jsonStr)
                backupData.settings?.let { repo.saveSettings(it) }
                backupData.workDays.forEach { workDayRepo.saveWorkDay(it) }

                onResult(true, "Yedek başarıyla yüklendi (${backupData.workDays.size} gün)")
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, "Geçersiz yedek dosyası: ${e.localizedMessage}")
            }
        }
    }
}
