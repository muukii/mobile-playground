package app.muukii.androidplayground

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateRectAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.first
import kotlin.math.roundToInt

/** A segment whose identity remains stable when its label or position changes. */
data class SegmentItem<Id : Any>(val id: Id, val label: String)

/**
 * A filled, capsule-shaped tab selector that keeps the selected item in view.
 *
 * Labels share equal widths when they fit; overflowing labels keep their natural
 * widths and scroll horizontally. Taps report a new ID to [onSelected], while
 * dragging only scrolls. The parent owns [selectedId] and keeps it valid when
 * removing items. IDs must be unique and stable across updates.
 *
 * This eager layout is intended for a small set of navigation tabs, not a feed.
 * Each label is measured once after querying its intrinsic width.
 *
 * @param items Ordered, already-localized labels and their stable identities.
 * @param selectedId The currently selected item's identity.
 * @param onSelected Called when a different segment is tapped.
 * @param modifier Applied to the visible viewport, not the overflowing content.
 * @param selectionColor Fill of the moving selection capsule.
 * @param selectedContentColor Foreground on the selection capsule.
 * @param containerColor Background of the shared track.
 */
@Composable
fun <Id : Any> ScrollableSegmentedControl(
    items: List<SegmentItem<Id>>,
    selectedId: Id,
    onSelected: (Id) -> Unit,
    modifier: Modifier = Modifier,
    selectionColor: Color = MaterialTheme.colorScheme.primary,
    selectedContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
) {
    if (items.isEmpty()) return

    val scrollState = rememberScrollState()
    val capsule = RoundedCornerShape(percent = 50)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(capsule)
            .background(containerColor),
    ) {
        val viewportWidth = constraints.maxWidth
        SubcomposeLayout(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .selectableGroup(),
        ) { constraints ->
            val inset = 4.dp.roundToPx()
            val measurables = subcompose(SegmentSlot.Tabs) {
                items.forEach { item ->
                    key(item.id) {
                        SegmentTab(
                            label = item.label,
                            selected = item.id == selectedId,
                            selectedContentColor = selectedContentColor,
                            onClick = {
                                if (item.id != selectedId) onSelected(item.id)
                            },
                        )
                    }
                }
            }

            val naturalWidths = measurables.map { it.maxIntrinsicWidth(Constraints.Infinity) }
            val availableWidth = (viewportWidth - inset * 2).coerceAtLeast(0)
            val rowWidth = maxOf(availableWidth, naturalWidths.sum())
            val equalWidth = rowWidth / items.size
            val fitsEqually = naturalWidths.all { it <= equalWidth }
            val extra = rowWidth - naturalWidths.sum()
            val widths = naturalWidths.mapIndexed { index, naturalWidth ->
                if (fitsEqually) {
                    equalWidth + if (index < rowWidth % items.size) 1 else 0
                } else {
                    naturalWidth + extra / items.size + if (index < extra % items.size) 1 else 0
                }
            }
            val placeables = measurables.mapIndexed { index, measurable ->
                measurable.measure(
                    Constraints(
                        minWidth = widths[index],
                        maxWidth = widths[index],
                        maxHeight = constraints.maxHeight,
                    ),
                )
            }
            val rowHeight = placeables.maxOf { it.height }
            val contentWidth = rowWidth + inset * 2
            val contentHeight = rowHeight + inset * 2
            val selectedIndex = items.indexOfFirst { it.id == selectedId }
            val indicator = if (selectedIndex >= 0) {
                val logicalLeft = inset + widths.take(selectedIndex).sum()
                val selectedWidth = widths[selectedIndex]
                val physicalLeft = when (layoutDirection) {
                    LayoutDirection.Ltr -> logicalLeft
                    LayoutDirection.Rtl -> contentWidth - logicalLeft - selectedWidth
                }
                val bounds = Rect(
                    left = physicalLeft.toFloat(),
                    top = inset.toFloat(),
                    right = (physicalLeft + selectedWidth).toFloat(),
                    bottom = (inset + rowHeight).toFloat(),
                )
                val scrollRange = (contentWidth - viewportWidth).coerceAtLeast(0)
                val scrollTarget = (logicalLeft + selectedWidth / 2f - viewportWidth / 2f)
                    .roundToInt()
                    .coerceIn(0, scrollRange)

                subcompose(SegmentSlot.ScrollEffect) {
                    RevealSelectedSegment(selectedId, scrollTarget, scrollRange, scrollState)
                }
                subcompose(SegmentSlot.Indicator) {
                    SegmentIndicator(bounds, selectionColor)
                }.single().measure(Constraints.fixed(contentWidth, contentHeight))
            } else {
                null
            }

            layout(contentWidth, contentHeight) {
                indicator?.place(0, 0)
                var x = inset
                placeables.forEachIndexed { index, placeable ->
                    placeable.placeRelative(x, inset + (rowHeight - placeable.height) / 2)
                    x += widths[index]
                }
            }
        }
    }
}

/** Waits for fresh scroll bounds before revealing an inserted or resized tab. */
@Composable
private fun RevealSelectedSegment(
    selectedId: Any,
    target: Int,
    scrollRange: Int,
    scrollState: ScrollState,
) {
    var hasRevealedSelection by remember { mutableStateOf(false) }
    LaunchedEffect(selectedId, target, scrollRange) {
        // horizontalScroll publishes its range after measuring this content.
        // Waiting avoids clamping a new last tab to the previous content width.
        snapshotFlow { scrollState.maxValue }.first { it == scrollRange }
        if (hasRevealedSelection) {
            scrollState.animateScrollTo(target)
        } else {
            scrollState.scrollTo(target)
            hasRevealedSelection = true
        }
    }
}

/** Draws beneath the labels so the selection never intercepts touch input. */
@Composable
private fun SegmentIndicator(bounds: Rect, color: Color) {
    val animatedBounds by animateRectAsState(
        targetValue = bounds,
        animationSpec = spring(),
        label = "Selected segment bounds",
    )
    Canvas(Modifier.fillMaxSize()) {
        drawRoundRect(
            color = color,
            topLeft = Offset(animatedBounds.left, animatedBounds.top),
            size = Size(animatedBounds.width, animatedBounds.height),
            cornerRadius = CornerRadius(animatedBounds.height / 2),
        )
    }
}

/** A selectable tab with a native ripple and a minimum 48 dp touch target. */
@Composable
private fun SegmentTab(
    label: String,
    selected: Boolean,
    selectedContentColor: Color,
    onClick: () -> Unit,
) {
    val contentColor by animateColorAsState(
        targetValue = if (selected) selectedContentColor else MaterialTheme.colorScheme.onSurface,
        label = "Segment label color",
    )
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .heightIn(min = 48.dp)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            softWrap = false,
        )
    }
}

private enum class SegmentSlot { Tabs, Indicator, ScrollEffect }
