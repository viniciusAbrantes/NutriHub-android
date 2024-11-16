package com.abrantesv.nutrihub.data.patient

import com.abrantesv.nutrihub.data.entities.Food
import com.abrantesv.nutrihub.data.entities.Meal
import com.abrantesv.nutrihub.data.entities.Patient
import com.abrantesv.nutrihub.data.entities.Plan
import kotlinx.coroutines.flow.Flow

class PatientRepositoryImpl(private val dao: PatientDao) : PatientRepository {
    override suspend fun insertPatient(patient: Patient) = dao.insertPatient(patient)
    override suspend fun insertPlan(plan: Plan) = dao.insertPlan(plan)
    override suspend fun insertMeal(meal: Meal) = dao.insertMeal(meal)
    override suspend fun insertFood(food: Food) = dao.insertFood(food)
    override suspend fun deletePatient(patient: Patient) = dao.deletePatient(patient)
    override suspend fun deletePlan(plan: Plan) = dao.deletePlan(plan)
    override suspend fun deleteMeal(meal: Meal) = dao.deleteMeal(meal)
    override suspend fun deleteFood(food: Food) = dao.deleteFood(food)
    override suspend fun getPatientById(id: Int): Patient? = dao.getPatientById(id)
    override fun searchPatients(name: String): Flow<List<Patient>> = dao.getPatientsByName(name)
    override fun getAllPatients(): Flow<List<Patient>> = dao.getAllPatients()
    override suspend fun getPlanById(id: Int): Plan? = dao.getPlanById(id)
    override fun getAllTemplatePlans(): Flow<List<Plan>> = dao.getAllTemplatePlans()
    override suspend fun getMealWithFoods(mealId: Int) = dao.getMealWithFoods(mealId)
    override suspend fun getPlanWithMeals(planId: Int) = dao.getPlanWithMeals(planId)

    override suspend fun getPlanAndPatientWithPlanId(planId: Int) =
        dao.getPlanAndPatientWithPlanId(planId)

    override suspend fun getPlanMeals(planId: Int): List<Meal> {
        return getPlanWithMeals(planId).firstOrNull()?.meals ?: emptyList()
    }

    override suspend fun getMealFoods(mealId: Int): List<Food> {
        return getMealWithFoods(mealId).firstOrNull()?.foods ?: emptyList()
    }
}
