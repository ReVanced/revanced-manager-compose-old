package app.revanced.manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import app.revanced.manager.preferences.PreferencesManager
import app.revanced.manager.ui.navigation.AppDestination
import app.revanced.manager.ui.screen.MainDashboardScreen
import app.revanced.manager.ui.screen.subscreens.AppSelectorSubscreen
import app.revanced.manager.ui.screen.subscreens.PatchesSelectorSubscreen
import app.revanced.manager.ui.theme.ReVancedManagerTheme
import app.revanced.manager.ui.theme.Theme
import com.xinto.taxi.Taxi
import com.xinto.taxi.rememberBackstackNavigator
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val prefs: PreferencesManager by inject()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            ReVancedManagerTheme(
                dynamicColor = prefs.dynamicColor,
                darkTheme = prefs.theme == Theme.SYSTEM && isSystemInDarkTheme() || prefs.theme == Theme.DARK,
            ) {
                val navigator = rememberBackstackNavigator<AppDestination>(AppDestination.Dashboard)

                BackHandler {
                    if (!navigator.pop()) finish()
                }

                Taxi(
                    modifier = Modifier.fillMaxSize(),
                    navigator = navigator,
                    transitionSpec = { fadeIn() with fadeOut() }
                ) { destination ->
                    when (destination) {
                        is AppDestination.Dashboard -> MainDashboardScreen(navigator = navigator)
                        is AppDestination.AppSelector -> AppSelectorSubscreen(
                            navigator = navigator
                        )
                        is AppDestination.PatchSelector -> PatchesSelectorSubscreen(navigator = navigator)
                    }
                }
            }
        }
    }
}