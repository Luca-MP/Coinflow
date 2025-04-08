package it.pezzotta.coinflow.activity

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Row
import kotlin.collections.listOf
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import it.pezzotta.coinflow.ui.theme.CoinflowTheme
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
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
                        title = {
                            Text(
                                "Coinflow",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                    )
                }) { innerPadding ->
                    CryptoList(
                        cryptos = listOf(
                            Crypto("Bitcoin", 1, 25000.0),
                            Crypto("Ethereum", 2, 1500.0),
                            Crypto("Litecoin", 3, 100.0),
                        ),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CryptoList(cryptos: List<Crypto>, modifier: Modifier) {
    LazyColumn (modifier = modifier) {
        items(cryptos) { crypto ->
            CryptoItem(crypto)
        }
    }
}

@Parcelize
data class Crypto(var name: String, var image: Int, var price: Double) : Parcelable

@Composable
fun CryptoItem(crypto: Crypto) {
    val context = LocalContext.current
    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = crypto.name, modifier = Modifier.padding(horizontal = 8.dp))
        Text(text = crypto.price.toString())
        IconButton(onClick = { with(context) {
            startActivity(DetailsActivity.newIntent(context, crypto))
        } }) {
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