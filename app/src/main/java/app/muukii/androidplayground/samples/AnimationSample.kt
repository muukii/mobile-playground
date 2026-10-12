package app.muukii.androidplayground.samples

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.muukii.androidplayground.PlaygroundTheme

/** Animates two properties from one state without running an idle animation. */
@Composable
fun AnimationSample() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val size by animateDpAsState(
        targetValue = if (expanded) 168.dp else 88.dp,
        animationSpec = spring(dampingRatio = 0.65f),
        label = "Sample size",
    )
    val color by animateColorAsState(
        targetValue = if (expanded) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
        label = "Sample color",
    )

    SampleLayout("Toggle one state to animate size and color together. Tap again mid-animation to change its destination.") {
        Box(
            modifier = Modifier.fillMaxWidth().height(240.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.size(size).background(color, RoundedCornerShape(28.dp)))
        }
        Button(onClick = { expanded = !expanded }) { Text("Toggle animation") }
        Text(
            "State: ${if (expanded) "Expanded" else "Compact"}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.testTag("animation-state"),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnimationPreview() {
    PlaygroundTheme { AnimationSample() }
}
