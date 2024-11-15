package com.abrantesv.nutrihub.data.patient

import com.abrantesv.nutrihub.data.entities.Food
import com.abrantesv.nutrihub.data.entities.Meal
import com.abrantesv.nutrihub.data.entities.Patient
import com.abrantesv.nutrihub.data.entities.Plan
import kotlinx.coroutines.flow.Flow

class PatientRepository(private val dao: PatientDao) {
    suspend fun insertPatient(patient: Patient) = dao.insertPatient(patient)
    suspend fun insertPlan(plan: Plan) = dao.insertPlan(plan)
    suspend fun insertMeal(meal: Meal) = dao.insertMeal(meal)
    suspend fun insertFood(food: Food) = dao.insertFood(food)
    suspend fun deletePatient(patient: Patient) = dao.deletePatient(patient)
    suspend fun deletePlan(plan: Plan) = dao.deletePlan(plan)
    suspend fun deleteMeal(meal: Meal) = dao.deleteMeal(meal)
    suspend fun deleteFood(food: Food) = dao.deleteFood(food)
    suspend fun getPatientById(id: Int): Patient? = dao.getPatientById(id)
    fun searchPatients(name: String): Flow<List<Patient>> = dao.getPatientsByName(name)
    fun getAllPatients(): Flow<List<Patient>> = dao.getAllPatients()
    suspend fun getPlanById(id: Int): Plan? = dao.getPlanById(id)
    fun getAllTemplatePlans(): Flow<List<Plan>> = dao.getAllTemplatePlans()
    suspend fun getMealWithFoods(mealId: Int) = dao.getMealWithFoods(mealId)
    suspend fun getPlanWithMeals(planId: Int) = dao.getPlanWithMeals(planId)

    suspend fun getPlanAndPatientWithPlanId(planId: Int) = dao.getPlanAndPatientWithPlanId(planId)

    suspend fun getPlanMeals(planId: Int): List<Meal> {
        return getPlanWithMeals(planId).firstOrNull()?.meals ?: emptyList()
    }

    suspend fun getMealFoods(mealId: Int): List<Food> {
        return getMealWithFoods(mealId).firstOrNull()?.foods ?: emptyList()
    }
}
