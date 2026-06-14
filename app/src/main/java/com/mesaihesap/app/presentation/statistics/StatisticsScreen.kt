package com.mesaihesap.app.presentation.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mesaihesap.app.domain.model.MonthlySummary
import com.mesaihesap.app.presentation.home.formatCurrency
import com.mesaihesap.app.presentation.theme.*

@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel = hiltViewModel()) {
    val weeklyData by viewModel.weeklyData.collectAsState()
    val summaries by viewModel.last12MonthsSummaries.collectAsState()
    val avgDailyHours by viewModel.averageDailyHours.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("İstatistikler", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        // Quick stats
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickStatCard(
                modifier = Modifier.weight(1f),
                label = "Ort. Günlük",
                value = String.format("%.1f saat", avgDailyHours),
                color = PrimaryBlue
            )
            val totalMonths = summaries.count { it.workedDays > 0 }
            QuickStatCard(
                modifier = Modifier.weight(1f),
                label = "Toplam Ay",
                value = "$totalMonths ay",
                color = WorkedGreen
            )
        }

        // Weekly bar chart
        SectionCard(title = "Bu Hafta Çalışma Saatleri") {
            WeeklyBarChart(weeklyData)
        }

        // Last 12 months salary
        SectionCard(title = "Son 12 Ay Maaş") {
            MonthlySalaryChart(summaries)
        }

        // Overtime breakdown
        if (summaries.isNotEmpty()) {
            val current = summaries.lastOrNull()
            current?.let { OvertimeBreakdownCard(it) }
        }
    }
}

@Composable
private fun QuickStatCard(modifier: Modifier, label: String, value: String, color: Color) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun WeeklyBarChart(data: List<WeeklyData>) {
    if (data.isEmpty()) {
        Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
            Text("Veri yok", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
        return
    }
    val maxHours = data.maxOf { it.hours }.coerceAtLeast(8.0)
    Row(
        modifier = Modifier.fillMaxWidth().height(120.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { item ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val barHeight = if (maxHours > 0) (item.hours / maxHours * 90).dp else 0.dp
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(barHeight.coerceAtLeast(4.dp))
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(if (item.hours > 0) PrimaryBlue else Color.Gray.copy(alpha = 0.3f))
                )
                Spacer(Modifier.height(4.dp))
                Text(item.label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
private fun MonthlySalaryChart(summaries: List<MonthlySummary>) {
    if (summaries.isEmpty()) {
        Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
            Text("Veri yok", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
        return
    }
    val maxSalary = summaries.maxOf { it.netSalary }.coerceAtLeast(1.0)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        summaries.takeLast(6).forEach { s ->
            val label = s.yearMonth.takeLast(5).let { ym ->
                val parts = ym.split("-")
                val months = listOf("", "Oca", "Şub", "Mar", "Nis", "May", "Haz", "Tem", "Ağu", "Eyl", "Eki", "Kas", "Ara")
                "${months.getOrNull(parts.lastOrNull()?.toIntOrNull() ?: 0) ?: ""} ${parts.firstOrNull() ?: ""}"
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(label, modifier = Modifier.width(64.dp), style = MaterialTheme.typography.labelSmall)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    val fraction = (s.netSalary / maxSalary).toFloat().coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction)
                            .background(WorkedGreen)
                    )
                }
                Text(
                    text = formatCurrency(s.netSalary),
                    modifier = Modifier.width(80.dp).padding(start = 8.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun OvertimeBreakdownCard(summary: MonthlySummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Bu Ay Mesai Dağılımı", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            OvertimeRow("Fazla Mesai", summary.overtimeHours, OvertimeOrange)
            OvertimeRow("Pazar Mesaisi", summary.sundayHours, HolidayRed)
            OvertimeRow("Resmi Tatil", summary.holidayHours, OfficialHolidayBlue)
            OvertimeRow("Gece Vardiyası", summary.nightShiftDays.toDouble(), NightShiftPurple, unit = "gün")
        }
    }
}

@Composable
private fun OvertimeRow(label: String, value: Double, color: Color, unit: String = "saat") {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(color))
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
        Text("${String.format("%.1f", value)} $unit", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = color)
    }
}
