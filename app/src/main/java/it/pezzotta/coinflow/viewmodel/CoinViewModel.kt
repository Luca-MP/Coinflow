package it.pezzotta.coinflow.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.pezzotta.coinflow.data.model.Coin
import it.pezzotta.coinflow.data.model.CoinDetails
import it.pezzotta.coinflow.data.repository.CoinRepository
import it.pezzotta.coinflow.ui.event.UiEvent
import it.pezzotta.coinflow.ui.state.CoinMarketState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinViewModel @Inject constructor(
    private val coinRepository: CoinRepository
) : ViewModel() {

    init {
        getCoinMarket(refresh = false)
    }

    var coinMarket by mutableStateOf<CoinMarketState>(CoinMarketState.Loading)
        private set

    var coinDetails by mutableStateOf<Result<CoinDetails>?>(null)
        private set

    var isRefreshing by mutableStateOf<Boolean>(false)
        private set

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun getCoinMarket(refresh: Boolean) {
        viewModelScope.launch {
            if (refresh) {
                coinMarket = CoinMarketState.Loading
                isRefreshing = true
            }
            val result = coinRepository.getCoinMarket()
            coinMarket = result
            isRefreshing = false

            if (result is CoinMarketState.Error) {
                result.throwable.message?.let {
                    _uiEvent.emit(UiEvent.ShowToast(it))
                }
            }
        }
    }

    fun getCoinDetails(coin: Coin, days: Int, precision: Int) {
        viewModelScope.launch {
            val result = coinRepository.getCoinDetails(coin, days, precision)
            coinDetails = result
        }
    }
}
