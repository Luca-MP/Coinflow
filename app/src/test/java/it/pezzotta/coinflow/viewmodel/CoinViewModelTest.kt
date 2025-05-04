import it.pezzotta.coinflow.data.model.Coin
import it.pezzotta.coinflow.data.model.CoinData
import it.pezzotta.coinflow.data.model.CoinDetails
import it.pezzotta.coinflow.data.model.CoinMarketHistory
import it.pezzotta.coinflow.data.repository.CoinRepository
import it.pezzotta.coinflow.ui.state.CoinDetailsState
import it.pezzotta.coinflow.ui.state.CoinMarketState
import it.pezzotta.coinflow.viewmodel.CoinViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class CoinViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val json = Json { ignoreUnknownKeys = true }

    private lateinit var coinRepository: CoinRepository
    private lateinit var coinViewModel: CoinViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coinRepository = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getCoinMarket should update coinMarket state with success result`() = testScope.runTest {
        val market = javaClass.classLoader?.getResourceAsStream("market_response.json")
            ?: throw AssertionError("Resource not found: market_response.json")
        val marketJson = market.bufferedReader().use { it.readText() }
        val marketData = json.decodeFromString<List<Coin>>(marketJson)
        val marketResult = CoinMarketState.Success(marketData)

        doReturn(marketResult).`when`(coinRepository).getCoinMarket()
        coinViewModel = CoinViewModel(coinRepository)

        advanceUntilIdle()
        val actualResult = coinViewModel.coinMarketState

        assertEquals(marketResult, actualResult)

        verify(coinRepository).getCoinMarket()
    }

    @Test
    fun `getCoinMarket should update coinMarket state with failure result`() = testScope.runTest {
        val exception = Exception("Error 429:")
        val marketResult = CoinMarketState.Error(exception)

        doReturn(marketResult).`when`(coinRepository).getCoinMarket()
        coinViewModel = CoinViewModel(coinRepository)

        advanceUntilIdle()
        val actualResult = coinViewModel.coinMarketState

        assertTrue(actualResult is CoinMarketState.Error)
        assertEquals(marketResult, actualResult)

        verify(coinRepository).getCoinMarket()
    }

    @Test
    fun `getCoinDetails should update coinDetails state with success result`() = testScope.runTest {
        val market = javaClass.classLoader?.getResourceAsStream("market_response.json")
            ?: throw AssertionError("Resource not found: market_response.json")
        val marketJson = market.bufferedReader().use { it.readText() }
        val marketData = Json.decodeFromString<List<Coin>>(marketJson)

        val coinData = javaClass.classLoader?.getResourceAsStream("coin_data_response.json")
            ?: throw AssertionError("Resource not found: coin_data_response.json")
        val coinDataJson = coinData.bufferedReader().use { it.readText() }
        val coinDataData = json.decodeFromString<CoinData>(coinDataJson)

        val coinMarketHistory = javaClass.classLoader?.getResourceAsStream("coin_market_history_response.json")
            ?: throw AssertionError("Resource not found: coin_market_history_response.json")
        val coinMarketHistoryJson = coinMarketHistory.bufferedReader().use { it.readText() }
        val coinMarketHistoryData = json.decodeFromString<CoinMarketHistory>(coinMarketHistoryJson)

        val coinDetailsResult = CoinDetailsState.Success(CoinDetails(coinDataData, coinMarketHistoryData))
        val days = 7
        val precision = 8

        doReturn(coinDetailsResult).`when`(coinRepository).getCoinDetails(marketData.first(), days, precision)
        coinViewModel = CoinViewModel(coinRepository)
        coinViewModel.getCoinDetails(false, marketData.first(), days, precision)

        advanceUntilIdle()
        val actualResult = coinViewModel.coinDetailsState

        assertEquals(coinDetailsResult, actualResult)

        verify(coinRepository).getCoinDetails(marketData.first(), days, precision)
    }

    @Test
    fun `getCoinDetails should update coinDetails state with failure result`() = testScope.runTest {
        val exception = Exception("Error 429:")
        val coinDetailsResult = CoinDetailsState.Error(exception)
        val days = 7
        val precision = 2

        doReturn(coinDetailsResult).`when`(coinRepository).getCoinDetails(Coin(), days, precision)
        coinViewModel = CoinViewModel(coinRepository)
        coinViewModel.getCoinDetails(false, Coin(), days, precision)

        advanceUntilIdle()
        val actualResult = coinViewModel.coinDetailsState

        assertTrue(actualResult is CoinDetailsState.Error)
        assertEquals(coinDetailsResult, actualResult)

        verify(coinRepository).getCoinDetails(Coin(), days, precision)
    }
}
