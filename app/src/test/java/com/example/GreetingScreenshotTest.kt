package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.StorageQuotaSummary
import com.example.ui.components.StorageDonutChart
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleSummary = StorageQuotaSummary(
      totalUsedBytes = 12_870_000_000L,
      totalAllocatedBytes = 17_000_000_000L,
      usedPercentage = 75.7f,
      googleDriveUsedBytes = 11_420_000_000L,
      googleDriveTotalBytes = 15_000_000_000L,
      dropboxUsedBytes = 1_450_000_000L,
      dropboxTotalBytes = 2_000_000_000L,
      telegramTotalUploadedBytes = 8_750_000_000L,
      totalFilesCount = 8,
      activeProvidersCount = 3
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        StorageDonutChart(summary = sampleSummary)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
