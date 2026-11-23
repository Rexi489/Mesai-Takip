package com.mesaihesap.app.presentation.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mesaihesap.app.domain.model.MonthlySummary
import com.mesaihesap.app.domain.model.WorkDay
import com.mesaihesap.app.presentation.home.formatCurrency
import com.mesaihesap.app.presentation.theme.WorkedGreen
import java.time.format.DateTimeFormatter
import java.util.Locale

import androidx.compose.ui.platform.LocalContext

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = hiltViewModel()) {
    val summaries by viewModel.summaries.collectAsState()
    val selectedDays by viewModel.selectedMonthDays.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            "Geçmiş Kayıtlar",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        if (summaries.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.History, null, modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
                    Spacer(Modifier.height(8.dp))
                    Text("Henüz kayıt yok", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                }
            }
            return
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(summaries, key = { it.yearMonth }) { summary ->
                val expanded = viewModel.isSelected(summary.yearMonth)
                val monthDays = if (expanded) selectedDays else emptyList()
                MonthHistoryCard(
                    summary = summary,
                    expanded = expanded,
                    days = monthDays,
                    onClick = {
                        viewModel.selectMonth(if (expanded) null else summary.yearMonth)
                    },
                    onExportPdf = {
                        viewModel.exportPdf(context, summary)
                    }
                )
            }
        }
    }
}

@Composable
private fun MonthHistoryCard(
    summary: MonthlySummary,
    expanded: Boolean,
    days: List<WorkDay>,
    onClick: () -> Unit,
    onExportPdf: () -> Unit
) {
    val monthLabel = try {
        val parts = summary.yearMonth.split("-")
        val ym = java.time.YearMonth.of(parts[0].toInt(), parts[1].toInt())
        ym.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("tr"))).replaceFirstChar { it.uppercase() }
    } catch (e: Exception) { summary.yearMonth }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(monthLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        InfoChip("${summary.workedDays} gün")
                        InfoChip("${String.format("%.0f", summary.totalHours)} saat")
                        if (summary.overtimeHours > 0) InfoChip("${String.format("%.1f", summary.overtimeHours)}h mesai")
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            formatCurrency(summary.netSalary),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = WorkedGreen
                        )
                        Icon(
                            if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                    IconButton(onClick = onExportPdf) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF Raporu", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    HorizontalDivider()
                    days.forEach { day ->
                        DayHistoryRow(day)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(text, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun DayHistoryRow(day: WorkDay) {
    val dateLabel = day.date.format(DateTimeFormatter.ofPattern("d MMM, EEE", Locale("tr")))
    val statusLabel = when {
        day.isLeave -> "İzinli"
        day.isReport -> "Raporlu"
        day.didNotWork -> "Çalışmadı"
        day.isWorked -> "${String.format("%.1f", day.workedHours)}s" + if (day.overtimeHours > 0) " + ${day.overtimeHours}h mesai" else ""
        else -> "-"
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(dateLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Text(statusLabel, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}
