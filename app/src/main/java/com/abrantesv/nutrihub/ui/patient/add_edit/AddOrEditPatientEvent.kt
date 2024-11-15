package com.abrantesv.nutrihub.ui.patient.add_edit

sealed class AddOrEditPatientEvent {
    data class OnChangeName(val name: String): AddOrEditPatientEvent()
    data class OnChangeEmail(val email: String): AddOrEditPatientEvent()
    data class OnChangeSex(val sex: Int): AddOrEditPatientEvent()
    data class OnChangeAge(val age: Int): AddOrEditPatientEvent()
    data class OnChangeHeight(val height: Float): AddOrEditPatientEvent()
    data class OnChangeWeight(val weight: Float): AddOrEditPatientEvent()
    data object OnClickSave: AddOrEditPatientEvent()
    data object OnClickCancel: AddOrEditPatientEvent()
}
