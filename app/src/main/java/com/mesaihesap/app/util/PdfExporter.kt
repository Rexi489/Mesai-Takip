package com.mesaihesap.app.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.core.content.FileProvider
import com.mesaihesap.app.domain.model.MonthlySummary
import com.mesaihesap.app.domain.model.SalarySettings
import com.mesaihesap.app.domain.model.WorkDay
import java.io.File
import java.io.FileOutputStream
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

object PdfExporter {

    fun export(
        context: Context,
        summary: MonthlySummary,
        settings: SalarySettings? = null,
        workDays: List<WorkDay> = emptyList()
    ): File? {
        return try {
            val document = PdfDocument()
            val pageWidth = 595
            val pageHeight = 842

            var pageNumber = 1
            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            var page = document.startPage(pageInfo)
            var canvas = page.canvas

            val ym = try {
                val parts = summary.yearMonth.split("-")
                YearMonth.of(parts[0].toInt(), parts[1].toInt())
            } catch (e: Exception) {
                YearMonth.now()
            }

            val monthLabel = ym.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("tr"))).replaceFirstChar { it.uppercase() }

            val titlePaint = Paint().apply { textSize = 18f; isFakeBoldText = true; color = 0xFF1565C0.toInt() }
            val headerPaint = Paint().apply { textSize = 13f; isFakeBoldText = true; color = 0xFF212121.toInt() }
            val subPaint = Paint().apply { textSize = 9.5f; color = 0xFF616161.toInt() }
            val bodyPaint = Paint().apply { textSize = 9.5f; color = 0xFF212121.toInt() }
            val boldBodyPaint = Paint().apply { textSize = 9.5f; isFakeBoldText = true; color = 0xFF212121.toInt() }
            val linePaint = Paint().apply { strokeWidth = 0.8f; color = 0xFFE0E0E0.toInt() }
            val rectPaint = Paint().apply { color = 0xFFF5F7FA.toInt() }

            var y = 35f

            // Top Banner Card
            val bannerRect = RectF(30f, y, 565f, y + 55f)
            canvas.drawRoundRect(bannerRect, 8f, 8f, rectPaint)
            
            canvas.drawText("MESAİ HESAP - AYLIK MESAİ VE MAAŞ RAPORU", 42f, y + 22f, titlePaint)
            val workplace = settings?.workplaceName?.ifBlank { "İş Yerim" } ?: "İş Yerim"
            canvas.drawText("$workplace  •  Rapor Dönemi: $monthLabel", 42f, y + 42f, subPaint)
            
            val todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale("tr")))
            canvas.drawText("Düzenleme Tarihi: $todayStr", 420f, y + 42f, subPaint)

            y += 68f

            // Summary Grid - 2 Columns
            val col1X = 30f
            val col2X = 300f
            val cardWidth = 255f

            // Box 1: Çalışma İstatistikleri
            val box1Rect = RectF(col1X, y, col1X + cardWidth, y + 160f)
            canvas.drawRoundRect(box1Rect, 6f, 6f, rectPaint)
            var sy1 = y + 18f
            canvas.drawText("Çalışma Özeti", col1X + 10f, sy1, headerPaint); sy1 += 18f

            fun statRow(label: String, valStr: String) {
                canvas.drawText(label, col1X + 10f, sy1, bodyPaint)
                canvas.drawText(valStr, col1X + 160f, sy1, boldBodyPaint)
                sy1 += 16f
            }
            statRow("Çalışılan Gün:", "${summary.workedDays} gün")
            statRow("Toplam Çalışma:", "${String.format(Locale.US, "%.1f", summary.totalHours)} saat")
            statRow("Fazla Mesai:", "${String.format(Locale.US, "%.1f", summary.overtimeHours)} saat")
            statRow("Pazar Mesaisi:", "${String.format(Locale.US, "%.1f", summary.sundayHours)} saat")
            statRow("Resmi Tatil:", "${String.format(Locale.US, "%.1f", summary.holidayHours)} saat")
            statRow("Gece Vardiyası:", "${summary.nightShiftDays} gün")
            statRow("İzinli / Raporlu:", "${summary.leaveDays} / ${summary.reportDays} gün")

            // Box 2: Maaş Detayı
            val box2Rect = RectF(col2X, y, col2X + cardWidth, y + 160f)
            canvas.drawRoundRect(box2Rect, 6f, 6f, rectPaint)
            var sy2 = y + 18f
            canvas.drawText("Maaş Detayı", col2X + 10f, sy2, headerPaint); sy2 += 18f

            fun salaryRow(label: String, valStr: String, isNet: Boolean = false) {
                val p = if (isNet) Paint().apply { textSize = 10.5f; isFakeBoldText = true; color = 0xFF2E7D32.toInt() } else bodyPaint
                val vp = if (isNet) Paint().apply { textSize = 10.5f; isFakeBoldText = true; color = 0xFF2E7D32.toInt() } else boldBodyPaint
                canvas.drawText(label, col2X + 10f, sy2, p)
                canvas.drawText(valStr, col2X + 160f, sy2, vp)
                sy2 += 15f
            }

            salaryRow("Temel Maaş:", "${String.format(Locale.US, "%,.2f", summary.baseSalary)} ₺")
            if (summary.overtimePay > 0) salaryRow("Fazla Mesai:", "${String.format(Locale.US, "%,.2f", summary.overtimePay)} ₺")
            if (summary.sundayPay > 0) salaryRow("Pazar Mesaisi:", "${String.format(Locale.US, "%,.2f", summary.sundayPay)} ₺")
            if (summary.holidayPay > 0) salaryRow("Resmi Tatil:", "${String.format(Locale.US, "%,.2f", summary.holidayPay)} ₺")
            if (summary.nightShiftPay > 0) salaryRow("Gece Primi:", "${String.format(Locale.US, "%,.2f", summary.nightShiftPay)} ₺")
            if (summary.mealAllowance + summary.transportAllowance > 0) salaryRow("Yemek / Yol:", "${String.format(Locale.US, "%,.2f", summary.mealAllowance + summary.transportAllowance)} ₺")
            if (summary.bonus > 0) salaryRow("Prim / Ek:", "${String.format(Locale.US, "%,.2f", summary.bonus)} ₺")
            if (summary.advance + summary.deductions > 0) salaryRow("Kesintiler:", "-${String.format(Locale.US, "%,.2f", summary.advance + summary.deductions)} ₺")
            canvas.drawLine(col2X + 10f, sy2 - 2f, col2X + 245f, sy2 - 2f, linePaint)
            salaryRow("NET MAAŞ:", "${String.format(Locale.US, "%,.2f", summary.netSalary)} ₺", isNet = true)

            y += 175f

            // Daily Log Table Title
            canvas.drawText("Günlük Çalışma ve Mesai Dökümü (Tüm Ay)", 30f, y, headerPaint)
            y += 12f

            fun drawTableHeader(c: android.graphics.Canvas, currentY: Float): Float {
                val thBg = RectF(30f, currentY, 565f, currentY + 18f)
                val thPaint = Paint().apply { color = 0xFF1565C0.toInt() }
                c.drawRoundRect(thBg, 4f, 4f, thPaint)

                val thTextPaint = Paint().apply { textSize = 8.5f; isFakeBoldText = true; color = Color.WHITE }
                c.drawText("Tarih", 35f, currentY + 13f, thTextPaint)
                c.drawText("Gün", 95f, currentY + 13f, thTextPaint)
                c.drawText("Giriş - Çıkış", 135f, currentY + 13f, thTextPaint)
                c.drawText("Mola", 215f, currentY + 13f, thTextPaint)
                c.drawText("Çalışma", 255f, currentY + 13f, thTextPaint)
                c.drawText("Mesai", 305f, currentY + 13f, thTextPaint)
                c.drawText("Durum / Not", 355f, currentY + 13f, thTextPaint)
                return currentY + 22f
            }

            y = drawTableHeader(canvas, y)

            // Construct full month list (1..daysInMonth)
            val savedMap = workDays.associateBy { it.date.dayOfMonth }
            val daysInMonth = ym.lengthOfMonth()
            val fullMonthDays = (1..daysInMonth).map { dayNum ->
                val date = ym.atDay(dayNum)
                savedMap[dayNum] ?: WorkDay(date = date, isSunday = date.dayOfWeek == DayOfWeek.SUNDAY)
            }

            val rowHeight = 16f
            val rowAltBg = Paint().apply { color = 0xFFF9FBFD.toInt() }

            fullMonthDays.forEachIndexed { idx, day ->
                if (y > pageHeight - 45f) {
                    document.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = document.startPage(pageInfo)
                    canvas = page.canvas
                    y = 35f
                    y = drawTableHeader(canvas, y)
                }

                if (idx % 2 == 1) {
                    canvas.drawRect(30f, y - 11f, 565f, y + 5f, rowAltBg)
                }

                val dateStr = day.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                val dayName = day.date.format(DateTimeFormatter.ofPattern("EEE", Locale("tr")))
                val times = if (day.startTime != null && day.endTime != null) "${day.startTime} - ${day.endTime}" else "-"
                val breakStr = if (day.breakMinutes > 0) "${day.breakMinutes} dk" else "-"
                val workedStr = if (day.workedHours > 0) "${String.format(Locale.US, "%.1f", day.workedHours)}s" else "-"
                val otStr = if (day.overtimeHours > 0) "${String.format(Locale.US, "%.1f", day.overtimeHours)}s" else "-"

                val (statusStr, statusColor) = when {
                    day.isLeave -> "İzinli" to 0xFF616161.toInt()
                    day.isReport -> "Raporlu" to 0xFF616161.toInt()
                    day.didNotWork -> "Çalışmadı" to 0xFFC62828.toInt()
                    day.isHoliday && day.isWorked -> "Resmi Tatil (Çalışıldı)" to 0xFF0277BD.toInt()
                    day.isHoliday -> "Resmi Tatil" to 0xFF0277BD.toInt()
                    day.isNightShift && day.overtimeHours > 0 -> "Gece Vard. + ${String.format(Locale.US, "%.1f", day.overtimeHours)}s Mesai" to 0xFF4A148C.toInt()
                    day.isNightShift -> "Gece Vardiyası" to 0xFF4A148C.toInt()
                    day.isSunday && day.isWorked -> "Pazar (Çalışıldı)" to 0xFFC62828.toInt()
                    day.isSunday -> "Pazar Tatili" to 0xFFC62828.toInt()
                    day.overtimeHours > 0 -> "Fazla Mesai (${String.format(Locale.US, "%.1f", day.overtimeHours)} sa)" to 0xFFE65100.toInt()
                    day.isWorked -> "Çalışıldı" to 0xFF2E7D32.toInt()
                    day.notes.isNotBlank() -> day.notes.take(22) to 0xFF333333.toInt()
                    else -> "Çalışılmadı" to 0xFF9E9E9E.toInt()
                }

                val statusPaint = Paint().apply { textSize = 9f; isFakeBoldText = true; color = statusColor }

                canvas.drawText(dateStr, 35f, y, bodyPaint)
                canvas.drawText(dayName, 95f, y, bodyPaint)
                canvas.drawText(times, 135f, y, bodyPaint)
                canvas.drawText(breakStr, 215f, y, bodyPaint)
                canvas.drawText(workedStr, 255f, y, boldBodyPaint)
                canvas.drawText(otStr, 305f, y, boldBodyPaint)
                canvas.drawText(statusStr, 355f, y, statusPaint)

                y += rowHeight
            }

            // Footer
            val footerPaint = Paint().apply { textSize = 8f; color = 0xFF9E9E9E.toInt() }
            canvas.drawText("Mesai Hesap Uygulaması Tarafından Otomatik Oluşturulmuştur.", 30f, pageHeight - 20f, footerPaint)

            document.finishPage(page)

            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
            val file = File(dir, "MesaiRaporu_${summary.yearMonth}.pdf")
            document.writeTo(FileOutputStream(file))
            document.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun sharePdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "PDF Raporunu Paylaş"))
    }

    fun openPdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            sharePdf(context, file)
        }
    }
}
