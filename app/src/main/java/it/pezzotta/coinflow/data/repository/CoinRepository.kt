package it.pezzotta.coinflow.data.repository

import it.pezzotta.coinflow.Constants
import it.pezzotta.coinflow.data.model.Coin
import it.pezzotta.coinflow.data.model.CoinDetail
import it.pezzotta.coinflow.data.remote.CoinService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CoinRepository(
    private val coinService: CoinService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun getCoinMarket(): Result<List<Coin>> {
        return try {
            val response = coinService.getCoinMarket(
                url = Constants.MARKETS_PATH,
                key = Constants.API_KEY,
                vsCurrency = Constants.EUR,
                perPage = 10,
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

    suspend fun getCoinData(coin: Coin): Result<CoinDetail> {
        return try {
            val response = coinService.getCoinData(
                url = coin.id!!,
                key = Constants.API_KEY,
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

    suspend fun getCoinMarketHistory(coin: Coin) = withContext(dispatcher) {
        withContext(dispatcher) {
            coinService.getCoinMarketHistory(
                url = coin.id!! + Constants.MARKET_CHART_PATH,
                key = Constants.API_KEY,
                vsCurrency = Constants.EUR,
                days = 7,
                interval = Constants.DAILY_INTERVAL,
                precision = 2
            )
        }
    }
}
