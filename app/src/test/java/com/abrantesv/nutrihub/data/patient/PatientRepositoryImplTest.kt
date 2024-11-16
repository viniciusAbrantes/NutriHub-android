package com.abrantesv.nutrihub.data.patient

import com.abrantesv.nutrihub.data.entities.Food
import com.abrantesv.nutrihub.data.entities.Meal
import com.abrantesv.nutrihub.data.entities.Patient
import com.abrantesv.nutrihub.data.entities.Plan
import com.abrantesv.nutrihub.data.repository.FakePatientRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class PatientRepositoryImplTest {
    private lateinit var fakeRepository: FakePatientRepository

    @Before
    fun setUp() {
        fakeRepository = FakePatientRepository()
    }

    @Test
    fun `Insert a patient and ensure it's returned by getPatientById`() {
        runBlocking {
            val patientId = fakeRepository.insertPatient(
                Patient(
                    null,
                    "Some Name",
                    null,
                    "some_name@teste.com",
                    24,
                    0,
                    1.73f,
                    78f,
                )
            )

            assertThat(fakeRepository.getPatientById(patientId.toInt())).isNotNull()
        }
    }

    @Test
    fun `Insert a plan and ensure it's returned by getPatientById`() {
        runBlocking {
            val planId = fakeRepository.insertPlan(
                Plan(null, "Some Plan", true)
            )

            assertThat(fakeRepository.getPlanById(planId.toInt())).isNotNull()
        }
    }

    @Test
    fun `Inserting a meal inside an existing plan should increase the meals size`() {
        runBlocking {
            val planId = fakeRepository.insertPlan(
                Plan(null, "Some Plan", true)
            )

            val mealsBefore = fakeRepository.getMeals().size
            fakeRepository.insertMeal(Meal(null, "Breakfast", planId.toInt()))
            val mealsAfter = fakeRepository.getMeals().size

            assertThat(mealsAfter).isGreaterThan(mealsBefore)
        }
    }


    @Test
    fun `Inserting a meal with invalid planId should not be accepted`() {
        runBlocking {
            val mealsBefore = fakeRepository.getMeals().size
            fakeRepository.insertMeal(Meal(null, "Breakfast", 0))
            val mealsAfter = fakeRepository.getMeals().size

            assertThat(mealsBefore).isEqualTo(mealsAfter)
        }
    }

    @Test
    fun `Inserting a food inside an existing meal should increase the foods size`() {
        runBlocking {
            val planId = fakeRepository.insertPlan(
                Plan(null, "Some Plan", true)
            )

            val mealId = fakeRepository.insertMeal(Meal(null, "Breakfast", planId.toInt()))

            val foodsBefore = fakeRepository.getFoods().size
            fakeRepository.insertFood(Food(null, "Bread", 1, "unit", mealId.toInt()))
            val foodsAfter = fakeRepository.getFoods().size

            assertThat(foodsAfter).isGreaterThan(foodsBefore)
        }
    }


    @Test
    fun `Inserting a food with invalid mealId should not be accepted`() {
        runBlocking {
            val foodsBefore = fakeRepository.getFoods().size
            fakeRepository.insertFood(Food(null, "Bread", 1, "unit", 0))
            val foodsAfter = fakeRepository.getFoods().size

            assertThat(foodsBefore).isEqualTo(foodsAfter)
        }
    }

    @Test
    fun `Deleting meal with invalid id should not be accepted`() {
        runBlocking {
            val planId = fakeRepository.insertPlan(
                Plan(null, "Some Plan", true)
            )
            val mealId = fakeRepository.insertMeal(
                Meal(null, "Breakfast", planId.toInt())
            ).toInt()
            val mealsBefore = fakeRepository.getMeals().size
            fakeRepository.deleteMeal(
                Meal(mealId + 1, "Breakfast", planId.toInt())
            )
            val mealsAfter = fakeRepository.getMeals().size

            assertThat(mealsBefore).isEqualTo(mealsAfter)
        }
    }

    @Test
    fun `Deleting food with invalid id should not be accepted`() {
        runBlocking {
            val planId = fakeRepository.insertPlan(
                Plan(null, "Some Plan", true)
            )
            val mealId = fakeRepository.insertMeal(Meal(null, "Breakfast", planId.toInt()))
            val foodId = fakeRepository.insertFood(
                Food(null, "Bread", 1, "unit", mealId.toInt())
            ).toInt()
            val foodsBefore = fakeRepository.getFoods().size
            fakeRepository.deleteFood(
                Food(foodId + 1, "Bread", 1, "unit", mealId.toInt())
            )
            val foodsAfter = fakeRepository.getFoods().size

            assertThat(foodsBefore).isEqualTo(foodsAfter)
        }
    }
}