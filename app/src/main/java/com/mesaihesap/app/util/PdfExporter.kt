package com.mesaihesap.app.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.mesaihesap.app.domain.model.MonthlySummary
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object PdfExporter {

    fun export(context: Context, summary: MonthlySummary): File? {
        return try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)
            drawContent(page.canvas, summary)
            document.finishPage(page)

            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val file = File(dir, "MesaiRaporu_${summary.yearMonth}.pdf")
            document.writeTo(FileOutputStream(file))
            document.close()
            file
        } catch (e: Exception) { null }
    }

    private fun drawContent(canvas: Canvas, summary: MonthlySummary) {
        val titlePaint = Paint().apply { textSize = 24f; isFakeBoldText = true }
        val bodyPaint = Paint().apply { textSize = 14f }
        val subPaint = Paint().apply { textSize = 12f; color = 0xFF666666.toInt() }

        val monthLabel = try {
            val parts = summary.yearMonth.split("-")
            val ym = java.time.YearMonth.of(parts[0].toInt(), parts[1].toInt())
            ym.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("tr"))).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) { summary.yearMonth }

        var y = 80f
        canvas.drawText("MESAİ HESAP RAPORU", 40f, y, titlePaint); y += 40f
        canvas.drawText(monthLabel, 40f, y, bodyPaint); y += 30f
        canvas.drawLine(40f, y, 555f, y, subPaint); y += 20f

        fun row(label: String, value: String) {
            canvas.drawText(label, 40f, y, bodyPaint)
            canvas.drawText(value, 350f, y, bodyPaint)
            y += 24f
        }

        row("Çalışılan Gün:", "${summary.workedDays} gün")
        row("Toplam Çalışma:", "${String.format("%.1f", summary.totalHours)} saat")
        row("Fazla Mesai:", "${String.format("%.1f", summary.overtimeHours)} saat")
        row("Pazar Mesaisi:", "${String.format("%.1f", summary.sundayHours)} saat")
        row("Resmi Tatil:", "${String.format("%.1f", summary.holidayHours)} saat")
        row("Gece Vardiyası:", "${summary.nightShiftDays} gün")
        y += 10f
        canvas.drawLine(40f, y, 555f, y, subPaint); y += 20f
        row("Temel Maaş:", "${String.format("%,.2f", summary.baseSalary)} ₺")
        if (summary.overtimePay > 0) row("Fazla Mesai Ücreti:", "${String.format("%,.2f", summary.overtimePay)} ₺")
        if (summary.sundayPay > 0) row("Pazar Ücreti:", "${String.format("%,.2f", summary.sundayPay)} ₺")
        if (summary.holidayPay > 0) row("Resmi Tatil Ücreti:", "${String.format("%,.2f", summary.holidayPay)} ₺")
        if (summary.mealAllowance > 0) row("Yemek Ücreti:", "${String.format("%,.2f", summary.mealAllowance)} ₺")
        if (summary.transportAllowance > 0) row("Yol Ücreti:", "${String.format("%,.2f", summary.transportAllowance)} ₺")
        if (summary.bonus > 0) row("Prim:", "${String.format("%,.2f", summary.bonus)} ₺")
        if (summary.advance > 0) row("Avans:", "-${String.format("%,.2f", summary.advance)} ₺")
        if (summary.deductions > 0) row("Kesintiler:", "-${String.format("%,.2f", summary.deductions)} ₺")
        y += 10f
        canvas.drawLine(40f, y, 555f, y, titlePaint); y += 24f
        val netPaint = Paint().apply { textSize = 18f; isFakeBoldText = true; color = 0xFF2E7D32.toInt() }
        canvas.drawText("NET MAAŞ:", 40f, y, netPaint)
        canvas.drawText("${String.format("%,.2f", summary.netSalary)} ₺", 350f, y, netPaint)
    }
}
