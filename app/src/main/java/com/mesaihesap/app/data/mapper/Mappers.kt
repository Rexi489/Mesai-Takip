package com.mesaihesap.app.data.mapper

import com.mesaihesap.app.data.local.entity.SalarySettingsEntity
import com.mesaihesap.app.data.local.entity.WorkDayEntity
import com.mesaihesap.app.domain.model.SalarySettings
import com.mesaihesap.app.domain.model.WorkDay
import java.time.LocalDate

fun WorkDayEntity.toDomain() = WorkDay(
    id = id,
    date = LocalDate.parse(date),
    startTime = startTime,
    endTime = endTime,
    breakMinutes = breakMinutes,
    overtimeHours = overtimeHours,
    isSunday = isSunday,
    isHoliday = isHoliday,
    isNightShift = isNightShift,
    isLeave = isLeave,
    isReport = isReport,
    didNotWork = didNotWork,
    notes = notes,
    workplaceId = workplaceId
)

fun WorkDay.toEntity() = WorkDayEntity(
    id = id,
    date = date.toString(),
    startTime = startTime,
    endTime = endTime,
    breakMinutes = breakMinutes,
    overtimeHours = overtimeHours,
    isSunday = isSunday,
    isHoliday = isHoliday,
    isNightShift = isNightShift,
    isLeave = isLeave,
    isReport = isReport,
    didNotWork = didNotWork,
    notes = notes,
    workplaceId = workplaceId
)

fun SalarySettingsEntity.toDomain() = SalarySettings(
    id = id, monthlySalary = monthlySalary, dailyHours = dailyHours,
    monthlyWorkDays = monthlyWorkDays, useAutoHourlyRate = useAutoHourlyRate,
    manualHourlyRate = manualHourlyRate, overtimeMultiplier = overtimeMultiplier,
    sundayMultiplier = sundayMultiplier, holidayMultiplier = holidayMultiplier,
    nightShiftBonus = nightShiftBonus, mealAllowance = mealAllowance,
    transportAllowance = transportAllowance, bonus = bonus, advance = advance,
    deductions = deductions, salaryDayOfMonth = salaryDayOfMonth,
    currency = currency, workplaceName = workplaceName,
    defaultStartTime = defaultStartTime, defaultEndTime = defaultEndTime,
    themeMode = themeMode
)

fun SalarySettings.toEntity() = SalarySettingsEntity(
    id = id, monthlySalary = monthlySalary, dailyHours = dailyHours,
    monthlyWorkDays = monthlyWorkDays, useAutoHourlyRate = useAutoHourlyRate,
    manualHourlyRate = manualHourlyRate, overtimeMultiplier = overtimeMultiplier,
    sundayMultiplier = sundayMultiplier, holidayMultiplier = holidayMultiplier,
    nightShiftBonus = nightShiftBonus, mealAllowance = mealAllowance,
    transportAllowance = transportAllowance, bonus = bonus, advance = advance,
    deductions = deductions, salaryDayOfMonth = salaryDayOfMonth,
    currency = currency, workplaceName = workplaceName,
    defaultStartTime = defaultStartTime, defaultEndTime = defaultEndTime,
    themeMode = themeMode
)
