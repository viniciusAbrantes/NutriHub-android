package com.abrantesv.nutrihub.data.repository

import com.abrantesv.nutrihub.data.entities.Food
import com.abrantesv.nutrihub.data.entities.Meal
import com.abrantesv.nutrihub.data.entities.Patient
import com.abrantesv.nutrihub.data.entities.Plan
import com.abrantesv.nutrihub.data.entities.relations.MealWithFoods
import com.abrantesv.nutrihub.data.entities.relations.PlanAndPatient
import com.abrantesv.nutrihub.data.entities.relations.PlanWithMeals
import com.abrantesv.nutrihub.data.patient.PatientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePatientRepository : PatientRepository {
    private val patients = mutableListOf<Patient>()
    private var patientIdCount = 0
    private val plans = mutableListOf<Plan>()
    private var planIdCount = 0
    private val meals = mutableListOf<Meal>()
    private var mealIdCount = 0
    private val foods = mutableListOf<Food>()
    private var foodIdCount = 0

    override suspend fun insertPatient(patient: Patient): Long {
        patient.copy(id = patientIdCount++).let {
            patients.add(it)
            return it.id?.toLong() ?: INVALID_ID
        }
    }

    override suspend fun insertPlan(plan: Plan): Long {
        plan.copy(id = planIdCount++).let {
            plans.add(it)
            return it.id?.toLong() ?: INVALID_ID
        }
    }

    override suspend fun insertMeal(meal: Meal): Long {
        plans.firstOrNull { it.id == meal.planId }?.let { plan ->
            meal.copy(id = mealIdCount++).let {
                plan.meals.add(it)
                meals.add(it)
                return it.id?.toLong() ?: INVALID_ID
            }
        }
        return INVALID_ID
    }

    override suspend fun insertFood(food: Food): Long {
        meals.firstOrNull { it.id == food.mealId }?.let { meal ->
            food.copy(id = foodIdCount++).let {
                meal.foods.add(it)
                foods.add(it)
                return it.id?.toLong() ?: INVALID_ID
            }
        }
        return INVALID_ID
    }

    override suspend fun deletePatient(patient: Patient) {
        patients.firstOrNull { it.id == patient.id }?.let {
            //if this is the only patient using the plan, it needs to be deleted
            if (patients.count { it.planId == patient.planId } == 1) {
                plans.firstOrNull { it.id == patient.planId }?.let { deletePlan(it) }
            }
        }
    }

    override suspend fun deletePlan(plan: Plan) {
        plan.meals.forEach { deleteMeal(it) }
        plans.remove(plan)
    }

    override suspend fun deleteMeal(meal: Meal) {
        meal.foods.forEach { deleteFood(it) }
        meals.remove(meal)
    }

    override suspend fun deleteFood(food: Food) {
        foods.remove(food)
    }

    override suspend fun getPatientById(id: Int): Patient? = patients.firstOrNull { it.id == id }

    override fun searchPatients(name: String): Flow<List<Patient>> = flow { emit(emptyList()) }

    override fun getAllPatients(): Flow<List<Patient>> = flow { emit(patients) }

    override suspend fun getPlanById(id: Int): Plan? = plans.firstOrNull { it.id == id }

    override fun getAllTemplatePlans(): Flow<List<Plan>> {
        return flow { emit(plans.filter { it.isTemplate }) }
    }

    override suspend fun getPlanMeals(planId: Int): List<Meal> {
        return getPlanById(planId)?.meals ?: emptyList()
    }

    override suspend fun getMealFoods(mealId: Int): List<Food> {
        return meals.firstOrNull { it.id == mealId }?.let {
            return it.foods
        } ?: emptyList()
    }

    override suspend fun getMealWithFoods(mealId: Int): List<MealWithFoods> {
        return emptyList()
    }

    override suspend fun getPlanWithMeals(planId: Int): List<PlanWithMeals> {
        return emptyList()
    }

    override suspend fun getPlanAndPatientWithPlanId(planId: Int): List<PlanAndPatient> {
        return emptyList()
    }

    fun getMeals() = meals
    fun getFoods() = foods

    companion object {
        private const val INVALID_ID = -1L
    }
}