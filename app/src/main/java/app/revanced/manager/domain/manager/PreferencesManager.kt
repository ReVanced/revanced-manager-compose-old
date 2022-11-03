package app.revanced.manager.domain.manager

import android.content.SharedPreferences
import app.revanced.manager.domain.manager.base.BasePreferenceManager
import app.revanced.manager.ui.theme.Theme
import app.revanced.manager.util.ghIntegrations
import app.revanced.manager.util.ghPatches

/**
 * @author Hyperion Authors, zt64
 */
class PreferencesManager(
    sharedPreferences: SharedPreferences
) : BasePreferenceManager(sharedPreferences) {
    var dynamicColor by booleanPreference("dynamic_color", true)
    var theme by enumPreference("theme", Theme.SYSTEM)
    var srcPatches by stringPreference("src_patches", ghPatches)
    var srcIntegrations by stringPreference("src_integrations", ghIntegrations)
}
