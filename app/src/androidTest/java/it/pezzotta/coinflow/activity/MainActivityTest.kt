package it.pezzotta.coinflow.activity

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainActivityTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() = hiltRule.inject()

    @Test
    fun marketScreenLoadingState() {
        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun marketScreenSuccessState() {
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithTag("coin_item_bitcoin").fetchSemanticsNodes()
                .isNotEmpty()
            composeTestRule.onAllNodesWithTag("coin_item_ethereum").fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithTag("coin_item_bitcoin").assertIsDisplayed()
        composeTestRule.onNodeWithTag("coin_item_ethereum").assertIsDisplayed()
    }

    // Use Airplane mode to test the error state
    @Test
    fun marketScreenErrorState() {
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithTag("retry_button").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("retry_button").assertIsDisplayed()
    }
}
