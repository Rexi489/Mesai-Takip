package com.mesaihesap.app.presentation.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mesaihesap.app.domain.model.WorkDay
import com.mesaihesap.app.presentation.theme.WorkedGreen
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailScreen(
    date: String,
    onBack: () -> Unit,
    viewModel: DayDetailViewModel = hiltViewModel()
) {
    val workDay by viewModel.workDay.collectAsState()
    val saved by viewModel.saved.collectAsState()
    val settings by viewModel.settings.collectAsState()

    LaunchedEffect(saved) { if (saved) onBack() }

    workDay?.let { day ->
        var startTime by remember(day) { mutableStateOf(day.startTime ?: "") }
        var endTime by remember(day) { mutableStateOf(day.endTime ?: "") }
        var breakMinutes by remember(day) { mutableStateOf(day.breakMinutes.toString()) }
        var overtimeHours by remember(day) { mutableStateOf(day.overtimeHours.toString()) }
        var isSunday by remember(day) { mutableStateOf(day.isSunday) }
        var isHoliday by remember(day) { mutableStateOf(day.isHoliday) }
        var isNightShift by remember(day) { mutableStateOf(day.isNightShift) }
        var isLeave by remember(day) { mutableStateOf(day.isLeave) }
        var isReport by remember(day) { mutableStateOf(day.isReport) }
        var didNotWork by remember(day) { mutableStateOf(day.didNotWork) }
        var notes by remember(day) { mutableStateOf(day.notes) }

        val dateTitle = day.date.format(DateTimeFormatter.ofPattern("d MMMM yyyy, EEEE", Locale("tr")))
            .replaceFirstChar { it.uppercase() }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(dateTitle, style = MaterialTheme.typography.titleMedium) },
                    navigationIcon = {
                        IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.delete() }) {
                            Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                        }
                    }
                )
            },
            bottomBar = {
                Button(
                    onClick = {
                        viewModel.save(
                            day.copy(
                                startTime = startTime.ifBlank { null },
                                endTime = endTime.ifBlank { null },
                                breakMinutes = breakMinutes.toIntOrNull() ?: 0,
                                overtimeHours = overtimeHours.toDoubleOrNull() ?: 0.0,
                                isSunday = isSunday,
                                isHoliday = isHoliday,
                                isNightShift = isNightShift,
                                isLeave = isLeave,
                                isReport = isReport,
                                didNotWork = didNotWork,
                                notes = notes
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Text("Kaydet", fontWeight = FontWeight.Bold)
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Normal Gün hızlı doldur butonu
                NormalGunButton(
                    dailyHours = settings.dailyHours,
                    startTime = settings.defaultStartTime,
                    endTime = settings.defaultEndTime,
                    onClick = {
                        val (start, end) = viewModel.normalGunSaatleri()
                        startTime = start
                        endTime = end
                        breakMinutes = "60"
                        overtimeHours = "0.0"
                        isLeave = false
                        isReport = false
                        didNotWork = false
                        isHoliday = false
                    }
                )

                SectionTitle("Çalışma Saatleri")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TimeField("Giriş Saati", startTime, { startTime = it }, Modifier.weight(1f))
                    TimeField("Çıkış Saati", endTime, { endTime = it }, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NumberField("Mola (dk)", breakMinutes, { breakMinutes = it }, Modifier.weight(1f))
                    NumberField("Fazla Mesai (saat)", overtimeHours, { overtimeHours = it }, Modifier.weight(1f))
                }

                SectionTitle("Gün Türü")
                ChipRow(
                    items = listOf(
                        "Gece Vardiyası" to isNightShift,
                        "Pazar Mesaisi" to isSunday,
                        "Resmi Tatil" to isHoliday,
                        "İzinli" to isLeave,
                        "Raporlu" to isReport,
                        "Çalışmadı" to didNotWork,
                    ),
                    onToggle = { idx, v ->
                        when (idx) {
                            0 -> isNightShift = v
                            1 -> isSunday = v
                            2 -> isHoliday = v
                            3 -> { isLeave = v; if (v) { isReport = false; didNotWork = false } }
                            4 -> { isReport = v; if (v) { isLeave = false; didNotWork = false } }
                            5 -> { didNotWork = v; if (v) { isLeave = false; isReport = false } }
                        }
                    }
                )

                SectionTitle("Notlar")
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Not ekle...") },
                    minLines = 2
                )
            }
        }
    } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun NormalGunButton(dailyHours: Double, startTime: String, endTime: String, onClick: () -> Unit) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = WorkedGreen.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Normal Mesaisiz Gün",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = WorkedGreen
                )
                Text(
                    "$startTime → $endTime  •  $dailyHours saat  •  Mola: 60 dk",
                    style = MaterialTheme.typography.bodySmall,
                    color = WorkedGreen.copy(alpha = 0.8f)
                )
            }
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = WorkedGreen),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Uygula", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun TimeField(label: String, value: String, onChange: (String) -> Unit, modifier: Modifier) {
    OutlinedTextField(
        value = value, onValueChange = onChange, modifier = modifier, label = { Text(label) },
        placeholder = { Text("08:00") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true
    )
}

@Composable
private fun NumberField(label: String, value: String, onChange: (String) -> Unit, modifier: Modifier) {
    OutlinedTextField(
        value = value, onValueChange = onChange, modifier = modifier, label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChipRow(items: List<Pair<String, Boolean>>, onToggle: (Int, Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(3).forEachIndexed { rowIdx, row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEachIndexed { colIdx, (label, selected) ->
                    val idx = rowIdx * 3 + colIdx
                    FilterChip(
                        selected = selected,
                        onClick = { onToggle(idx, !selected) },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }
    }
}
