package io.shellify.app.mock.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.shellify.app.domain.model.WebApp
import io.shellify.app.presentation.share.AppShareSheet
import io.shellify.app.presentation.theme.ShellifyTheme
import io.shellify.core.ui.R as CoreUiR
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppShareSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val app = WebApp(id = 1L, name = "DuckAI", url = "https://duck.ai", isolationId = "iso-1")

    @Test
    fun websiteUrlMode_showsOriginalUrlAndHidesQrAndAppLink() {
        composeTestRule.setContent {
            ShellifyTheme {
                AppShareSheet(
                    appName = app.name,
                    appUrl = app.url,
                    onDismiss = {},
                    shareWebsiteUrl = true,
                    app = app,
                )
            }
        }
        composeTestRule.onNodeWithText("DuckAI").assertIsDisplayed()
        composeTestRule.onNodeWithText("https://duck.ai").assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(CoreUiR.string.share_copy_link)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(CoreUiR.string.share_button)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(CoreUiR.string.share_copy_app_link)).assertDoesNotExist()
    }

    @Test
    fun shellifyLinkMode_showsCopyAppLink() {
        composeTestRule.setContent {
            ShellifyTheme {
                AppShareSheet(
                    appName = app.name,
                    appUrl = app.url,
                    onDismiss = {},
                    shareWebsiteUrl = false,
                    app = app,
                )
            }
        }
        composeTestRule.onNodeWithText(context.getString(CoreUiR.string.share_copy_app_link)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(CoreUiR.string.share_copy_link)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(CoreUiR.string.share_button)).assertIsDisplayed()
        composeTestRule.onNodeWithText("https://duck.ai").assertDoesNotExist()
    }
}
