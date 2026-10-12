package app.muukii.androidplayground

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Verifies observable selection and viewport behavior against actual layout. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ScrollableSegmentedControlTest {
    @get:Rule
    val compose = createComposeRule()

    private val tabs = (1..10).map { SegmentItem(it, "Category $it") }

    @Test
    fun fittingTabsHaveEqualWidthsAndTapsUpdateSelection() {
        val selected = mutableStateOf(1)
        compose.setContent {
            PlaygroundTheme {
                Surface {
                    Box(Modifier.width(320.dp)) {
                        ScrollableSegmentedControl(
                            items = listOf(SegmentItem(1, "Likes"), SegmentItem(2, "Visitors")),
                            selectedId = selected.value,
                            onSelected = { selected.value = it },
                            modifier = Modifier.testTag("control"),
                        )
                    }
                }
            }
        }
        val first = compose.onNodeWithText("Likes").getUnclippedBoundsInRoot()
        val second = compose.onNodeWithText("Visitors").getUnclippedBoundsInRoot()
        assertEquals((first.right - first.left).value, (second.right - second.left).value, 1f)
        compose.onNodeWithText("Visitors").performClick().assertIsSelected()
        compose.runOnIdle { assertEquals(2, selected.value) }
    }

    @Test
    fun externalSelectionRevealsTheLastTab() {
        val selected = mutableStateOf(1)
        compose.setContent {
            PlaygroundTheme {
                Box(Modifier.width(280.dp)) {
                    ScrollableSegmentedControl(tabs, selected.value, { selected.value = it }, Modifier.testTag("control"))
                }
            }
        }
        compose.runOnIdle { selected.value = 10 }
        compose.onNodeWithText("Category 10").assertIsSelected()
        assertFullyVisible("Category 10")
    }

    @Test
    fun insertingASelectedTabAndResizingRevealsItsFinalBounds() {
        val items = mutableStateOf(tabs.take(8))
        val selected = mutableStateOf(1)
        val width = mutableStateOf(320.dp)
        compose.setContent {
            PlaygroundTheme {
                Box(Modifier.width(width.value)) {
                    ScrollableSegmentedControl(items.value, selected.value, { selected.value = it }, Modifier.testTag("control"))
                }
            }
        }
        compose.runOnIdle {
            items.value = tabs
            selected.value = 10
        }
        assertFullyVisible("Category 10")
        compose.runOnIdle { width.value = 200.dp }
        assertFullyVisible("Category 10")
    }

    @Test
    fun draggingDoesNotChangeSelection() {
        val selected = mutableStateOf(1)
        compose.setContent {
            PlaygroundTheme {
                Box(Modifier.width(280.dp)) {
                    ScrollableSegmentedControl(tabs, selected.value, { selected.value = it }, Modifier.testTag("control"))
                }
            }
        }
        compose.onNodeWithTag("control").performTouchInput { swipeLeft() }
        compose.runOnIdle { assertEquals(1, selected.value) }
    }

    @Test
    fun rightToLeftSelectionUsesLogicalScrollCoordinates() {
        val selected = mutableStateOf(1)
        compose.setContent {
            PlaygroundTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Box(Modifier.width(280.dp)) {
                        ScrollableSegmentedControl(tabs, selected.value, { selected.value = it }, Modifier.testTag("control"))
                    }
                }
            }
        }
        compose.runOnIdle { selected.value = 10 }
        compose.onNodeWithText("Category 10").assertIsSelected()
        assertFullyVisible("Category 10")
    }

    private fun assertFullyVisible(label: String) {
        compose.waitForIdle()
        compose.onNodeWithText(label).assertIsDisplayed()
        val tab = compose.onNodeWithText(label).getUnclippedBoundsInRoot()
        val viewport = compose.onNodeWithTag("control").getUnclippedBoundsInRoot()
        assertTrue("$label starts outside the viewport: $tab / $viewport", tab.left >= viewport.left)
        assertTrue("$label ends outside the viewport: $tab / $viewport", tab.right <= viewport.right)
    }
}
