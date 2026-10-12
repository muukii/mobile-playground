package app.muukii.androidplayground

import androidx.compose.runtime.Composable
import app.muukii.androidplayground.samples.AlignmentSample
import app.muukii.androidplayground.samples.AnimationSample
import app.muukii.androidplayground.samples.BottomSheetSample

/**
 * Describes an independently runnable experiment in the playground.
 *
 * [id] is a stable, unique route component. Keep it unchanged when renaming the
 * displayed [title] so Android can restore an open experiment after recreation.
 * [content] owns the experiment's state; navigation belongs to the host.
 */
data class Sample(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val content: @Composable () -> Unit,
) {
    /** The destination used by the catalog's navigation graph. */
    val route: String get() = "sample/$id"
}

/** Add a sample here to make it available in both the catalog and navigation. */
val playgroundSamples: List<Sample> = listOf(
    Sample(
        id = "segmented-control",
        title = "Scrollable segments",
        category = "Controls",
        description = "Explore selection, growing tabs, and a narrow viewport.",
        content = { SegmentedControlPlayground() },
    ),
    Sample(
        id = "alignment",
        title = "Alignment",
        category = "Layout",
        description = "Move content through the nine positions of a Box.",
        content = { AlignmentSample() },
    ),
    Sample(
        id = "bottom-sheet",
        title = "Bottom sheet",
        category = "Presentation",
        description = "Present a modal sheet and compare its expansion behavior.",
        content = { BottomSheetSample() },
    ),
    Sample(
        id = "animation",
        title = "Animation",
        category = "Motion",
        description = "Animate size and color together with a state change.",
        content = { AnimationSample() },
    ),
)
