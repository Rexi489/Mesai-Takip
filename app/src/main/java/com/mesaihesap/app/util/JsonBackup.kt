package com.mesaihesap.app.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.mesaihesap.app.domain.model.SalarySettings
import com.mesaihesap.app.domain.model.WorkDay
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.LocalDate

data class BackupData(
    val settings: SalarySettings?,
    val workDays: List<WorkDay>
)

object JsonBackup {

    fun exportToJson(settings: SalarySettings, days: List<WorkDay>): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("exportDate", System.currentTimeMillis())

        val settingsObj = JSONObject().apply {
            put("monthlySalary", settings.monthlySalary)
            put("dailyHours", settings.dailyHours)
            put("monthlyWorkDays", settings.monthlyWorkDays)
            put("useAutoHourlyRate", settings.useAutoHourlyRate)
            put("manualHourlyRate", settings.manualHourlyRate)
            put("overtimeMultiplier", settings.overtimeMultiplier)
            put("sundayMultiplier", settings.sundayMultiplier)
            put("holidayMultiplier", settings.holidayMultiplier)
            put("nightShiftBonus", settings.nightShiftBonus)
            put("mealAllowance", settings.mealAllowance)
            put("transportAllowance", settings.transportAllowance)
            put("bonus", settings.bonus)
            put("advance", settings.advance)
            put("deductions", settings.deductions)
            put("salaryDayOfMonth", settings.salaryDayOfMonth)
            put("workplaceName", settings.workplaceName)
            put("defaultStartTime", settings.defaultStartTime)
            put("defaultEndTime", settings.defaultEndTime)
            put("themeMode", settings.themeMode)
        }
        root.put("settings", settingsObj)

        val daysArray = JSONArray()
        days.forEach { day ->
            daysArray.put(JSONObject().apply {
                put("date", day.date.toString())
                put("startTime", day.startTime ?: "")
                put("endTime", day.endTime ?: "")
                put("breakMinutes", day.breakMinutes)
                put("overtimeHours", day.overtimeHours)
                put("isSunday", day.isSunday)
                put("isHoliday", day.isHoliday)
                put("isNightShift", day.isNightShift)
                put("isLeave", day.isLeave)
                put("isReport", day.isReport)
                put("didNotWork", day.didNotWork)
                put("notes", day.notes)
            })
        }
        root.put("workDays", daysArray)

        return root.toString(2)
    }

    fun exportFile(context: Context, settings: SalarySettings, days: List<WorkDay>): File {
        val jsonStr = exportToJson(settings, days)
        val file = File(context.cacheDir, "mesai_backup_${System.currentTimeMillis()}.json")
        file.writeText(jsonStr)
        return file
    }

    fun shareBackupFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Yedek Dosyasını Kaydet / Paylaş"))
    }

    fun importFromJson(jsonStr: String): BackupData {
        val root = JSONObject(jsonStr)

        var settings: SalarySettings? = null
        if (root.has("settings")) {
            val s = root.getJSONObject("settings")
            settings = SalarySettings(
                monthlySalary = s.optDouble("monthlySalary", 0.0),
                dailyHours = s.optDouble("dailyHours", 7.5),
                monthlyWorkDays = s.optInt("monthlyWorkDays", 26),
                useAutoHourlyRate = s.optBoolean("useAutoHourlyRate", true),
                manualHourlyRate = s.optDouble("manualHourlyRate", 0.0),
                overtimeMultiplier = s.optDouble("overtimeMultiplier", 1.5),
                sundayMultiplier = s.optDouble("sundayMultiplier", 1.5),
                holidayMultiplier = s.optDouble("holidayMultiplier", 2.0),
                nightShiftBonus = s.optDouble("nightShiftBonus", 0.0),
                mealAllowance = s.optDouble("mealAllowance", 0.0),
                transportAllowance = s.optDouble("transportAllowance", 0.0),
                bonus = s.optDouble("bonus", 0.0),
                advance = s.optDouble("advance", 0.0),
                deductions = s.optDouble("deductions", 0.0),
                salaryDayOfMonth = s.optInt("salaryDayOfMonth", 15),
                workplaceName = s.optString("workplaceName", "İş Yerim"),
                defaultStartTime = s.optString("defaultStartTime", "08:30"),
                defaultEndTime = s.optString("defaultEndTime", "17:00"),
                themeMode = s.optString("themeMode", "SYSTEM")
            )
        }

        val workDays = mutableListOf<WorkDay>()
        if (root.has("workDays")) {
            val arr = root.getJSONArray("workDays")
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val dateStr = obj.optString("date")
                if (dateStr.isNotBlank()) {
                    val date = LocalDate.parse(dateStr)
                    val st = obj.optString("startTime").ifBlank { null }
                    val et = obj.optString("endTime").ifBlank { null }
                    workDays.add(
                        WorkDay(
                            date = date,
                            startTime = st,
                            endTime = et,
                            breakMinutes = obj.optInt("breakMinutes", 0),
                            overtimeHours = obj.optDouble("overtimeHours", 0.0),
                            isSunday = obj.optBoolean("isSunday", false),
                            isHoliday = obj.optBoolean("isHoliday", false),
                            isNightShift = obj.optBoolean("isNightShift", false),
                            isLeave = obj.optBoolean("isLeave", false),
                            isReport = obj.optBoolean("isReport", false),
                            didNotWork = obj.optBoolean("didNotWork", false),
                            notes = obj.optString("notes", "")
                        )
                    )
                }
            }
        }

        return BackupData(settings, workDays)
    }
}
