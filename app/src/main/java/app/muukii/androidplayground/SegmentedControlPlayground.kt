package app.muukii.androidplayground

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** An interactive sample for testing selection, tab growth, and viewport changes. */
@Composable
fun SegmentedControlPlayground(modifier: Modifier = Modifier) {
    var twoItemSelection by rememberSaveable { mutableStateOf("visitors") }
    var selection by rememberSaveable { mutableStateOf("visitors") }
    var itemCount by rememberSaveable { mutableIntStateOf(8) }
    var narrowViewport by rememberSaveable { mutableStateOf(false) }
    val items = SampleSegments.take(itemCount)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Scrollable segments", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text(
                "A filled selection that stays in view as the number of tabs grows.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Two tabs", style = MaterialTheme.typography.titleMedium)
            ScrollableSegmentedControl(
                items = SampleSegments.take(2),
                selectedId = twoItemSelection,
                onSelected = { twoItemSelection = it },
                modifier = Modifier.testTag("two-segments"),
            )
        }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text("Growing collection", style = MaterialTheme.typography.titleMedium)
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    val widthModifier = if (narrowViewport) Modifier.widthIn(max = 240.dp) else Modifier
                    ScrollableSegmentedControl(
                        items = items,
                        selectedId = selection,
                        onSelected = { selection = it },
                        modifier = widthModifier.testTag("growing-segments"),
                    )
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Selected", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        items.first { it.id == selection }.label,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.testTag("selected-segment"),
                    )
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("First" to 0, "Middle" to items.size / 2, "Last" to items.lastIndex).forEach { (title, index) ->
                        TextButton(
                            onClick = { selection = items[index].id },
                            colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        ) { Text(title) }
                    }
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Segments: $itemCount", modifier = Modifier.testTag("segment-count"))
                    Row {
                        IconButton(
                            enabled = itemCount > 2,
                            onClick = {
                                itemCount -= 1
                                if (SampleSegments.take(itemCount).none { it.id == selection }) {
                                    selection = SampleSegments[itemCount - 1].id
                                }
                            },
                            modifier = Modifier.semantics { contentDescription = "Decrease segment count" },
                        ) { Text("−", style = MaterialTheme.typography.titleLarge) }
                        IconButton(
                            enabled = itemCount < SampleSegments.size,
                            onClick = { itemCount += 1 },
                            modifier = Modifier.semantics { contentDescription = "Increase segment count" },
                        ) { Text("+", style = MaterialTheme.typography.titleLarge) }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Narrow viewport")
                    Switch(
                        checked = narrowViewport,
                        onCheckedChange = { narrowViewport = it },
                        modifier = Modifier.testTag("narrow-viewport"),
                    )
                }
                TextButton(
                    enabled = itemCount < SampleSegments.size,
                    onClick = {
                        itemCount += 1
                        selection = SampleSegments[itemCount - 1].id
                    },
                    modifier = Modifier.testTag("add-and-select"),
                ) { Text("+  Add and select next tab") }
            }
        }
    }
}

private val SampleSegments = listOf(
    SegmentItem("likes", "いいね！"),
    SegmentItem("visitors", "足あと"),
    SegmentItem("matches", "マッチング"),
    SegmentItem("messages", "メッセージ"),
    SegmentItem("favorites", "お気に入り"),
    SegmentItem("recommended", "おすすめ"),
    SegmentItem("online", "オンライン"),
    SegmentItem("new", "新着"),
    SegmentItem("nearby", "近くの人"),
    SegmentItem("profile", "プロフィール"),
    SegmentItem("notifications", "お知らせ"),
    SegmentItem("all", "すべて"),
)

@Preview(name = "Scrollable segmented control", showBackground = true)
@Preview(name = "Large text", showBackground = true, fontScale = 1.5f)
@Composable
private fun SegmentedControlPreview() {
    PlaygroundTheme {
        Surface { SegmentedControlPlayground() }
    }
}
