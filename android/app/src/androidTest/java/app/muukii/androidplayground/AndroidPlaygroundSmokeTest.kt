package app.muukii.androidplayground

import android.content.Intent
import android.os.SystemClock
import android.view.accessibility.AccessibilityNodeInfo
import android.view.KeyEvent
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Exercises the installed demo through Android's public accessibility actions.
 * This also verifies the tab semantics exposed to accessibility services.
 */
class AndroidPlaygroundSmokeTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val automation = instrumentation.uiAutomation

    @Test
    fun selectionFollowsInsertionRemovalAndViewportChanges() {
        launchCatalog()
        click(awaitNode { it.text?.toString() == "Scrollable segments" })
        awaitNode { it.text?.toString() == "Growing collection" }

        click(awaitNode { it.text?.toString() == "Last" })
        awaitSelected("新着")

        click(awaitNode { it.text?.toString() == "+  Add and select next tab" })
        awaitNode { it.text?.toString() == "Segments: 9" }
        awaitSelected("近くの人")

        click(awaitNode { it.contentDescription?.toString() == "Decrease segment count" })
        awaitNode { it.text?.toString() == "Segments: 8" }
        awaitSelected("新着")

        click(awaitNode { it.isCheckable })
        awaitNode { it.isCheckable && it.isChecked }
        awaitSelected("新着")
        click(awaitNode { it.isCheckable && it.isChecked })
        awaitNode { it.isCheckable && !it.isChecked }

        click(awaitNode { it.text?.toString() == "First" })
        awaitSelected("いいね！")
        click(awaitNode { it.text?.toString() == "Middle" })
        awaitSelected("お気に入り")
    }

    @Test
    fun catalogSupportsSystemBackAcrossExperimentsAndModalSheet() {
        launchCatalog()
        click(awaitNode { it.text?.toString() == "Alignment" })
        click(awaitNode { it.text?.toString() == "End" })
        click(awaitNode { it.text?.toString() == "Bottom" })
        awaitNode { it.text?.toString() == "Position: Bottom · End" }
        pressBack()
        awaitNode { it.text?.toString() == "Compose experiments" }

        click(awaitNode { it.text?.toString() == "Bottom sheet" })
        // Expanded sheets can otherwise consume Back by collapsing one step.
        click(awaitNode { it.isCheckable })
        click(awaitNode { it.text?.toString() == "Open bottom sheet" })
        awaitNode { it.text?.toString() == "A native modal sheet" }
        pressBack()
        awaitNode { it.text?.toString() == "Open bottom sheet" && it.isVisibleToUser }
        assertTrue("Sheet preference should survive dismissal", awaitNode { it.isCheckable }.isChecked)
        click(awaitNode { it.text?.toString() == "Open bottom sheet" })
        awaitNode { it.text?.toString() == "A native modal sheet" }
        click(awaitNode { it.text?.toString() == "Close sheet" && it.isVisibleToUser })
        awaitNode { it.text?.toString() == "Open bottom sheet" && it.isVisibleToUser }
        pressBack()
        awaitNode { it.text?.toString() == "Compose experiments" }

        scrollCatalogTo("Animation")
        click(awaitNode { it.text?.toString() == "Animation" })
        click(awaitNode { it.text?.toString() == "Toggle animation" })
        awaitNode { it.text?.toString() == "State: Expanded" }
        pressBack()
        awaitNode { it.text?.toString() == "Compose experiments" }
    }

    private fun launchCatalog() {
        val context = instrumentation.targetContext
        val intent = requireNotNull(context.packageManager.getLaunchIntentForPackage(context.packageName))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        awaitNode { it.text?.toString() == "Compose experiments" }
    }

    private fun pressBack() {
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
    }

    private fun scrollCatalogTo(label: String) {
        repeat(8) {
            if (findNode(automation.rootInActiveWindow) { node ->
                    node.text?.toString() == label && node.isVisibleToUser
                } != null) return
            val scrollable = awaitNode { it.isScrollable }
            assertTrue("Catalog should scroll to $label", scrollable.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD))
            SystemClock.sleep(150)
        }
        throw AssertionError("Catalog did not reveal $label")
    }

    private fun awaitSelected(label: String): AccessibilityNodeInfo = awaitNode {
        it.text?.toString() == label && hasSelectedAncestor(it) && it.isVisibleToUser
    }

    private fun hasSelectedAncestor(node: AccessibilityNodeInfo): Boolean {
        var current: AccessibilityNodeInfo? = node
        while (current != null) {
            if (current.isSelected) return true
            current = current.parent
        }
        return false
    }

    private fun click(node: AccessibilityNodeInfo) {
        var target: AccessibilityNodeInfo? = node
        while (target != null && !target.isClickable) {
            target = target.parent
        }
        assertTrue("No clickable ancestor for ${node.text}", requireNotNull(target).performAction(AccessibilityNodeInfo.ACTION_CLICK))
    }

    private fun awaitNode(predicate: (AccessibilityNodeInfo) -> Boolean): AccessibilityNodeInfo {
        val deadline = SystemClock.uptimeMillis() + 8_000
        while (SystemClock.uptimeMillis() < deadline) {
            findNode(automation.rootInActiveWindow, predicate)?.let { return it }
            SystemClock.sleep(50)
        }
        val nodes = mutableListOf<String>()
        fun describe(node: AccessibilityNodeInfo?) {
            if (node == null) return
            nodes += "${node.className} text=${node.text} description=${node.contentDescription} selected=${node.isSelected} visible=${node.isVisibleToUser}"
            for (index in 0 until node.childCount) describe(node.getChild(index))
        }
        describe(automation.rootInActiveWindow)
        throw AssertionError("Expected accessibility node did not appear.\n" + nodes.joinToString("\n"))
    }

    private fun findNode(
        node: AccessibilityNodeInfo?,
        predicate: (AccessibilityNodeInfo) -> Boolean,
    ): AccessibilityNodeInfo? {
        if (node == null) return null
        if (predicate(node)) return node
        for (index in 0 until node.childCount) {
            findNode(node.getChild(index), predicate)?.let { return it }
        }
        return null
    }
}
