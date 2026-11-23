package com.mesaihesap.app.di

import android.content.Context
import androidx.room.Room
import com.mesaihesap.app.data.local.AppDatabase
import com.mesaihesap.app.data.local.dao.SalarySettingsDao
import com.mesaihesap.app.data.local.dao.WorkDayDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "mesai_hesap.db")
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideWorkDayDao(db: AppDatabase): WorkDayDao = db.workDayDao()

    @Provides
    fun provideSalarySettingsDao(db: AppDatabase): SalarySettingsDao = db.salarySettingsDao()
}
