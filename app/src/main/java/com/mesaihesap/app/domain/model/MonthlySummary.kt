package com.mesaihesap.app.domain.model

data class MonthlySummary(
    val yearMonth: String,          // 2026-06
    val workedDays: Int = 0,
    val totalHours: Double = 0.0,
    val overtimeHours: Double = 0.0,
    val sundayHours: Double = 0.0,
    val holidayHours: Double = 0.0,
    val nightShiftDays: Int = 0,
    val leaveDays: Int = 0,
    val reportDays: Int = 0,
    val baseSalary: Double = 0.0,
    val overtimePay: Double = 0.0,
    val sundayPay: Double = 0.0,
    val holidayPay: Double = 0.0,
    val nightShiftPay: Double = 0.0,
    val mealAllowance: Double = 0.0,
    val transportAllowance: Double = 0.0,
    val bonus: Double = 0.0,
    val advance: Double = 0.0,
    val deductions: Double = 0.0
) {
    val totalEarnings: Double
        get() = baseSalary + overtimePay + sundayPay + holidayPay +
                nightShiftPay + mealAllowance + transportAllowance + bonus

    val netSalary: Double
        get() = totalEarnings - advance - deductions
}
