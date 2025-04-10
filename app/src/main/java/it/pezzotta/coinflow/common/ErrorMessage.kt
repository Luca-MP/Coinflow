package it.pezzotta.coinflow.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import it.pezzotta.coinflow.R
import it.pezzotta.coinflow.data.model.Coin
import it.pezzotta.coinflow.viewmodel.CoinViewModel

@Composable
fun ErrorMessage(
    coinViewModel: CoinViewModel,
    isDetailsScreen: Boolean,
    coin: Coin,
    days: Int,
    precision: Int
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.warning_sign),
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.ops),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
        }
        Button(
            onClick = {
                if (!isDetailsScreen) coinViewModel.getCoinMarket() else coinViewModel.getCoinDetails(
                    coin,
                    days,
                    precision,
                )
            }) {
            Text(text = stringResource(R.string.retry))
        }
    }
}
