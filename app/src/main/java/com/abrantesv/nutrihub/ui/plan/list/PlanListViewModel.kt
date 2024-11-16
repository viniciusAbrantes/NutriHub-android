package com.abrantesv.nutrihub.ui.plan.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrantesv.nutrihub.data.entities.Meal
import com.abrantesv.nutrihub.data.entities.Plan
import com.abrantesv.nutrihub.data.patient.PatientRepository
import com.abrantesv.nutrihub.util.Routes
import com.abrantesv.nutrihub.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlanListViewModel @Inject constructor(
    private val repository: PatientRepository
) : ViewModel() {
    private val _plans = repository.getAllTemplatePlans()
    val plans = _plans.flatMapLatest { plans ->
        plans.forEach { plan ->
            if (plan.id != null) {
                plan.meals.addAll(repository.getPlanMeals(plan.id))
                plan.meals.forEach { meal ->
                    if (meal.id != null) meal.foods.addAll(repository.getMealFoods(meal.id))
                }
            }
        }
        MutableStateFlow(plans)
    }

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: PlanListEvent) {
        Log.d(TAG, "onEvent event=$event")

        when (event) {
            PlanListEvent.OnClickAddPlan -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_OR_EDIT_PLAN))
            }

            is PlanListEvent.OnClickEditPlan -> {
                sendUiEvent(
                    UiEvent.Navigate(
                        Routes.ADD_OR_EDIT_PLAN + "?${Routes.PLAN_ID_ARGUMENT}=${event.plan.id}"
                    )
                )
            }

            is PlanListEvent.OnClickDeletePlan -> {
                viewModelScope.launch {
                    deletePlan(event.plan)
                }
            }
        }
    }

    /*private suspend fun getPlanMeals(planId: Int): List<Meal> {
        return repository.getPlanWithMeals(planId).firstOrNull()?.meals ?: emptyList()
    }

    private suspend fun getMealFoods(mealId: Int): List<Food> {
        return repository.getMealWithFoods(mealId).firstOrNull()?.foods ?: emptyList()
    }*/

    private suspend fun deletePlan(plan: Plan) {
        if (plan.id == null) return

        Log.d(TAG, "Deleting ${plan.name}")
        repository.getPlanMeals(plan.id).forEach { meal ->
            deleteMeal(meal)
        }
        repository.deletePlan(plan)
    }

    private suspend fun deleteMeal(meal: Meal) {
        if (meal.id == null) return

        Log.d(TAG, "Deleting ${meal.name}")
        repository.getMealFoods(meal.id).forEach { food ->
            Log.d(TAG, "Deleting ${food.name}")
            repository.deleteFood(food)
        }
        repository.deleteMeal(meal)
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    companion object {
        val TAG = PlanListViewModel::class.simpleName
    }
}