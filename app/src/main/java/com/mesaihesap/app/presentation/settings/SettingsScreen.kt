package com.mesaihesap.app.presentation.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(
    onSalarySettingsClick: () -> Unit,
    viewModel: SalarySettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current
    var showDevDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.importBackupFromUri(context, it) { success, message ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    if (showDevDialog) {
        GeliştirmeDialog(onDismiss = { showDevDialog = false })
    }
    if (showAboutDialog) {
        HakkındaDialog(onDismiss = { showAboutDialog = false })
    }
    if (showThemeDialog) {
        TemaSeçimDialog(
            currentTheme = settings.themeMode,
            onSelect = { mode ->
                viewModel.save(settings.copy(themeMode = mode))
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }
    if (showBackupDialog) {
        YedeklemeDialog(
            onExport = {
                viewModel.exportBackup(context)
                showBackupDialog = false
            },
            onImport = {
                filePickerLauncher.launch("*/*")
                showBackupDialog = false
            },
            onDismiss = { showBackupDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Ayarlar", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(settings.workplaceName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Maaş: ${String.format("%,.0f", settings.monthlySalary)} ₺  •  Saatlik: ${String.format("%.2f", settings.hourlyRate)} ₺",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        SettingsGroupTitle("Maaş Ayarları")
        SettingsItem(
            icon = Icons.Default.AttachMoney,
            title = "Maaş ve Ücret Ayarları",
            subtitle = "Maaş, mesai katsayıları, ek ödemeler",
            onClick = onSalarySettingsClick
        )

        SettingsGroupTitle("Uygulama")
        val themeSubtitle = when (settings.themeMode) {
            "LIGHT" -> "Açık Tema"
            "DARK" -> "Karanlık Tema"
            else -> "Sistem Varsayılanı"
        }
        SettingsItem(
            icon = Icons.Default.Palette,
            title = "Uygulama Teması",
            subtitle = themeSubtitle,
            onClick = { showThemeDialog = true }
        )
        SettingsItem(
            icon = Icons.Default.Notifications,
            title = "Bildirimler",
            subtitle = "Mesai hatırlatıcıları ve maaş bildirimleri",
            inDevelopment = true,
            onClick = { showDevDialog = true }
        )
        SettingsItem(
            icon = Icons.Default.CloudUpload,
            title = "Yedekleme & Geri Yükleme",
            subtitle = "JSON formatında dışa / içe aktar",
            inDevelopment = false,
            onClick = { showBackupDialog = true }
        )
        SettingsItem(
            icon = Icons.Default.PictureAsPdf,
            title = "PDF Raporu",
            subtitle = "Bu ayın maaş ve mesai raporunu oluştur",
            inDevelopment = false,
            onClick = { viewModel.exportCurrentMonthPdf(context) }
        )

        SettingsGroupTitle("Güvenlik")
        SettingsItem(
            icon = Icons.Default.Fingerprint,
            title = "Biyometrik Giriş",
            subtitle = "Parmak izi veya yüz tanıma",
            inDevelopment = true,
            onClick = { showDevDialog = true }
        )
        SettingsItem(
            icon = Icons.Default.Lock,
            title = "PIN Koruması",
            subtitle = "Uygulama için PIN belirle",
            inDevelopment = true,
            onClick = { showDevDialog = true }
        )

        SettingsGroupTitle("Hızlı Maaş Hesaplama")
        SalaryCalculatorCard(
            savedMonthlySalary = settings.monthlySalary,
            savedWorkDays = settings.monthlyWorkDays,
            savedDailyHours = settings.dailyHours,
            savedOvertimeMultiplier = settings.overtimeMultiplier
        )

        SettingsGroupTitle("Hakkında")
        SettingsItem(
            icon = Icons.Default.Info,
            title = "Uygulama Hakkında",
            subtitle = "Sürüm 1.3.0",
            onClick = { showAboutDialog = true }
        )
    }
}

@Composable
private fun SalaryCalculatorCard(
    savedMonthlySalary: Double,
    savedWorkDays: Int,
    savedDailyHours: Double,
    savedOvertimeMultiplier: Double
) {
    var baseSalary by remember(savedMonthlySalary) {
        mutableStateOf(if (savedMonthlySalary > 0) savedMonthlySalary.toLong().toString() else "")
    }
    var workDays by remember(savedWorkDays) { mutableStateOf(savedWorkDays.toString()) }
    var dailyHours by remember(savedDailyHours) { mutableStateOf(savedDailyHours.toLong().toString()) }
    var overtimeHours by remember { mutableStateOf("") }

    val base = baseSalary.replace(",", ".").toDoubleOrNull() ?: 0.0
    val days = workDays.replace(",", ".").toIntOrNull()?.takeIf { it > 0 } ?: 26
    val hours = dailyHours.replace(",", ".").toDoubleOrNull()?.takeIf { it > 0 } ?: 7.5
    val otHours = overtimeHours.replace(",", ".").toDoubleOrNull() ?: 0.0

    val hourlyRate = if (base > 0) base / (days * hours) else 0.0
    val overtimePay = hourlyRate * savedOvertimeMultiplier * otHours
    val totalPay = base + overtimePay

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Calculate, null, tint = MaterialTheme.colorScheme.primary)
                Text("Maaş Hesaplama", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = baseSalary,
                    onValueChange = { baseSalary = it },
                    label = { Text("Ana Maaş (₺)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = overtimeHours,
                    onValueChange = { overtimeHours = it },
                    label = { Text("Mesai (saat)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = workDays,
                    onValueChange = { workDays = it },
                    label = { Text("Çalışma Günü") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = dailyHours,
                    onValueChange = { dailyHours = it },
                    label = { Text("Günlük Saat") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            if (base > 0) {
                HorizontalDivider()
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Saatlik ücret:", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                    Text("${String.format("%,.2f", hourlyRate)} ₺/saat",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                }
                if (otHours > 0) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Fazla mesai (×${savedOvertimeMultiplier}):", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("+${String.format("%,.2f", overtimePay)} ₺",
                            style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
                HorizontalDivider()
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Toplam:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("${String.format("%,.2f", totalPay)} ₺",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun YedeklemeDialog(
    onExport: () -> Unit,
    onImport: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.CloudUpload, null, tint = MaterialTheme.colorScheme.primary) },
        title = { Text("Yedekleme & Geri Yükleme", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Verilerinizi JSON formatında yedekleyebilir veya daha önceden aldığınız yedeği geri yükleyebilirsiniz.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Button(
                    onClick = onExport,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Verileri Yedekle (JSON Dışa Aktar)", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onImport,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Download, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Yedekten Geri Yükle (İçe Aktar)", fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Kapat") }
        }
    )
}

@Composable
private fun GeliştirmeDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Construction, null, tint = MaterialTheme.colorScheme.primary) },
        title = {
            Text("Geliştirme Aşamasında", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        },
        text = {
            Text(
                "Bu özellik henüz geliştirme aşamasındadır.\nYakında kullanıma sunulacaktır.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tamam", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun TemaSeçimDialog(
    currentTheme: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(
        "SYSTEM" to "Sistem Varsayılanı",
        "LIGHT" to "Açık Tema",
        "DARK" to "Karanlık Tema"
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Palette, null, tint = MaterialTheme.colorScheme.primary) },
        title = { Text("Tema Seçimi", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                options.forEach { (mode, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(mode) }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (currentTheme == mode),
                            onClick = { onSelect(mode) }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(label, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("İptal") }
        }
    )
}

@Composable
private fun HakkındaDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary) },
        title = {
            Text("Uygulama Hakkında", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Mesai Hesap", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Sürüm 1.3.0", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Text("Geliştirici", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                Text(
                    "Emrah Karaduman",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    "kişisel mesai ve maaş takip uygulaması.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Kapat", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun SettingsGroupTitle(text: String) {
    Text(
        text, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    inDevelopment: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (inDevelopment)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon, null,
                tint = if (inDevelopment)
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                else
                    MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (inDevelopment)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    if (inDevelopment) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                "Yakında",
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
            Icon(
                Icons.Default.ChevronRight, null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
        }
    }
}
