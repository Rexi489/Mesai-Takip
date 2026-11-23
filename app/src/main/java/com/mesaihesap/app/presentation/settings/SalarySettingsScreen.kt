package com.mesaihesap.app.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mesaihesap.app.domain.model.SalarySettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalarySettingsScreen(
    onBack: () -> Unit,
    viewModel: SalarySettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()

    var monthlySalary by remember(settings) { mutableStateOf(settings.monthlySalary.toFormattedString()) }
    var dailyHours by remember(settings) { mutableStateOf(settings.dailyHours.toFormattedString()) }
    var monthlyWorkDays by remember(settings) { mutableStateOf(settings.monthlyWorkDays.toString()) }
    var useAutoHourlyRate by remember(settings) { mutableStateOf(settings.useAutoHourlyRate) }
    var manualHourlyRate by remember(settings) { mutableStateOf(settings.manualHourlyRate.toFormattedString()) }
    var overtimeMultiplier by remember(settings) { mutableStateOf(settings.overtimeMultiplier.toFormattedString()) }
    var sundayMultiplier by remember(settings) { mutableStateOf(settings.sundayMultiplier.toFormattedString()) }
    var holidayMultiplier by remember(settings) { mutableStateOf(settings.holidayMultiplier.toFormattedString()) }
    var nightShiftBonus by remember(settings) { mutableStateOf(settings.nightShiftBonus.toFormattedString()) }
    var mealAllowance by remember(settings) { mutableStateOf(settings.mealAllowance.toFormattedString()) }
    var transportAllowance by remember(settings) { mutableStateOf(settings.transportAllowance.toFormattedString()) }
    var bonus by remember(settings) { mutableStateOf(settings.bonus.toFormattedString()) }
    var advance by remember(settings) { mutableStateOf(settings.advance.toFormattedString()) }
    var deductions by remember(settings) { mutableStateOf(settings.deductions.toFormattedString()) }
    var salaryDay by remember(settings) { mutableStateOf(settings.salaryDayOfMonth.toString()) }
    var workplaceName by remember(settings) { mutableStateOf(settings.workplaceName) }
    var defaultStartTime by remember(settings) { mutableStateOf(settings.defaultStartTime) }
    var defaultEndTime by remember(settings) { mutableStateOf(settings.defaultEndTime) }

    val computedHourlyRate = if (useAutoHourlyRate) {
        val s = monthlySalary.parseCleanDouble() ?: 0.0
        val d = monthlyWorkDays.parseCleanInt() ?: 26
        val h = dailyHours.parseCleanDouble() ?: 8.0
        if (d > 0 && h > 0) s / (d * h) else 0.0
    } else manualHourlyRate.parseCleanDouble() ?: 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Maaş Ayarları") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    viewModel.save(SalarySettings(
                        monthlySalary = monthlySalary.parseCleanDouble() ?: 0.0,
                        dailyHours = dailyHours.parseCleanDouble() ?: 7.5,
                        monthlyWorkDays = monthlyWorkDays.parseCleanInt() ?: 26,
                        useAutoHourlyRate = useAutoHourlyRate,
                        manualHourlyRate = manualHourlyRate.parseCleanDouble() ?: 0.0,
                        overtimeMultiplier = overtimeMultiplier.parseCleanDouble() ?: 1.5,
                        sundayMultiplier = sundayMultiplier.parseCleanDouble() ?: 1.5,
                        holidayMultiplier = holidayMultiplier.parseCleanDouble() ?: 2.0,
                        nightShiftBonus = nightShiftBonus.parseCleanDouble() ?: 0.0,
                        mealAllowance = mealAllowance.parseCleanDouble() ?: 0.0,
                        transportAllowance = transportAllowance.parseCleanDouble() ?: 0.0,
                        bonus = bonus.parseCleanDouble() ?: 0.0,
                        advance = advance.parseCleanDouble() ?: 0.0,
                        deductions = deductions.parseCleanDouble() ?: 0.0,
                        salaryDayOfMonth = salaryDay.parseCleanInt() ?: 15,
                        workplaceName = workplaceName,
                        defaultStartTime = defaultStartTime.ifBlank { "08:30" },
                        defaultEndTime = defaultEndTime.ifBlank { "17:00" }
                    ))
                    onBack()
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) { Text("Kaydet", fontWeight = FontWeight.Bold) }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingSection("İş Yeri")
            SettingTextField("İş Yeri Adı", workplaceName, { workplaceName = it })

            SettingSection("Temel Maaş")
            SettingTextField("Aylık Net Maaş (₺)", monthlySalary, { monthlySalary = it }, KeyboardType.Decimal)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingTextField("Günlük Saat", dailyHours, { dailyHours = it }, KeyboardType.Decimal, Modifier.weight(1f))
                SettingTextField("Aylık Gün", monthlyWorkDays, { monthlyWorkDays = it }, KeyboardType.Number, Modifier.weight(1f))
            }

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Saatlik: ${String.format("%.2f", computedHourlyRate)} ₺", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Switch(checked = useAutoHourlyRate, onCheckedChange = { useAutoHourlyRate = it })
                }
                if (!useAutoHourlyRate) {
                    SettingTextField("Manuel Saatlik Ücret (₺)", manualHourlyRate, { manualHourlyRate = it }, KeyboardType.Decimal, Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp))
                }
            }

            SettingSection("Katsayılar")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingTextField("Fazla Mesai", overtimeMultiplier, { overtimeMultiplier = it }, KeyboardType.Decimal, Modifier.weight(1f))
                SettingTextField("Pazar", sundayMultiplier, { sundayMultiplier = it }, KeyboardType.Decimal, Modifier.weight(1f))
                SettingTextField("Resmi Tatil", holidayMultiplier, { holidayMultiplier = it }, KeyboardType.Decimal, Modifier.weight(1f))
            }

            SettingSection("Ek Ödemeler")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingTextField("Yemek (₺)", mealAllowance, { mealAllowance = it }, KeyboardType.Decimal, Modifier.weight(1f))
                SettingTextField("Yol (₺)", transportAllowance, { transportAllowance = it }, KeyboardType.Decimal, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingTextField("Prim (₺)", bonus, { bonus = it }, KeyboardType.Decimal, Modifier.weight(1f))
                SettingTextField("Gece Primi (₺)", nightShiftBonus, { nightShiftBonus = it }, KeyboardType.Decimal, Modifier.weight(1f))
            }

            SettingSection("Kesintiler")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingTextField("Avans (₺)", advance, { advance = it }, KeyboardType.Decimal, Modifier.weight(1f))
                SettingTextField("Kesinti (₺)", deductions, { deductions = it }, KeyboardType.Decimal, Modifier.weight(1f))
            }

            SettingSection("Varsayılan Çalışma Saatleri")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingTextField("Giriş Saati", defaultStartTime, { defaultStartTime = it }, KeyboardType.Number, Modifier.weight(1f))
                SettingTextField("Çıkış Saati", defaultEndTime, { defaultEndTime = it }, KeyboardType.Number, Modifier.weight(1f))
            }

            SettingSection("Maaş Günü")
            SettingTextField("Ayın Kaçı", salaryDay, { salaryDay = it }, KeyboardType.Number)
        }
    }
}

@Composable
private fun SettingSection(title: String) {
    Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
}

@Composable
private fun SettingTextField(
    label: String, value: String, onChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        value = value, onValueChange = onChange, modifier = modifier, label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType), singleLine = true
    )
}

private fun Double.toFormattedString(): String = if (this == 0.0) "" else if (this == this.toLong().toDouble()) this.toLong().toString() else this.toString()

private fun String.parseCleanDouble(): Double? = this.replace(',', '.').trim().toDoubleOrNull()
private fun String.parseCleanInt(): Int? = this.replace(',', '.').trim().toIntOrNull()
