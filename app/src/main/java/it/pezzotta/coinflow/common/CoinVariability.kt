package it.pezzotta.coinflow.common

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import it.pezzotta.coinflow.data.model.Coin
import it.pezzotta.coinflow.ui.theme.GreenChart
import it.pezzotta.coinflow.ui.theme.RedChart

@Composable
fun CoinVariability(coin: Coin) {
    Row {
        coin.priceChange24h?.let {
            Text(
                text = if (it > 0) "+${"%.2f".format(it) + "€"}" else "%.2f".format(it) + "€",
                color = if (it > 0) GreenChart else RedChart,
            )
        }
        Text("  ")
        coin.marketCapChangePercentage24h?.let {
            Text(
                text = if (it > 0) "+${"%.2f".format(it) + "%  "}" else "%.2f".format(it) + "%  ",
                color = if (it > 0) GreenChart else RedChart,
            )
        }
    }
}
