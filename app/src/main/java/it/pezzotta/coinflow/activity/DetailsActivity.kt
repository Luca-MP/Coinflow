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
import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import it.pezzotta.coinflow.viewmodel.CoinViewModel
import it.pezzotta.coinflow.data.model.Coin
import it.pezzotta.coinflow.sdkEqlOrAbv33
import it.pezzotta.coinflow.ui.theme.CoinflowTheme
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.extensions.format
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.PopupProperties
import it.pezzotta.coinflow.Constants
import it.pezzotta.coinflow.R
import it.pezzotta.coinflow.common.CoinVariability
import it.pezzotta.coinflow.common.ErrorMessage
import it.pezzotta.coinflow.common.CoinPlaceholder
import it.pezzotta.coinflow.convertToItalianTime
import it.pezzotta.coinflow.data.model.CoinData
import it.pezzotta.coinflow.data.model.CoinDetails
import it.pezzotta.coinflow.data.model.CoinMarketHistory
import it.pezzotta.coinflow.data.model.Description
import it.pezzotta.coinflow.data.model.Image
import it.pezzotta.coinflow.data.model.Links
import it.pezzotta.coinflow.findMinAndMax
import it.pezzotta.coinflow.getNumberOfDays
import it.pezzotta.coinflow.noRippleClickable
import it.pezzotta.coinflow.prettyFormat
import it.pezzotta.coinflow.ui.theme.GreenChart
import it.pezzotta.coinflow.ui.theme.GreyChart
import it.pezzotta.coinflow.ui.theme.RedChart

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
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
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
                                Text(it, fontSize = 24.sp)
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
    val coinDetails = coinViewModel.coinDetails

    LaunchedEffect(coin) { coinViewModel.getCoinDetails(coin, Constants.DAYS, Constants.PRECISION) }

    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        when {
            coinDetails == null -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            coinDetails.isSuccess -> {
                coinDetails.getOrNull()?.let { CoinDetail(context, coin = coin, coinDetails = it) }
            }

            coinDetails.isFailure -> {
                Toast.makeText(
                    context, "${coinDetails.exceptionOrNull()?.message}", Toast.LENGTH_LONG
                ).show()
                ErrorMessage(coinViewModel, true, coin, Constants.DAYS, Constants.PRECISION)
            }
        }
    }
}

@Composable
fun CoinDetail(context: Context, coin: Coin, coinDetails: CoinDetails) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    placeholder = CoinPlaceholder(R.drawable.app_icon),
                    model = ImageRequest.Builder(context).data(coinDetails.coinData.image?.large)
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
                            coin.currentPrice?.prettyFormat().toString(),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = " " + stringResource(R.string.EURO),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    CoinVariability(coin, true)
                    Text(
                        "${stringResource(R.string.ATH)}: ${coin.ath?.prettyFormat()} ${
                            stringResource(
                                R.string.EURO
                            )
                        }",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            IconButton(onClick = {
                val url = coinDetails.coinData.links?.homepage?.first()
                val intent = Intent(Intent.ACTION_VIEW, url?.toUri())
                context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Rounded.Info, contentDescription = "Website"
                )
            }
        }
        CoinChart(coinDetails)
        Text(
            "${stringResource(R.string.last_update)}: ${
            coin.lastUpdated?.let { convertToItalianTime(it) }
        }", fontSize = 10.sp, modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 16.dp))
        ExpandableDescription(coin, coinDetails.coinData.description?.en ?: "")
    }
}

@Composable
fun CoinChart(coinDetails: CoinDetails) {
    val minChartValue: Double? = coinDetails.coinMarketHistory.prices?.let { prices ->
        findMinAndMax(prices).first.let { it - (it * 10) / 100 }
    }
    val maxChartValue: Double? = coinDetails.coinMarketHistory.prices?.let { prices ->
        findMinAndMax(prices).second.let { it + (it * 10) / 100 }
    }
    val firstChartValue: Double? = coinDetails.coinMarketHistory.prices?.first()?.get(1)
    val lastChartValue: Double? = coinDetails.coinMarketHistory.prices?.last()?.get(1)

    Box(modifier = Modifier.height(400.dp).testTag("coin_detail_${coinDetails.coinData.id}")) {
        minChartValue?.let { minChartValue ->
            maxChartValue?.let { maxChartValue ->
                firstChartValue?.let { firstChartValue ->
                    lastChartValue?.let { lastChartValue ->
                        LineChart(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 16.dp),
                            labelProperties = LabelProperties(
                                enabled = true,
                                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                                labels = getNumberOfDays(
                                    coinDetails.coinMarketHistory.prices.first().first().toLong()
                                ),
                            ),
                            labelHelperProperties = LabelHelperProperties(
                                textStyle = TextStyle(
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            ),
                            gridProperties = GridProperties(
                                yAxisProperties = GridProperties.AxisProperties(
                                    lineCount = 0
                                )
                            ),
                            indicatorProperties = HorizontalIndicatorProperties(
                                contentBuilder = { it.format(2) },
                                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                            ),
                            popupProperties = PopupProperties(
                                containerColor = GreyChart, contentBuilder = { it.prettyFormat().format(2) }),
                            data = remember {
                                listOf(
                                    Line(
                                        label = "${coinDetails.coinData.name}/Euro",
                                        values = coinDetails.coinMarketHistory.prices.map { it[1] },
                                        color = if (firstChartValue > lastChartValue) SolidColor(
                                            RedChart
                                        ) else SolidColor(
                                            GreenChart
                                        ),
                                        firstGradientFillColor = if (firstChartValue > lastChartValue) RedChart.copy(
                                            alpha = .5f
                                        ) else GreenChart.copy(alpha = .5f),
                                        secondGradientFillColor = Color.Transparent,
                                        strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                                        gradientAnimationDelay = 1000,
                                        drawStyle = DrawStyle.Stroke(width = 2.dp),
                                    )
                                )
                            },
                            minValue = minChartValue,
                            maxValue = maxChartValue,
                            animationMode = AnimationMode.Together(delayBuilder = { it * 500L }),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableDescription(coin: Coin, description: String) {
    val isExpanded = remember { mutableStateOf(false) }
    val maxLines = if (isExpanded.value) Int.MAX_VALUE else 5

    Column(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp)
            )
            .noRippleClickable { isExpanded.value = !isExpanded.value }
            .testTag("coin_detail_description")) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            coin.name?.let { coinName ->
                Text(
                    stringResource(R.string.what_is, coinName),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Icon(
                imageVector = if (isExpanded.value) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                contentDescription = "Details"
            )
        }
        Text(
            text = description,
            maxLines = maxLines,
            style = TextStyle(fontSize = 16.sp),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        )
    }
}

@Preview
@Composable
fun CoinDetailPreview() {
    CoinflowTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                CoinDetail(
                    context = LocalContext.current, coin = Coin(
                        name = "Coin 1",
                        symbol = "C1",
                        image = "",
                        ath = 1000000000.0,
                        currentPrice = 100000.0,
                        priceChange24h = 1000.0,
                        priceChangePercentage24h = 1.0,
                        lastUpdated = "2008-10-31T23:00:00.000Z"
                    ), coinDetails = CoinDetails(
                        coinData = CoinData(
                            name = "Coin 1",
                            description = Description(en = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."),
                            image = Image(large = ""),
                            links = Links(homepage = listOf(""))
                        ), coinMarketHistory = CoinMarketHistory(
                            prices = listOf(
                                listOf(1744375620000.0, 90000.00),
                                listOf(1743779376258.0, 100000.00),
                            )
                        )
                    )
                )
            }
        }
    }
}
