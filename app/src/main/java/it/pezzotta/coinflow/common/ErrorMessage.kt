package it.pezzotta.coinflow.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.pezzotta.coinflow.R
import it.pezzotta.coinflow.data.model.Coin
import it.pezzotta.coinflow.ui.theme.CoinflowTheme
import it.pezzotta.coinflow.viewmodel.CoinViewModel

@Composable
fun ErrorMessage(
    coinViewModel: CoinViewModel?, isDetailsScreen: Boolean, coin: Coin, days: Int, precision: Int
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Rounded.Warning,
            contentDescription = "Warning",
            Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.ops),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.5f)
                    .padding(top = 24.dp)
                    .testTag("retry_button"), onClick = {
                    if (!isDetailsScreen) coinViewModel?.getCoinMarket(isRefreshing = true) else coinViewModel?.getCoinDetails(
                        coin, days, precision
                    )
                }) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}

@Preview
@Composable
fun ErrorMessagePreview() {
    CoinflowTheme {
        Surface {
            ErrorMessage(
                null, false, Coin(), 7, 8
            )
        }
    }
}
