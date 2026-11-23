package com.mesaihesap.app.domain.model

import java.time.LocalDate

data class WorkDay(
    val id: Long = 0,
    val date: LocalDate,
    val startTime: String? = null,
    val endTime: String? = null,
    val breakMinutes: Int = 0,
    val overtimeHours: Double = 0.0,
    val isSunday: Boolean = false,
    val isHoliday: Boolean = false,
    val isNightShift: Boolean = false,
    val isLeave: Boolean = false,
    val isReport: Boolean = false,
    val didNotWork: Boolean = false,
    val notes: String = "",
    val workplaceId: Long = 1
) {
    val workedHours: Double
        get() {
            if (startTime == null || endTime == null) return 0.0
            return try {
                val (sh, sm) = startTime.split(":").map { it.toInt() }
                val (eh, em) = endTime.split(":").map { it.toInt() }
                var totalMinutes = (eh * 60 + em) - (sh * 60 + sm)
                if (totalMinutes < 0) totalMinutes += 24 * 60
                totalMinutes -= breakMinutes
                if (totalMinutes > 0) totalMinutes / 60.0 else 0.0
            } catch (e: Exception) { 0.0 }
        }

    val isWorked: Boolean
        get() = !isLeave && !isReport && !didNotWork && startTime != null
}
