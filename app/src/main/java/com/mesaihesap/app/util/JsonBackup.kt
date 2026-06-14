package com.mesaihesap.app.util

import android.content.Context
import com.mesaihesap.app.domain.model.WorkDay
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object JsonBackup {

    fun exportWorkDays(context: Context, days: List<WorkDay>): File {
        val array = JSONArray()
        days.forEach { day ->
            array.put(JSONObject().apply {
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
        val file = File(context.filesDir, "mesai_backup_${System.currentTimeMillis()}.json")
        file.writeText(array.toString(2))
        return file
    }
}
