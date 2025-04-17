package it.pezzotta.coinflow.activity

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters

@HiltAndroidTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CoinFlowTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() = hiltRule.inject()

    @Test
    fun t1_loadingMarketScreen() {
        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun t2_loadMarketScreenSuccessfully() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("coin_item_bitcoin").fetchSemanticsNodes()
                .isNotEmpty()
            composeTestRule.onAllNodesWithTag("coin_item_ethereum").fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithTag("coin_item_bitcoin").assertIsDisplayed()
        composeTestRule.onNodeWithTag("coin_item_ethereum").assertIsDisplayed()
    }

    @Test
    fun t3_pullDownToRefresh() {
        // wait fo the loader indicator to disappear
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("loading_indicator").fetchSemanticsNodes().isEmpty()
        }

        // look for bitcoin item
        composeTestRule.onNodeWithTag("coin_item_bitcoin").assertIsDisplayed()

        // swipe down to refresh
        composeTestRule.onNodeWithTag("swipe_refresh").performTouchInput {
            down(center)
            moveBy(Offset(0f, 5000f))
            up()
        }

        // wait until bitcoin item appears after refresh
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("coin_item_bitcoin").fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Assert bitcoin item is now shown
        composeTestRule.onNodeWithTag("coin_item_bitcoin").assertIsDisplayed()
    }

    @Test
    fun t4_navigatesToCoinDetailScreen() {
        // wait fo the loader indicator to disappear
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("loading_indicator").fetchSemanticsNodes().isEmpty()
        }

        // click the bitcoin item
        composeTestRule.onNodeWithTag("coin_item_bitcoin").assertIsDisplayed().performClick()

        // wait until loading indicator disappears
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("loading_indicator").fetchSemanticsNodes().isEmpty()
        }

        // wait for the chart inside detail screen to load
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("coin_detail_bitcoin").fetchSemanticsNodes()
                .isNotEmpty()
        }

        // assert that the detail title is visible
        composeTestRule.onNodeWithTag("coin_detail_bitcoin").assertIsDisplayed()
    }

    @Test
    fun t5_detailsScreenDescriptionClick() {
        // wait for bitcoin item to appear
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("coin_item_bitcoin").fetchSemanticsNodes()
                .isNotEmpty()
        }

        // look for bitcoin item
        composeTestRule.onNodeWithTag("coin_item_bitcoin").assertIsDisplayed()

        // click the bitcoin item
        composeTestRule.onNodeWithTag("coin_item_bitcoin").assertIsDisplayed().performClick()

        // wait until loading indicator disappears
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("loading_indicator").fetchSemanticsNodes().isEmpty()
        }

        // wait for description item to appear
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("coin_detail_description").fetchSemanticsNodes()
                .isNotEmpty()
        }

        // look for description item
        composeTestRule.onNodeWithTag("coin_detail_description").assertIsDisplayed().performClick()
    }

    // Use Airplane mode to test the error state
    /*@Test
    fun t6_marketScreenErrorState() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("retry_button").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("retry_button").assertIsDisplayed()
    }*/
}
