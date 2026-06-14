package com.mesaihesap.app.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mesaihesap.app.domain.model.WorkDay
import com.mesaihesap.app.presentation.theme.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarScreen(
    onDayClick: (String) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val yearMonth by viewModel.currentYearMonth.collectAsState()
    val workDays by viewModel.workDays.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        val monthTitle = yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("tr")))
            .replaceFirstChar { it.uppercase() }
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.DeleteSweep, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Ayı Temizle", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) },
            text = {
                Text(
                    "$monthTitle ayına ait tüm kayıtlar silinecek. Bu işlem geri alınamaz.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteCurrentMonth()
                    showDeleteDialog = false
                }) { Text("Sil", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("İptal") }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        CalendarHeader(yearMonth, viewModel::previousMonth, viewModel::nextMonth, onDeleteMonth = { showDeleteDialog = true })
        WeekDayLabels()
        CalendarGrid(yearMonth, workDays, onDayClick)
        Spacer(Modifier.height(16.dp))
        CalendarLegend()
    }
}

@Composable
private fun CalendarHeader(yearMonth: YearMonth, onPrev: () -> Unit, onNext: () -> Unit, onDeleteMonth: () -> Unit) {
    val title = yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("tr")))
        .replaceFirstChar { it.uppercase() }
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrev) { Icon(Icons.Default.ChevronLeft, null) }
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        IconButton(onClick = onNext) { Icon(Icons.Default.ChevronRight, null) }
        IconButton(onClick = onDeleteMonth) {
            Icon(Icons.Default.DeleteSweep, contentDescription = "Ayı Temizle", tint = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun WeekDayLabels() {
    val days = listOf("Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz")
    Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        days.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = if (day == "Paz") HolidayRed else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium
            )
        }
    }
    Spacer(Modifier.height(4.dp))
}

@Composable
private fun CalendarGrid(
    yearMonth: YearMonth,
    workDays: Map<String, WorkDay>,
    onDayClick: (String) -> Unit
) {
    val firstDay = yearMonth.atDay(1)
    val startOffset = (firstDay.dayOfWeek.value - 1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val today = LocalDate.now()

    val cells = startOffset + daysInMonth
    val rows = (cells + 6) / 7

    Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        for (row in 0 until rows) {
            Row(Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val day = cellIndex - startOffset + 1
                    val modifier = Modifier.weight(1f).aspectRatio(1f).padding(2.dp)
                    if (day in 1..daysInMonth) {
                        val date = yearMonth.atDay(day)
                        val dateStr = date.toString()
                        val workDay = workDays[dateStr]
                        DayCell(
                            day = day,
                            date = date,
                            workDay = workDay,
                            isToday = date == today,
                            modifier = modifier,
                            onClick = { onDayClick(dateStr) }
                        )
                    } else {
                        Box(modifier) {}
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    date: LocalDate,
    workDay: WorkDay?,
    isToday: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val isSunday = date.dayOfWeek == DayOfWeek.SUNDAY
    val bgColor = when {
        workDay?.isHoliday == true -> OfficialHolidayBlue.copy(alpha = 0.2f)
        workDay?.isNightShift == true -> NightShiftPurple.copy(alpha = 0.2f)
        workDay?.isLeave == true || workDay?.isReport == true -> Color.Gray.copy(alpha = 0.15f)
        workDay?.isWorked == true && workDay.overtimeHours > 0 -> OvertimeOrange.copy(alpha = 0.2f)
        workDay?.isWorked == true && isSunday -> HolidayRed.copy(alpha = 0.2f)
        workDay?.isWorked == true -> WorkedGreen.copy(alpha = 0.15f)
        workDay?.didNotWork == true -> HolidayRed.copy(alpha = 0.1f)
        else -> Color.Transparent
    }
    val dotColor = when {
        workDay?.isHoliday == true -> OfficialHolidayBlue
        workDay?.isNightShift == true -> NightShiftPurple
        workDay?.overtimeHours ?: 0.0 > 0 -> OvertimeOrange
        workDay?.isWorked == true && isSunday -> HolidayRed
        workDay?.isWorked == true -> WorkedGreen
        else -> null
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .then(if (isToday) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp)) else Modifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$day",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (isSunday) HolidayRed else MaterialTheme.colorScheme.onBackground
            )
            if (dotColor != null) {
                Box(
                    modifier = Modifier.size(5.dp).clip(CircleShape).background(dotColor)
                )
            }
        }
    }
}

@Composable
private fun CalendarLegend() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Renk Açıklamaları", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LegendItem(WorkedGreen, "Çalışıldı")
                LegendItem(HolidayRed, "Tatil/Pazar")
                LegendItem(OfficialHolidayBlue, "Resmi Tatil")
            }
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LegendItem(OvertimeOrange, "Fazla Mesai")
                LegendItem(NightShiftPurple, "Gece Vardiyası")
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}
