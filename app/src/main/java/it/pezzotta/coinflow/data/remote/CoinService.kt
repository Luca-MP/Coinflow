package it.pezzotta.coinflow.data.remote

import it.pezzotta.coinflow.data.model.Coin
import it.pezzotta.coinflow.data.model.CoinData
import it.pezzotta.coinflow.data.model.CoinMarketHistory
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.Url

interface CoinService {
    @GET
    @Headers("Content-Type: application/json")
    suspend fun getCoinMarket(
        @Url url: String,
        @Header("x-cg-api-key") key: String,
        @Query ("vs_currency") vsCurrency: String,
        @Query ("per_page") perPage: Int
    ): Response<List<Coin>>

    @GET
    @Headers("Content-Type: application/json")
    suspend fun getCoinData(
        @Url url: String,
        @Header("x-cg-api-key") key: String,
    ): Response<CoinData>

    @GET
    @Headers("Content-Type: application/json")
    suspend fun getCoinMarketHistory(
        @Url url: String,
        @Header("x-cg-api-key") key: String,
        @Query ("vs_currency") vsCurrency: String,
        @Query ("days") days: Int,
        @Query ("precision") precision: Int
    ): Response<CoinMarketHistory>
}
