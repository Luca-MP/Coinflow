@file:OptIn(ExperimentalMaterial3Api::class)

package it.pezzotta.coinflow.activity

import android.content.Context
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dagger.hilt.android.AndroidEntryPoint
import it.pezzotta.coinflow.R
import it.pezzotta.coinflow.viewmodel.CoinViewModel
import it.pezzotta.coinflow.common.CoinVariability
import it.pezzotta.coinflow.common.ErrorMessage
import it.pezzotta.coinflow.common.CoinPlaceholder
import it.pezzotta.coinflow.data.model.Coin
import it.pezzotta.coinflow.prettyFormat
import it.pezzotta.coinflow.ui.theme.CoinflowTheme

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val coinViewModel: CoinViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoinflowTheme {
                MarketScreen(coinViewModel)
            }
        }
    }
}

@Composable
fun MarketScreen(coinViewModel: CoinViewModel) {
    val context = LocalContext.current
    val coinMarketState = coinViewModel.coinMarket.collectAsState(initial = null)
    val result = coinMarketState.value

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        stringResource(R.string.app_name),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
            )
        },
    ) { innerPadding ->
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
                    CryptoList(context = context, coins = coinList)
                }

                result.isFailure -> {
                    Toast.makeText(
                        context, "${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT
                    ).show()
                    ErrorMessage(coinViewModel, false, Coin(), 7, 8)
                }
            }
        }
    }
}

@Composable
fun CryptoList(context: Context, coins: List<Coin>) {
    LazyColumn {
        items(coins) { coin ->
            CryptoItem(context, coin)
            if (coin != coins.last()) HorizontalDivider()
        }
    }
}

@Composable
fun CryptoItem(context: Context, coin: Coin) {
    Row(
        modifier = Modifier
            .height(88.dp)
            .fillMaxWidth()
            .clickable {
                with(context) { startActivity(DetailsActivity.newIntent(context, coin)) }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp)
        ) {
            coin.image?.let {
                AsyncImage(
                    placeholder = CoinPlaceholder(R.drawable.app_icon),
                    model = ImageRequest.Builder(context).data(it).crossfade(true).build(),
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
                        text = it.uppercase(), fontWeight = FontWeight.Bold, fontSize = 18.sp
                    )
                }
                coin.name?.let { Text(text = it) }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                horizontalAlignment = AbsoluteAlignment.Right, modifier = Modifier.padding(4.dp)
            ) {
                coin.currentPrice?.let {
                    Text(
                        text = "${it.prettyFormat()} €",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                CoinVariability(coin, false)
            }
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowRight,
                contentDescription = "Details",
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}

@Preview
@Composable
fun MarketScreenPreview() {
    CoinflowTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                CryptoList(
                    context = LocalContext.current,
                    coins = listOf(
                        Coin(
                            name = "Coin 1",
                            symbol = "C1",
                            image = "",
                            currentPrice = 100000.0,
                            priceChange24h = 1000.0,
                            marketCapChangePercentage24h = 1.0
                        )
                    )
                )
            }
        }
    }
}
