package com.abrantesv.nutrihub.ui.patient.list

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrantesv.nutrihub.data.entities.Patient
import com.abrantesv.nutrihub.data.entities.Plan
import com.abrantesv.nutrihub.data.patient.PatientRepository
import com.abrantesv.nutrihub.util.Routes
import com.abrantesv.nutrihub.util.Routes.PATIENT_ID_ARGUMENT
import com.abrantesv.nutrihub.util.Routes.PLAN_ID_ARGUMENT
import com.abrantesv.nutrihub.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PatientListViewModel @Inject constructor(
    private val repository: PatientRepository
) : ViewModel() {
    val templatePlans = repository.getAllTemplatePlans()
    val searchName = MutableStateFlow("")
    val patients: Flow<List<Patient>> = searchName.flatMapLatest {
        if (it.isEmpty()) repository.getAllPatients() else repository.searchPatients(it)
    }

    var shouldShowTemplateDialog by mutableStateOf(false)
        private set

    private var patientSelectingTemplate by mutableStateOf<Patient?>(null)

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: PatientListEvent) {
        Log.d(TAG, "onEvent event=$event")

        when (event) {
            PatientListEvent.OnClickAddPatient -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_OR_EDIT_PATIENT))
            }

            is PatientListEvent.OnClickEditPatient -> {
                sendUiEvent(
                    UiEvent.Navigate(
                        Routes.ADD_OR_EDIT_PATIENT + "?$PATIENT_ID_ARGUMENT=${event.patient.id}"
                    )
                )
            }

            is PatientListEvent.OnClickDeletePatient -> {
                viewModelScope.launch {
                    repository.deletePatient(event.patient)
                }
            }

            is PatientListEvent.OnSearchPatient -> searchName.value = event.name

            is PatientListEvent.OnClickUpdatePlan -> {
                if (event.patient.planId != null) {
                    sendUiEvent(
                        UiEvent.Navigate(
                            Routes.ADD_OR_EDIT_PLAN + "?$PLAN_ID_ARGUMENT=${event.patient.planId}"
                        )
                    )
                } else {
                    viewModelScope.launch {
                        templatePlans.collectLatest {
                            if (it.isNotEmpty()) {
                                shouldShowTemplateDialog = true
                                patientSelectingTemplate = event.patient
                            } else {
                                navigateToNewPlan(event.patient)
                            }
                        }
                    }
                }
            }

            PatientListEvent.OnClickDismissDialog -> shouldShowTemplateDialog = false

            is PatientListEvent.OnSelectTemplatePlan -> {
                shouldShowTemplateDialog = false
                patientSelectingTemplate?.let {
                    viewModelScope.launch {
                        createPlanFromTemplate(it, event.plan.id)
                    }
                }
            }

            PatientListEvent.OnSelectNewPlan -> {
                shouldShowTemplateDialog = false
                patientSelectingTemplate?.let {
                    viewModelScope.launch {
                        navigateToNewPlan(it)
                    }
                }
            }
        }
    }

    private suspend fun createPlanFromTemplate(patient: Patient, planId: Int?) {
        if (planId == null) return
        val newPlanId = repository.insertPlan(
            Plan(id = null, name = "${patient.name} plan", isTemplate = false)
        ).toInt()
        repository.insertPatient(patient.copy(planId = newPlanId))

        repository.getPlanMeals(planId).forEach {meal ->
            val newMealId = repository.insertMeal(meal.copy(id = null, planId = newPlanId)).toInt()
            if (meal.id != null) {
                repository.getMealFoods(meal.id).forEach { food ->
                    repository.insertFood(food.copy(id = null, mealId = newMealId))
                }
            }
        }

        sendUiEvent(
            UiEvent.Navigate(
                Routes.ADD_OR_EDIT_PLAN + "?$PLAN_ID_ARGUMENT=${newPlanId}"
            )
        )
    }

    private suspend fun navigateToNewPlan(patient: Patient) {
        val planId = repository.insertPlan(
            Plan(id = null, name = "${patient.name} plan", isTemplate = false)
        ).toInt()
        repository.insertPatient(patient.copy(planId = planId))
        sendUiEvent(
            UiEvent.Navigate(
                Routes.ADD_OR_EDIT_PLAN + "?$PLAN_ID_ARGUMENT=${planId}"
            )
        )
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    companion object {
        val TAG = PatientListViewModel::class.simpleName
    }
}