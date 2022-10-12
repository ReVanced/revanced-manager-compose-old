package app.revanced.manager

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.PowerManager
import android.provider.Settings
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
import app.revanced.manager.ui.screen.subscreens.*
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
        permissions()
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
                        is AppDestination.AppSelector -> AppSelectorSubscreen(navigator = navigator)
                        is AppDestination.PatchSelector -> PatchesSelectorSubscreen(navigator = navigator)
                        is AppDestination.Contributors -> ContributorsSubscreen(navigator = navigator)
                        is AppDestination.SourceSelector -> SourceSelectorSubscreen(navigator = navigator)
                        is AppDestination.Licenses -> LicensesSubscreen(navigator = navigator)
                        is AppDestination.Patcher -> PatchingSubscreen(navigator = navigator)
                    }
                }
            }
        }
    }

    private fun permissions() {

        fun request(string: String) {
            val intent = Intent(string)
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.fromParts("package", applicationContext.packageName, null)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivityForResult(intent, 1)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            request(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
        val pm = applicationContext.getSystemService(POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(applicationContext.packageName)) {
            request(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        }
    }
}