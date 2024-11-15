package com.abrantesv.nutrihub.ui.patient.list

import com.abrantesv.nutrihub.data.entities.Patient
import com.abrantesv.nutrihub.data.entities.Plan

sealed class PatientListEvent {
    data object OnClickAddPatient : PatientListEvent()
    data class OnClickDeletePatient(val patient: Patient) : PatientListEvent()
    data class OnClickEditPatient(val patient: Patient) : PatientListEvent()
    data class OnSearchPatient(val name: String) : PatientListEvent()
    data class OnClickUpdatePlan(val patient: Patient) : PatientListEvent()
    data object OnClickDismissDialog : PatientListEvent()
    data class OnSelectTemplatePlan(val plan: Plan) : PatientListEvent()
    data object OnSelectNewPlan : PatientListEvent()
}
