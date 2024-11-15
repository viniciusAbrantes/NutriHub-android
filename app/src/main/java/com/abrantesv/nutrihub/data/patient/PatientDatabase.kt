package com.abrantesv.nutrihub.data.patient

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abrantesv.nutrihub.data.entities.Food
import com.abrantesv.nutrihub.data.entities.Meal
import com.abrantesv.nutrihub.data.entities.Plan
import com.abrantesv.nutrihub.data.entities.Patient

@Database(
    entities = [Patient::class, Plan::class, Meal::class, Food::class],
    version = 1,
    exportSchema = false
)
abstract class PatientDatabase : RoomDatabase() {
    abstract val dao: PatientDao
}