package it.pezzotta.coinflow.ui.state

import it.pezzotta.coinflow.data.model.Coin

sealed class CoinMarketState {
    object Loading : CoinMarketState()
    data class Success(val coins: List<Coin>) : CoinMarketState()
    data class Error(val throwable: Throwable) : CoinMarketState()
}
