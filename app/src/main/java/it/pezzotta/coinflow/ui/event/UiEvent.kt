package it.pezzotta.coinflow.ui.event

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
}
