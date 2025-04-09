@file:OptIn(ExperimentalMaterial3Api::class)

package it.pezzotta.coinflow.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dagger.hilt.android.AndroidEntryPoint
import it.pezzotta.coinflow.CoinViewModel
import it.pezzotta.coinflow.data.model.Coin
import it.pezzotta.coinflow.ui.theme.CoinflowTheme
import it.pezzotta.coinflow.ui.theme.Green
import it.pezzotta.coinflow.ui.theme.Red

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val coinViewModel: CoinViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoinflowTheme {
                CoinScreen(coinViewModel)
            }
        }
    }
}

@Composable
fun CoinScreen(coinViewModel: CoinViewModel) {
    val coinMarketState = coinViewModel.coinMarket.collectAsState(initial = null)
    val result = coinMarketState.value

    Scaffold(
        modifier = Modifier.fillMaxSize(), topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("COINFLOW", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                },
            )
        }) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                result == null -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                result.isSuccess -> {
                    val coinList = result.getOrNull() ?: emptyList()
                    CryptoList(coins = coinList)
                }

                result.isFailure -> {
                    Text(
                        text = "Error: ${result.exceptionOrNull()?.message}",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun CryptoList(coins: List<Coin>) {
    LazyColumn {
        items(coins) { coin ->
            CryptoItem(coin)
            if (coin != coins.last()) HorizontalDivider()
        }
    }
}

@Composable
fun CryptoItem(coin: Coin) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable {
                with(context) { startActivity(DetailsActivity.newIntent(context, coin)) }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            coin.image?.let {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(it).crossfade(true)
                        .build(),
                    contentDescription = "Coin image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(24.dp))
                )
            }
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                coin.symbol?.let {
                    Text(
                        text = it.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                coin.name?.let { Text(text = it) }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = AbsoluteAlignment.Right, modifier = Modifier.padding(4.dp)) {
                coin.currentPrice?.let {
                    Text(
                        text = "${"%.2f".format(it)} €",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                Row {
                    coin.priceChange24h?.let {
                        Text(
                            text = if (it > 0) "+${"%.2f".format(it) + "%"}" else "%.2f".format(it) + "%",
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
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowRight,
                contentDescription = "Details"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CoinflowTheme {}
}
