package com.mesaihesap.app.domain.model

data class SalarySettings(
    val id: Long = 1,
    val monthlySalary: Double = 0.0,
    val dailyHours: Double = 7.5,
    val monthlyWorkDays: Int = 26,
    val useAutoHourlyRate: Boolean = true,
    val manualHourlyRate: Double = 0.0,
    val overtimeMultiplier: Double = 1.5,
    val sundayMultiplier: Double = 1.5,
    val holidayMultiplier: Double = 2.0,
    val nightShiftBonus: Double = 0.0,
    val mealAllowance: Double = 0.0,
    val transportAllowance: Double = 0.0,
    val bonus: Double = 0.0,
    val advance: Double = 0.0,
    val deductions: Double = 0.0,
    val salaryDayOfMonth: Int = 15,
    val currency: String = "TRY",
    val workplaceName: String = "İş Yerim",
    val defaultStartTime: String = "08:30",
    val defaultEndTime: String = "17:00",
    val themeMode: String = "SYSTEM"
) {
    val hourlyRate: Double
        get() = if (useAutoHourlyRate) {
            if (monthlyWorkDays > 0 && dailyHours > 0)
                monthlySalary / (monthlyWorkDays * dailyHours)
            else 0.0
        } else manualHourlyRate
}
