package app.muukii.androidplayground

import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Verifies the catalog's actual destinations and interactive sample controls. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PlaygroundNavigationTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun everyCatalogEntryOpensAndReturnsToTheList() {
        launchPlayground()
        playgroundSamples.forEach { sample ->
            openSample(sample.id)
            compose.onNodeWithTag("${sample.id}-screen").assertIsDisplayed()
            compose.onNodeWithContentDescription("Back to samples").performClick()
            compose.onNodeWithTag("sample-list").assertIsDisplayed()
        }
    }

    @Test
    fun alignmentControlsMoveTheContentOnBothAxes() {
        launchPlayground()
        openSample("alignment")
        compose.onNodeWithTag("horizontal-end").performClick().assertIsSelected()
        compose.onNodeWithTag("vertical-bottom").performScrollTo().performClick().assertIsSelected()
        compose.onNodeWithText("Position: Bottom · End").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun animationCanReverseItsTargetState() {
        launchPlayground()
        openSample("animation")
        compose.onNodeWithText("Toggle animation").performClick()
        compose.onNodeWithText("State: Expanded").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Toggle animation").performClick()
        compose.onNodeWithText("State: Compact").performScrollTo().assertIsDisplayed()
    }

    private fun launchPlayground() {
        compose.setContent {
            PlaygroundTheme { Surface { PlaygroundApp() } }
        }
    }

    private fun openSample(id: String) {
        compose.onNodeWithTag("sample-list").performScrollToNode(hasTestTag("sample-$id"))
        compose.onNodeWithTag("sample-$id").performClick()
    }
}
