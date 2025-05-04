package it.pezzotta.coinflow.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.pezzotta.coinflow.data.model.Coin
import it.pezzotta.coinflow.data.repository.CoinRepository
import it.pezzotta.coinflow.ui.event.UiEvent
import it.pezzotta.coinflow.ui.state.CoinDetailsState
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

    var coinMarketState by mutableStateOf<CoinMarketState>(CoinMarketState.Loading)
        private set

    var coinDetailsState by mutableStateOf<CoinDetailsState>(CoinDetailsState.Loading)
        private set

    var isRefreshing by mutableStateOf<Boolean>(false)
        private set

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun getCoinMarket(refresh: Boolean) {
        viewModelScope.launch {
            if (refresh) {
                coinMarketState = CoinMarketState.Loading
                isRefreshing = true
            }
            val result = coinRepository.getCoinMarket()
            coinMarketState = result
            isRefreshing = false

            if (result is CoinMarketState.Error) {
                result.throwable.message?.let {
                    _uiEvent.emit(UiEvent.ShowToast(it))
                }
            }
        }
    }

    fun getCoinDetails(refresh: Boolean, coin: Coin, days: Int, precision: Int) {
        viewModelScope.launch {
            if (refresh) {
                coinDetailsState = CoinDetailsState.Loading
                isRefreshing = true
            }

            val result = coinRepository.getCoinDetails(coin, days, precision)
            coinDetailsState = result
            isRefreshing = false

            if (result is CoinDetailsState.Error) {
                result.throwable.message?.let {
                    _uiEvent.emit(UiEvent.ShowToast(it))
                }
            }
        }
    }
}
