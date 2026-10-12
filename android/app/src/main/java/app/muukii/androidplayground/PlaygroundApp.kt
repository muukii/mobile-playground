package app.muukii.androidplayground

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

private const val CatalogRoute = "catalog"

/**
 * Owns navigation, the back stack, and system insets for all experiments.
 *
 * Navigation Compose restores the current destination after recreation and
 * handles system Back. Samples remain ordinary composables without controllers.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentSample = playgroundSamples.firstOrNull {
        it.route == backStackEntry?.destination?.route
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentSample?.title ?: "Android Playground") },
                navigationIcon = {
                    if (currentSample != null) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_back),
                                contentDescription = stringResource(R.string.back_to_samples),
                            )
                        }
                    }
                },
            )
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = CatalogRoute,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                // Child samples can still request safeDrawingPadding without
                // applying the already-consumed system insets a second time.
                .consumeWindowInsets(padding),
        ) {
            composable(CatalogRoute) {
                PlaygroundScreen(
                    samples = playgroundSamples,
                    onOpenSample = { sample ->
                        navController.navigate(sample.route) {
                            launchSingleTop = true
                        }
                    },
                )
            }
            playgroundSamples.forEach { sample ->
                composable(sample.route) {
                    Box(Modifier.fillMaxSize().testTag("${sample.id}-screen")) {
                        sample.content()
                    }
                }
            }
        }
    }
}
