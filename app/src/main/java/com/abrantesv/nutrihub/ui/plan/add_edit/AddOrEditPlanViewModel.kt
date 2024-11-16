package com.abrantesv.nutrihub.ui.plan.add_edit

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrantesv.nutrihub.data.entities.Food
import com.abrantesv.nutrihub.data.entities.Meal
import com.abrantesv.nutrihub.data.entities.Plan
import com.abrantesv.nutrihub.data.patient.PatientRepository
import com.abrantesv.nutrihub.util.Routes
import com.abrantesv.nutrihub.util.Routes.DEFAULT_INVALID_ID
import com.abrantesv.nutrihub.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddOrEditPlanViewModel @Inject constructor(
    private val repository: PatientRepository, savedStateHandle: SavedStateHandle
) : ViewModel() {

    var plan by mutableStateOf<Plan?>(null)
        private set

    private var planId = DEFAULT_INVALID_ID
    private var mealId = DEFAULT_INVALID_ID

    var isInitialized by mutableStateOf(false)
        private set

    private var isTemplate by mutableStateOf(true)

    var planName by mutableStateOf("")
        private set

    var meals by mutableStateOf<List<Meal>>(emptyList())
        private set

    var isEditingMeal by mutableStateOf(false)
        private set

    var mealName by mutableStateOf("")
        private set

    var foods by mutableStateOf<List<Food>>(emptyList())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            val routeId = savedStateHandle.get<Int>(Routes.PLAN_ID_ARGUMENT) ?: DEFAULT_INVALID_ID
            if (routeId == DEFAULT_INVALID_ID) {
                planId = repository.insertPlan(createEmptyPlan()).toInt()
            } else {
                repository.getPlanById(routeId)?.let { plan ->
                    planId = plan.id ?: DEFAULT_INVALID_ID
                    planName = plan.name
                    isTemplate = plan.isTemplate
                    plan.id?.let {
                        meals = repository.getPlanMeals(planId)
                        meals.forEach { meal ->
                            meal.id?.let {
                                meal.foods.addAll(repository.getMealFoods(meal.id))
                            }
                        }
                    }
                }
            }
            Log.d(TAG, "init planId=${planId}")
            isInitialized = true
        }
    }

    fun onEvent(event: AddOrEditPlanEvent) {
        Log.d(TAG, "onEvent event=$event")

        when (event) {
            is AddOrEditPlanEvent.OnClickAddMeal -> {
                viewModelScope.launch {
                    mealId = repository.insertMeal(createEmptyMeal()).toInt()
                    foods = emptyList()
                    isEditingMeal = true
                    Log.d(TAG, "OnClickAddMeal editingMealId=$mealId")
                }
            }

            is AddOrEditPlanEvent.OnClickEditMeal -> {
                event.meal.let {
                    mealId = it.id ?: DEFAULT_INVALID_ID
                    mealName = it.name
                    foods = it.foods
                    isEditingMeal = true
                    Log.d(TAG, "OnClickEditMeal editingMealId=$mealId")
                }
            }

            is AddOrEditPlanEvent.OnClickAddFood -> {
                viewModelScope.launch {
                    val id = repository.insertFood(createEmptyFood()).toInt()
                    val foods = foods.toMutableList()
                    foods.add(
                        Food(
                            id = id,
                            name = DEFAULT_ITEM_NAME,
                            amount = 0,
                            unit = DEFAULT_ITEM_UNIT,
                            mealId = mealId
                        )
                    )
                    this@AddOrEditPlanViewModel.foods = foods
                }
            }

            is AddOrEditPlanEvent.OnClickCloseMeal -> {
                if (validateMeal()) {
                    viewModelScope.launch {
                        refreshMeals()
                        isEditingMeal = false
                    }
                }
            }

            is AddOrEditPlanEvent.OnChangePlanName -> {
                viewModelScope.launch {
                    planName = event.name
                    repository.insertPlan(Plan(planId, planName, isTemplate))
                    Log.d(TAG, "OnChangePlanName new name=$planName")
                }
            }

            is AddOrEditPlanEvent.OnChangeMealName -> {
                viewModelScope.launch {
                    mealName = event.name
                    repository.insertMeal(Meal(mealId, mealName, planId))
                    Log.d(TAG, "OnChangePlanName new name=$planName")
                }
            }

            is AddOrEditPlanEvent.OnChangeFood -> {
                viewModelScope.launch {
                    repository.insertFood(
                        Food(
                            id = event.id,
                            name = event.name,
                            amount = event.amount,
                            unit = event.unit,
                            mealId = mealId
                        )
                    )
                    Log.d(TAG, "OnChangePlanName new name=$planName")
                }
            }

            AddOrEditPlanEvent.OnClickClosePlan -> {
                if (validatePlan()) {
                    viewModelScope.launch {
                        repository.insertPlan(Plan(planId, planName, isTemplate))
                        sendUiEvent(UiEvent.PopBackStack)
                    }
                }
            }

            AddOrEditPlanEvent.OnClickCancelMeal -> {
                mealId = DEFAULT_INVALID_ID
                mealName = ""
                foods = emptyList()
                isEditingMeal = false
            }
        }
    }

    private fun validatePlan(): Boolean {
        if (planName.isBlank()) {
            sendUiEvent(UiEvent.ShowSnackBar("Please inform the plan name"))
            return false
        }

        if (meals.isEmpty()) {
            sendUiEvent(UiEvent.ShowSnackBar("Please add at least one meal"))
            return false
        }

        return true
    }

    private fun validateMeal(): Boolean {
        if (mealName.isBlank()) {
            sendUiEvent(UiEvent.ShowSnackBar("Please inform the meal name"))
            return false
        }

        if (foods.isEmpty()) {
            sendUiEvent(UiEvent.ShowSnackBar("Please add at least one item"))
            return false
        }

        return true
    }

    private suspend fun refreshMeals() {
        meals = repository.getPlanMeals(planId)
        meals.forEach { meal ->
            meal.id?.let {
                meal.foods.addAll(repository.getMealFoods(meal.id))
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private fun createEmptyPlan(): Plan {
        return Plan(id = null, name = "", isTemplate = isTemplate)
    }

    private fun createEmptyMeal(): Meal {
        return Meal(id = null, name = "", planId)
    }

    private fun createEmptyFood(): Food {
        return Food(
            id = null,
            name = DEFAULT_ITEM_NAME,
            amount = 0,
            unit = DEFAULT_ITEM_UNIT,
            mealId = mealId
        )
    }

    companion object {
        private val TAG = AddOrEditPlanViewModel::class.simpleName
        private const val DEFAULT_ITEM_NAME = "item"
        private const val DEFAULT_ITEM_UNIT = "g"
    }
}