package app.muukii.androidplayground.samples

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.muukii.androidplayground.PlaygroundTheme

/** Demonstrates logical start/end alignment and saveable control state. */
@Composable
fun AlignmentSample() {
    var horizontal by rememberSaveable { mutableIntStateOf(1) }
    var vertical by rememberSaveable { mutableIntStateOf(1) }
    val positions = listOf(
        listOf(Alignment.TopStart, Alignment.TopCenter, Alignment.TopEnd),
        listOf(Alignment.CenterStart, Alignment.Center, Alignment.CenterEnd),
        listOf(Alignment.BottomStart, Alignment.BottomCenter, Alignment.BottomEnd),
    )
    val horizontalLabels = listOf("Start", "Center", "End")
    val verticalLabels = listOf("Top", "Center", "Bottom")

    SampleLayout("Change each axis to see how Box places its content. Start and End follow the layout direction.") {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(24.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                .padding(16.dp),
            contentAlignment = positions[vertical][horizontal],
        ) {
            Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(16.dp)) {
                Text("Compose", modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp))
            }
        }
        AlignmentControls("Horizontal", horizontalLabels, horizontal) { horizontal = it }
        AlignmentControls("Vertical", verticalLabels, vertical) { vertical = it }
        Text(
            "Position: ${verticalLabels[vertical]} · ${horizontalLabels[horizontal]}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.testTag("alignment-position"),
        )
    }
}

/** Presents one independently selectable alignment axis. */
@Composable
private fun AlignmentControls(title: String, labels: List<String>, selected: Int, onSelect: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            labels.forEachIndexed { index, label ->
                FilterChip(
                    selected = selected == index,
                    onClick = { onSelect(index) },
                    label = { Text(label) },
                    modifier = Modifier.testTag("${title.lowercase()}-${label.lowercase()}"),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AlignmentPreview() {
    PlaygroundTheme { AlignmentSample() }
}
