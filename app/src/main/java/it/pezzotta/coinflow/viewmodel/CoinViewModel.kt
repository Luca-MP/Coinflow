package it.pezzotta.coinflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.pezzotta.coinflow.data.model.Coin
import it.pezzotta.coinflow.data.model.CoinDetails
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
        getCoinMarket()
    }

    private val _coinMarket = MutableStateFlow<Result<List<Coin>>?>(null)
    val coinMarket: StateFlow<Result<List<Coin>>?> = _coinMarket

    private val _coinDetails = MutableStateFlow<Result<CoinDetails>?>(null)
    val coinDetails: StateFlow<Result<CoinDetails>?> = _coinDetails


    fun getCoinMarket() {
        viewModelScope.launch {
            val result = coinRepository.getCoinMarket()
            _coinMarket.value = result
        }
    }

    suspend fun refreshCoinMarket() {
        _coinMarket.value = coinRepository.getCoinMarket()
    }

    fun getCoinDetails(coin: Coin, days: Int, precision: Int) {
        viewModelScope.launch {
            val result = coinRepository.getCoinDetails(coin, days, precision)
            _coinDetails.value = result
        }
    }
}
