package com.abrantesv.nutrihub.di

import android.app.Application
import androidx.room.Room
import com.abrantesv.nutrihub.data.patient.PatientDatabase
import com.abrantesv.nutrihub.data.patient.PatientRepository
import com.abrantesv.nutrihub.data.patient.PatientRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val DATABASE_NAME = "patient_db"

    @Provides
    @Singleton
    fun providePatientDatabase(app: Application): PatientDatabase {
        return Room.databaseBuilder(app, PatientDatabase::class.java, DATABASE_NAME).build()
    }

    @Provides
    @Singleton
    fun providePatientRepository(db: PatientDatabase): PatientRepository {
        return PatientRepositoryImpl(db.dao)
    }

}