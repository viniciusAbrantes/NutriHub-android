package com.abrantesv.nutrihub.util

sealed class UiEvent {
    data object PopBackStack : UiEvent()
    data class Navigate(val route: String) : UiEvent()
    data class ShowSnackBar(val message: String, val action: String? = null) : UiEvent()
}
