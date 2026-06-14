package com.mesaihesap.app.notification

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import java.util.Calendar

object NotificationScheduler {

    fun scheduleAll(context: Context) {
        scheduleDailyReminder(context)
    }

    private fun scheduleDailyReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }
        try {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
        } catch (_: SecurityException) {}
    }

    fun sendMesaiReminder(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, "mesai_reminder")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Mesai Takibi")
            .setContentText("Bugünkü mesaini girmeyi unutma!")
            .setAutoCancel(true)
            .build()
        manager.notify(1001, notification)
    }
}
