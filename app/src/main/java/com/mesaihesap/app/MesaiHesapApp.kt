package com.mesaihesap.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MesaiHesapApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            listOf(
                NotificationChannel("mesai_reminder", "Mesai Hatırlatıcı", NotificationManager.IMPORTANCE_DEFAULT),
                NotificationChannel("salary_reminder", "Maaş Hatırlatıcı", NotificationManager.IMPORTANCE_HIGH),
                NotificationChannel("general", "Genel Bildirimler", NotificationManager.IMPORTANCE_LOW)
            ).forEach { manager?.createNotificationChannel(it) }
        }
    }
}
