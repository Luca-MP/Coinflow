package it.pezzotta.coinflow.common

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import it.pezzotta.coinflow.R
import it.pezzotta.coinflow.data.model.Coin
import it.pezzotta.coinflow.prettyFormat
import it.pezzotta.coinflow.ui.theme.CoinflowTheme
import it.pezzotta.coinflow.ui.theme.GreenChart
import it.pezzotta.coinflow.ui.theme.RedChart

@Composable
fun CoinVariability(coin: Coin, isDetailScreen: Boolean) {
    Row {
        coin.priceChange24h?.let {
            Text(
                text = if (it >= 0) "+${it.prettyFormat() + "€"}" else it.prettyFormat() + "€",
                color = if (it >= 0) GreenChart else RedChart,
            )
        }
        Text("  ")
        coin.priceChangePercentage24h?.let {
            Text(
                text = if (it >= 0) "+${it.prettyFormat() + "% "}" else it.prettyFormat() + "% ",
                color = if (it >= 0) GreenChart else RedChart,
            )
        }
        if (isDetailScreen) coin.priceChange24h?.let {
            Text(
                stringResource(R.string.today),
                color = if (it >= 0) GreenChart else RedChart
            )
        }
    }
}

@Preview
@Composable
fun CoinDetailPreview() {
    CoinflowTheme {
        Surface {
            CoinVariability(
                coin = Coin(
                    priceChange24h = 1000.0,
                    priceChangePercentage24h = 1.0,
                ),
                true,
            )
        }
    }
}
