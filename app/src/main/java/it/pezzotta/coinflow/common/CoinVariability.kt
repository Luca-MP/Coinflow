package it.pezzotta.coinflow.common

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import it.pezzotta.coinflow.data.model.Coin
import it.pezzotta.coinflow.ui.theme.Green
import it.pezzotta.coinflow.ui.theme.Red

@Composable
fun CoinVariability(coin: Coin) {
    Row {
        coin.priceChange24h?.let {
            Text(
                text = if (it > 0) "+${"%.2f".format(it) + "€"}" else "%.2f".format(it) + "€",
                color = if (it > 0) Green else Red,
            )
        }
        Text("  ")
        coin.marketCapChangePercentage24h?.let {
            Text(
                text = if (it > 0) "+${"%.2f".format(it) + "%  "}" else "%.2f".format(it) + "%  ",
                color = if (it > 0) Green else Red,
            )
        }
    }
}
