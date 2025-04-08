package it.pezzotta.coinflow.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Coin(
    @SerialName("id") val id: String? = null,
    @SerialName("symbol") val symbol: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("image") val image: String? = null,
    @SerialName("current_price") val currentPrice: Double? = null,
    @SerialName("market_cap") val marketCap: Long? = null,
    @SerialName("market_cap_rank") val marketCapRank: Int? = null,
    @SerialName("fully_diluted_valuation") val fullyDilutedValuation: Long? = null,
    @SerialName("total_volume") val totalVolume: Long? = null,
    @SerialName("high_24h") val high24h: Double? = null,
    @SerialName("low_24h") val low24h: Double? = null,
    @SerialName("price_change_24h") val priceChange24h: Double? = null,
    @SerialName("price_change_percentage_24h") val priceChangePercentage24h: Double? = null,
    @SerialName("market_cap_change_24h") val marketCapChange24h: Double? = null,
    @SerialName("market_cap_change_percentage_24h") val marketCapChangePercentage24h: Double? = null,
    @SerialName("circulating_supply") val circulatingSupply: Double? = null,
    @SerialName("total_supply") val totalSupply: Double? = null,
    @SerialName("max_supply") val maxSupply: Double? = null,
    @SerialName("ath") val ath: Double? = null,
    @SerialName("ath_change_percentage") val athChangePercentage: Double? = null,
    @SerialName("ath_date") val athDate: String? = null,
    @SerialName("atl") val atl: Double? = null,
    @SerialName("atl_change_percentage") val atlChangePercentage: Double? = null,
    @SerialName("atl_date") val atlDate: String? = null,
    @SerialName("roi") val roi: Roi? = null,
    @SerialName("last_updated") val lastUpdated: String? = null
) : Parcelable

@Parcelize
@Serializable
data class Roi(
    @SerialName("times") val times: Double? = null,
    @SerialName("currency") val currency: String? = null,
    @SerialName("percentage") val percentage: Double? = null
) : Parcelable
