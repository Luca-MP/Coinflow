package it.pezzotta.coinflow.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dagger.hilt.android.AndroidEntryPoint
import it.pezzotta.coinflow.CoinViewModel
import it.pezzotta.coinflow.data.model.Coin
import it.pezzotta.coinflow.data.model.CoinDetail
import it.pezzotta.coinflow.sdkEqlOrAbv33
import it.pezzotta.coinflow.ui.theme.CoinflowTheme
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.foundation.layout.height
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line
import it.pezzotta.coinflow.common.CoinVariability

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {
    private val coinViewModel: CoinViewModel by viewModels()

    companion object {
        private const val COIN = "COIN"
        fun newIntent(context: Context, coin: Coin) =
            Intent(context, DetailsActivity::class.java).putExtra(COIN, coin)
    }

    private val coin: Coin by lazy {
        if (sdkEqlOrAbv33()) {
            intent.getParcelableExtra(COIN, Coin::class.java) as Coin
        } else {
            val coin: Coin = intent.getParcelableExtra(COIN)!!
            coin
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoinflowTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    CenterAlignedTopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        navigationIcon = {
                            IconButton(onClick = { finish() }) {
                                Icon(
                                    Icons.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        title = {
                            coin.name?.let {
                                Text(
                                    it,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        },
                    )
                }) { innerPadding ->
                    CoinDetailScreen(innerPadding, coinViewModel, coin)
                }
            }
        }
    }
}

@Composable
fun CoinDetailScreen(innerPadding: PaddingValues, coinViewModel: CoinViewModel, coin: Coin) {
    val context = LocalContext.current
    LaunchedEffect(coin) {
        coinViewModel.getCoinData(coin)
    }
    val coinDetails by coinViewModel.coinData.collectAsState(initial = null)
    val result = coinDetails

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
                CoinDetails(context, coin = coin, coinDetail = result.getOrNull())
            }

            result.isFailure -> {
                Toast.makeText(
                    context,
                    "Error: ${result.exceptionOrNull()?.message}",
                    Toast.LENGTH_LONG
                ).show()
                Text(
                    text = "Ops! Something went wrong",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun CoinDetails(context: Context, coin: Coin, coinDetail: CoinDetail?) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(scrollState)) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(coinDetail?.image?.large)
                        .crossfade(true).build(),
                    contentDescription = "Coin image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(24.dp))
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "%.2f".format(coin.currentPrice),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(" EUR", style = MaterialTheme.typography.bodyMedium)
                    }
                    CoinVariability(coin)
                    Text(
                        "ATH: ${"%.2f".format(coin.ath)} EUR",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            IconButton(onClick = {
                val url = coinDetail?.links?.homepage?.first()
                val intent = Intent(Intent.ACTION_VIEW, url?.toUri())
                context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Rounded.Info, contentDescription = "Website"
                )
            }
        }
        CoinChart(coinDetail)
        ExpandableDescription(coinDetail?.description?.en!!)
    }
}

@Composable
fun CoinChart(coinDetail: CoinDetail?) {
    Box(modifier = Modifier.height(400.dp)) {
        LineChart(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            data = remember {
                listOf(
                    Line(
                        label = "${coinDetail?.name!!}/Euro",
                        values = listOf(
                            75695.38,
                            75301.41,
                            76509.59,
                            76275.9,
                            71140.7,
                            72531.24,
                            69491.45,
                            75900.74
                        ),
                        color = SolidColor(Color(0xFF23af92)),
                        firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                        secondGradientFillColor = Color.Transparent,
                        strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                        gradientAnimationDelay = 1000,
                        drawStyle = DrawStyle.Stroke(width = 2.dp),
                    )
                )
            },
            minValue = 60000.0,
            maxValue = 80000.0,
            animationMode = AnimationMode.Together(delayBuilder = { it * 500L }),
        )
    }
}

@Composable
fun ExpandableDescription(content: String) {
    val isExpanded = remember { mutableStateOf(false) }
    val maxLines = if (isExpanded.value) Int.MAX_VALUE else 5

    Column {
        Text(
            text = content,
            maxLines = maxLines,
            style = TextStyle(fontSize = 16.sp),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        TextButton(
            modifier = Modifier.align(Alignment.End),
            onClick = { isExpanded.value = !isExpanded.value }) {
            Text(if (isExpanded.value) "Show Less" else "Read More")
        }
    }
}
