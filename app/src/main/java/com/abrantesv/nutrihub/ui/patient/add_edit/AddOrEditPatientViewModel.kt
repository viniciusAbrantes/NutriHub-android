package com.abrantesv.nutrihub.ui.patient.add_edit

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrantesv.nutrihub.data.entities.Patient
import com.abrantesv.nutrihub.data.patient.PatientRepository
import com.abrantesv.nutrihub.util.Routes.DEFAULT_INVALID_ID
import com.abrantesv.nutrihub.util.Routes.PATIENT_ID_ARGUMENT
import com.abrantesv.nutrihub.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddOrEditPatientViewModel @Inject constructor(
    private val repository: PatientRepository, savedStateHandle: SavedStateHandle
) : ViewModel() {
    var patient by mutableStateOf<Patient?>(null)
        private set

    var name by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    private var sex by mutableIntStateOf(SEX_OTHER_INT)

    var age by mutableIntStateOf(INVALID_AGE)
        private set

    var height by mutableFloatStateOf(MIN_HEIGHT)
        private set

    var weight by mutableFloatStateOf(MIN_WEIGHT)
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        val patientId = savedStateHandle.get<Int>(PATIENT_ID_ARGUMENT)
        if (patientId != null && patientId != DEFAULT_INVALID_ID) {
            viewModelScope.launch {
                repository.getPatientById(patientId)?.let { patient ->
                    this@AddOrEditPatientViewModel.patient = patient
                    name = patient.name
                    email = patient.email ?: ""
                    sex = patient.sex.toInt()
                    age = patient.age
                    weight = patient.weight
                    height = patient.height
                }
            }
        }
    }

    fun onEvent(event: AddOrEditPatientEvent) {
        Log.d(TAG, "onEvent event=$event")

        when (event) {
            is AddOrEditPatientEvent.OnChangeName -> name = event.name
            is AddOrEditPatientEvent.OnChangeEmail -> email = event.email
            is AddOrEditPatientEvent.OnChangeSex -> sex = event.sex
            is AddOrEditPatientEvent.OnChangeAge -> age = event.age
            is AddOrEditPatientEvent.OnChangeHeight -> height = event.height
            is AddOrEditPatientEvent.OnChangeWeight -> weight = event.weight

            AddOrEditPatientEvent.OnClickSave -> {
                createPatient()?.let {
                    viewModelScope.launch {
                        repository.insertPatient(it)
                    }
                    sendUiEvent(UiEvent.PopBackStack)
                }
            }

            AddOrEditPatientEvent.OnClickCancel -> {
                sendUiEvent(UiEvent.PopBackStack)
            }
        }
    }

    private fun createPatient(): Patient? {
        return if (!validatePatient()) {
            null
        } else {
            Patient(
                id = patient?.id,
                name = name.trim(),
                email = email,
                sex = sex.toShort(),
                age = age,
                height = height,
                weight = weight,
                planId = null
            )
        }
    }

    private fun validatePatient(): Boolean {
        if (name.isBlank()) {
            sendUiEvent(UiEvent.ShowSnackBar("Please inform a name"))
            return false
        }

        if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            sendUiEvent(UiEvent.ShowSnackBar("Please inform a valid email"))
            return false
        }

        if (age == INVALID_AGE || age > MAX_AGE) {
            sendUiEvent(UiEvent.ShowSnackBar("Please inform a valid age"))
            return false
        }

        if (height == MIN_HEIGHT) {
            sendUiEvent(UiEvent.ShowSnackBar("Please inform the height"))
            return false
        }

        if (weight == MIN_WEIGHT) {
            sendUiEvent(UiEvent.ShowSnackBar("Please inform the weight"))
            return false
        }

        return true
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun getSexText(): String {
        return when (sex) {
            SEX_MALE_INT -> SEX_MALE_TEXT
            SEX_FEMALE_INT -> SEX_FEMALE_TEXT
            else -> SEX_OTHER_TEXT
        }
    }

    fun getSexInt(sexText: String): Int {
        return when (sexText) {
            SEX_MALE_TEXT -> SEX_MALE_INT
            SEX_FEMALE_TEXT -> SEX_FEMALE_INT
            else -> SEX_OTHER_INT
        }
    }

    companion object {
        val TAG = AddOrEditPatientViewModel::class.simpleName
        const val INVALID_AGE = -1
        private const val MAX_AGE = 100
        const val MIN_WEIGHT = 0f
        const val MAX_WEIGHT = 200f
        const val MIN_HEIGHT = 0f
        const val MAX_HEIGHT = 2.5f
        const val SEX_MALE_INT = 0
        const val SEX_FEMALE_INT = 1
        const val SEX_OTHER_INT = 9
        const val SEX_MALE_TEXT = "Male"
        const val SEX_FEMALE_TEXT = "Female"
        const val SEX_OTHER_TEXT = "Other"
    }
}