package it.pezzotta.coinflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.pezzotta.coinflow.data.model.Coin
import it.pezzotta.coinflow.data.model.CoinDetail
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

    private val _coinData = MutableStateFlow<Result<CoinDetail>?>(null)
    val coinData: StateFlow<Result<CoinDetail>?> = _coinData

    private fun getCoinMarket() {
        viewModelScope.launch {
            val result = coinRepository.getCoinMarket()
            _coinMarket.value = result
        }
    }

    fun getCoinData(coin: Coin) {
        viewModelScope.launch {
            val result = coinRepository.getCoinData(coin)
            _coinData.value = result
        }
    }
}