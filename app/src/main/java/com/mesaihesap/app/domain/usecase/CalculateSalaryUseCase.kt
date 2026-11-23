package com.mesaihesap.app.domain.usecase

import com.mesaihesap.app.domain.model.MonthlySummary
import com.mesaihesap.app.domain.model.SalarySettings
import com.mesaihesap.app.domain.model.WorkDay
import javax.inject.Inject

class CalculateSalaryUseCase @Inject constructor() {

    operator fun invoke(
        workDays: List<WorkDay>,
        settings: SalarySettings,
        targetYearMonth: String? = null
    ): MonthlySummary {
        val workedDays = workDays.count { it.isWorked }
        val totalHours = workDays.sumOf { it.workedHours }
        val overtimeHours = workDays.sumOf { it.overtimeHours }
        val sundayHours = workDays.filter { it.isSunday && it.isWorked }.sumOf { it.workedHours }
        val holidayHours = workDays.filter { it.isHoliday && it.isWorked }.sumOf { it.workedHours }
        val nightShiftDays = workDays.count { it.isNightShift && it.isWorked }
        val leaveDays = workDays.count { it.isLeave }
        val reportDays = workDays.count { it.isReport }

        val hr = settings.hourlyRate

        val overtimePay = hr * overtimeHours * settings.overtimeMultiplier
        val sundayPay = hr * sundayHours * settings.sundayMultiplier
        val holidayPay = hr * holidayHours * settings.holidayMultiplier
        val nightShiftPay = settings.nightShiftBonus * nightShiftDays

        val yearMonth = targetYearMonth ?: workDays.firstOrNull()?.date?.let {
            "${it.year}-${it.monthValue.toString().padStart(2, '0')}"
        } ?: ""

        return MonthlySummary(
            yearMonth = yearMonth,
            workedDays = workedDays,
            totalHours = totalHours,
            overtimeHours = overtimeHours,
            sundayHours = sundayHours,
            holidayHours = holidayHours,
            nightShiftDays = nightShiftDays,
            leaveDays = leaveDays,
            reportDays = reportDays,
            baseSalary = settings.monthlySalary,
            overtimePay = overtimePay,
            sundayPay = sundayPay,
            holidayPay = holidayPay,
            nightShiftPay = nightShiftPay,
            mealAllowance = settings.mealAllowance,
            transportAllowance = settings.transportAllowance,
            bonus = settings.bonus,
            advance = settings.advance,
            deductions = settings.deductions
        )
    }
}
