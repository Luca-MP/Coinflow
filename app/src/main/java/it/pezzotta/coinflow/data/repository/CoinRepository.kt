package it.pezzotta.coinflow.data.repository

import it.pezzotta.coinflow.Constants
import it.pezzotta.coinflow.data.model.Coin
import it.pezzotta.coinflow.data.model.CoinData
import it.pezzotta.coinflow.data.model.CoinDetails
import it.pezzotta.coinflow.data.model.CoinMarketHistory
import it.pezzotta.coinflow.data.remote.CoinService
import it.pezzotta.coinflow.ui.state.CoinDetailsState
import it.pezzotta.coinflow.ui.state.CoinMarketState
import javax.inject.Inject

class CoinRepository @Inject constructor(private val coinService: CoinService) {
    suspend fun getCoinMarket(): CoinMarketState {
        return try {
            val response = coinService.getCoinMarket(
                url = Constants.MARKETS_PATH,
                key = Constants.API_KEY,
                vsCurrency = Constants.EUR,
                perPage = 10
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    CoinMarketState.Success(it)
                } ?: CoinMarketState.Error(Exception("Empty body"))
            } else {
                CoinMarketState.Error(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            CoinMarketState.Error(e)
        }
    }

    private suspend fun getCoinData(coin: Coin): Result<CoinData> {
        return try {
            val response = coin.id?.let {
                coinService.getCoinData(
                    url = it, key = Constants.API_KEY
                )
            }
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty body"))
            } else {
                Result.failure(Exception("Error ${response?.code()}: ${response?.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getCoinMarketHistory(
        coin: Coin, days: Int, precision: Int
    ): Result<CoinMarketHistory> {
        return try {
            val response = coinService.getCoinMarketHistory(
                url = coin.id + Constants.MARKET_CHART_PATH,
                key = Constants.API_KEY,
                vsCurrency = Constants.EUR,
                days = days,
                precision = precision
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty body"))
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCoinDetails(coin: Coin, days: Int, precision: Int): CoinDetailsState {
        return try {
            val coinDataResult = getCoinData(coin)
            if (coinDataResult.isFailure) {
                return CoinDetailsState.Error(coinDataResult.exceptionOrNull()!!)
            }

            val coinMarketHistoryResult = getCoinMarketHistory(coin, days, precision)
            if (coinMarketHistoryResult.isFailure) {
                return CoinDetailsState.Error(coinMarketHistoryResult.exceptionOrNull()!!)
            }

            CoinDetailsState.Success(
                CoinDetails(
                    coinData = coinDataResult.getOrThrow(),
                    coinMarketHistory = coinMarketHistoryResult.getOrThrow()
                )
            )
        } catch (e: Exception) {
            CoinDetailsState.Error(e)
        }
    }
}
