package com.mesaihesap.app.presentation.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mesaihesap.app.domain.model.MonthlySummary
import com.mesaihesap.app.presentation.theme.*
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

import androidx.compose.ui.platform.LocalContext

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val summary by viewModel.summary.collectAsState()
    val currentYearMonth by viewModel.currentYearMonth.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        HeaderCard(
            yearMonth = currentYearMonth,
            summary = summary,
            daysInMonth = viewModel.daysInMonth,
            onPrevious = { viewModel.previousMonth() },
            onNext = { viewModel.nextMonth() },
            onExportPdf = { viewModel.exportPdf(context) }
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Genel Bakış",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))

        Row(Modifier.padding(horizontal = 16.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Çalışılan Gün",
                value = "${summary.workedDays}",
                unit = "gün",
                icon = Icons.Default.CalendarToday,
                color = WorkedGreen
            )
            Spacer(Modifier.width(8.dp))
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Toplam Saat",
                value = String.format("%.1f", summary.totalHours),
                unit = "saat",
                icon = Icons.Default.AccessTime,
                color = PrimaryBlue
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(Modifier.padding(horizontal = 16.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Fazla Mesai",
                value = String.format("%.1f", summary.overtimeHours),
                unit = "saat",
                icon = Icons.Default.Timer,
                color = OvertimeOrange
            )
            Spacer(Modifier.width(8.dp))
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Pazar Mesaisi",
                value = String.format("%.1f", summary.sundayHours),
                unit = "saat",
                icon = Icons.Default.Weekend,
                color = HolidayRed
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(Modifier.padding(horizontal = 16.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Resmi Tatil",
                value = String.format("%.1f", summary.holidayHours),
                unit = "saat",
                icon = Icons.Default.Event,
                color = OfficialHolidayBlue
            )
            Spacer(Modifier.width(8.dp))
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Gece Vardiyası",
                value = "${summary.nightShiftDays}",
                unit = "gün",
                icon = Icons.Default.Nightlight,
                color = NightShiftPurple
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Maaş Detayı",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
        SalaryBreakdownCard(summary = summary)

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun HeaderCard(
    yearMonth: YearMonth,
    summary: MonthlySummary,
    daysInMonth: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onExportPdf: () -> Unit
) {
    val monthName = yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("tr")))
        .replaceFirstChar { it.uppercase() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(Brush.linearGradient(listOf(GradientStart, GradientEnd)))
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onPrevious) {
                        Icon(Icons.Default.ChevronLeft, null, tint = Color.White)
                    }
                    Text(
                        text = monthName,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onNext) {
                        Icon(Icons.Default.ChevronRight, null, tint = Color.White)
                    }
                }
                IconButton(onClick = onExportPdf) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF Raporu Al", tint = Color.White)
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Tahmini Maaş",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = formatCurrency(summary.netSalary),
                color = Color.White,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            val progress = if (daysInMonth > 0) summary.workedDays.toFloat() / daysInMonth else 0f
            val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Aylık İlerleme", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                Text("${summary.workedDays}/$daysInMonth gün", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    unit: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(4.dp))
                Text(unit, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.padding(bottom = 3.dp))
            }
        }
    }
}

@Composable
private fun SalaryBreakdownCard(summary: MonthlySummary) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            SalaryRow("Temel Maaş", summary.baseSalary, color = MaterialTheme.colorScheme.onSurface)
            if (summary.overtimePay > 0) SalaryRow("Fazla Mesai Ücreti", summary.overtimePay, color = OvertimeOrange)
            if (summary.sundayPay > 0) SalaryRow("Pazar Mesaisi", summary.sundayPay, color = HolidayRed)
            if (summary.holidayPay > 0) SalaryRow("Resmi Tatil", summary.holidayPay, color = OfficialHolidayBlue)
            if (summary.nightShiftPay > 0) SalaryRow("Gece Primi", summary.nightShiftPay, color = NightShiftPurple)
            if (summary.mealAllowance > 0) SalaryRow("Yemek Ücreti", summary.mealAllowance, color = WorkedGreen)
            if (summary.transportAllowance > 0) SalaryRow("Yol Ücreti", summary.transportAllowance, color = WorkedGreen)
            if (summary.bonus > 0) SalaryRow("Prim", summary.bonus, color = WorkedGreen)
            if (summary.advance > 0 || summary.deductions > 0) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
            if (summary.advance > 0) SalaryRow("Avans", -summary.advance, color = HolidayRed)
            if (summary.deductions > 0) SalaryRow("Kesintiler", -summary.deductions, color = HolidayRed)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("NET MAAŞ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(formatCurrency(summary.netSalary), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = WorkedGreen)
            }
        }
    }
}

@Composable
private fun SalaryRow(label: String, amount: Double, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
        Text(
            text = if (amount < 0) "-${formatCurrency(-amount)}" else formatCurrency(amount),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

fun formatCurrency(amount: Double): String =
    "%,.2f ₺".format(amount).replace(",", ".")
