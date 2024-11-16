package com.abrantesv.nutrihub.data.patient

import com.abrantesv.nutrihub.data.entities.Food
import com.abrantesv.nutrihub.data.entities.Meal
import com.abrantesv.nutrihub.data.entities.Patient
import com.abrantesv.nutrihub.data.entities.Plan
import com.abrantesv.nutrihub.data.entities.relations.MealWithFoods
import com.abrantesv.nutrihub.data.entities.relations.PlanAndPatient
import com.abrantesv.nutrihub.data.entities.relations.PlanWithMeals
import kotlinx.coroutines.flow.Flow

interface PatientRepository {
    suspend fun insertPatient(patient: Patient): Long
    suspend fun insertPlan(plan: Plan): Long
    suspend fun insertMeal(meal: Meal): Long
    suspend fun insertFood(food: Food): Long
    suspend fun deletePatient(patient: Patient)
    suspend fun deletePlan(plan: Plan)
    suspend fun deleteMeal(meal: Meal)
    suspend fun deleteFood(food: Food)
    suspend fun getPatientById(id: Int): Patient?
    fun searchPatients(name: String): Flow<List<Patient>>
    fun getAllPatients(): Flow<List<Patient>>
    suspend fun getPlanById(id: Int): Plan?
    fun getAllTemplatePlans(): Flow<List<Plan>>
    suspend fun getMealWithFoods(mealId: Int): List<MealWithFoods>
    suspend fun getPlanWithMeals(planId: Int): List<PlanWithMeals>
    suspend fun getPlanAndPatientWithPlanId(planId: Int): List<PlanAndPatient>
    suspend fun getPlanMeals(planId: Int): List<Meal>
    suspend fun getMealFoods(mealId: Int): List<Food>
}