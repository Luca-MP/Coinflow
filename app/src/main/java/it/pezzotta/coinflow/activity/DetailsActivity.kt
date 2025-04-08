package it.pezzotta.coinflow.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import dagger.hilt.android.AndroidEntryPoint
import it.pezzotta.coinflow.sdkEqlOrAbv33
import it.pezzotta.coinflow.ui.theme.CoinflowTheme

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    companion object {
        private const val CRYPTO = "CRYPTO"
        fun newIntent(context: Context, crypto: Crypto) =
            Intent(context, DetailsActivity::class.java).putExtra(CRYPTO, crypto)
    }

    private val crypto: Crypto by lazy {
        if (sdkEqlOrAbv33()) {
            intent.getParcelableExtra(CRYPTO, Crypto::class.java) as Crypto
        } else {
            val crypto: Crypto = intent.getParcelableExtra(CRYPTO)!!
            crypto
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
                        title = {
                            Text(
                                crypto.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                    )
                }) { innerPadding ->
                    Text("Detail", modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}