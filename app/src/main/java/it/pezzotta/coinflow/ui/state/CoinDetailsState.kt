package it.pezzotta.coinflow.ui.state

import it.pezzotta.coinflow.data.model.CoinDetails

sealed class CoinDetailsState {
    object Loading : CoinDetailsState()
    data class Success(val coinDetails: CoinDetails) : CoinDetailsState()
    data class Error(val throwable: Throwable) : CoinDetailsState()
}
