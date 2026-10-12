package app.muukii.androidplayground.samples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.muukii.androidplayground.PlaygroundTheme
import kotlinx.coroutines.launch

/** Exercises native sheet presentation, expansion, and dismissal with Back. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetSample() {
    var showSheet by rememberSaveable { mutableStateOf(false) }
    var skipPartiallyExpanded by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)
    val scope = rememberCoroutineScope()

    SampleLayout("Open a modal sheet, drag it between positions, or dismiss it with Back. Change its starting behavior below.") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Skip partially expanded", modifier = Modifier.weight(1f))
            Switch(
                checked = skipPartiallyExpanded,
                onCheckedChange = { skipPartiallyExpanded = it },
                modifier = Modifier.testTag("skip-partially-expanded"),
            )
        }
        Button(onClick = { showSheet = true }) { Text("Open bottom sheet") }
    }

    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }, sheetState = sheetState) {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Text("A native modal sheet", style = MaterialTheme.typography.headlineSmall)
                Text("The sheet owns Back while it is open. Dismissing it returns to this experiment, keeping its controls in place.")
                // Enough content to offer a partial expansion on phone screens.
                repeat(4) { index ->
                    Text("Item ${index + 1}", modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp))
                }
                Button(onClick = {
                    scope.launch {
                        sheetState.hide()
                        if (!sheetState.isVisible) showSheet = false
                    }
                }) { Text("Close sheet") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetPreview() {
    PlaygroundTheme { BottomSheetSample() }
}
