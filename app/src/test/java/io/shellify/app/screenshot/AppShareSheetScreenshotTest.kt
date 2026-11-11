package io.shellify.app.screenshot

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import io.shellify.app.domain.model.WebApp
import io.shellify.app.presentation.share.AppShareSheet
import io.shellify.app.presentation.theme.ShellifyTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AppShareSheetScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val github = WebApp(id = 1L, name = "GitHub", url = "https://github.com", isolationId = "iso-1")

    @Test
    fun default() {
        composeTestRule.setContent {
            ShellifyTheme {
                AppShareSheet(
                    appName = "GitHub",
                    appUrl = "https://github.com",
                    onDismiss = {},
                    shareWebsiteUrl = true,
                    app = github,
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = screenshotOptions)
    }

    @Test
    fun shellifyLinkMode() {
        composeTestRule.setContent {
            ShellifyTheme {
                AppShareSheet(
                    appName = "GitHub",
                    appUrl = "https://github.com",
                    onDismiss = {},
                    shareWebsiteUrl = false,
                    app = github,
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = screenshotOptions)
    }
}
