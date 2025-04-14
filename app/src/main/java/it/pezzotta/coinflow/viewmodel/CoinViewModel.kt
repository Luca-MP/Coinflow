package it.pezzotta.coinflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.pezzotta.coinflow.data.model.Coin
import it.pezzotta.coinflow.data.model.CoinDetails
import it.pezzotta.coinflow.data.repository.CoinMarketState
import it.pezzotta.coinflow.data.repository.CoinRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinViewModel @Inject constructor(
    private val coinRepository: CoinRepository
) : ViewModel() {

    init {
        getCoinMarket(isRefreshing = false)
    }

    private val _coinMarket = MutableStateFlow<CoinMarketState>(CoinMarketState.Loading)
    val coinMarket: StateFlow<CoinMarketState> = _coinMarket

    private val _coinDetails = MutableStateFlow<Result<CoinDetails>?>(null)
    val coinDetails: StateFlow<Result<CoinDetails>?> = _coinDetails

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    fun getCoinMarket(isRefreshing: Boolean) {
        viewModelScope.launch {
            if (isRefreshing) {
                _coinMarket.value = CoinMarketState.Loading
                _isRefreshing.value = true
            }
            val result = coinRepository.getCoinMarket()
            _coinMarket.value = result
            _isRefreshing.value = false
        }
    }

    fun getCoinDetails(coin: Coin, days: Int, precision: Int) {
        viewModelScope.launch {
            val result = coinRepository.getCoinDetails(coin, days, precision)
            _coinDetails.value = result
        }
    }
}
