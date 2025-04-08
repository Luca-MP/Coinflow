package it.pezzotta.coinflow.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class CoinMarketHistory(
    @SerialName("prices") val prices: List<List<Double>>? = null,
    @SerialName("market_caps") val marketCaps: List<List<Double>>? = null,
    @SerialName("total_volumes") val totalVolumes: List<List<Double>>? = null
) : Parcelable
