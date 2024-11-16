package com.abrantesv.nutrihub.data.patient

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.abrantesv.nutrihub.data.entities.Food
import com.abrantesv.nutrihub.data.entities.Meal
import com.abrantesv.nutrihub.data.entities.Patient
import com.abrantesv.nutrihub.data.entities.Plan
import com.abrantesv.nutrihub.data.entities.relations.MealWithFoods
import com.abrantesv.nutrihub.data.entities.relations.PlanAndPatient
import com.abrantesv.nutrihub.data.entities.relations.PlanWithMeals
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: Patient): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: Plan): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: Meal): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: Food): Long

    @Delete
    suspend fun deletePatient(patient: Patient)

    @Delete
    suspend fun deletePlan(plan: Plan)

    @Delete
    suspend fun deleteMeal(meal: Meal)

    @Delete
    suspend fun deleteFood(food: Food)

    @Query("SELECT * FROM patient WHERE id = :id")
    suspend fun getPatientById(id: Int): Patient?

    @Query("SELECT * FROM patient WHERE name LIKE '%' || :name || '%'")
    fun getPatientsByName(name: String?): Flow<List<Patient>>

    @Query("SELECT * FROM patient")
    fun getAllPatients(): Flow<List<Patient>>

    @Query("SELECT * FROM `plan` WHERE id = :id")
    suspend fun getPlanById(id: Int): Plan?

    @Query("SELECT * FROM `plan` where isTemplate = 1")
    fun getAllTemplatePlans(): Flow<List<Plan>>

    @Transaction
    @Query("SELECT * FROM meal WHERE id = :mealId")
    suspend fun getMealWithFoods(mealId: Int): List<MealWithFoods>

    @Transaction
    @Query("SELECT * FROM `plan` WHERE id = :planId")
    suspend fun getPlanWithMeals(planId: Int): List<PlanWithMeals>

    @Transaction
    @Query("SELECT * FROM `plan` WHERE id = :planId")
    suspend fun getPlanAndPatientWithPlanId(planId: Int): List<PlanAndPatient>
}