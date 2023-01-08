package app.revanced.manager.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.revanced.manager.domain.manager.PreferencesManager
// import app.revanced.manager.network.dto.ReVancedSocials
import app.revanced.manager.network.service.ReVancedService
import app.revanced.manager.network.utils.getOrNull
import app.revanced.manager.ui.theme.Theme
import app.revanced.manager.util.openUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(
    private val app: Application,
    private val reVancedService: ReVancedService,
    val prefs: PreferencesManager,
) : ViewModel() {
    val socialsMap = mutableStateMapOf<String, String>()

    private suspend fun loadSocials() = withContext(Dispatchers.IO) {
        val socials = reVancedService.getSocials().getOrNull() ?: return@withContext
        // don't know if there is a better way to do this :breaddoge2:
        socials.forEach { (key, value) ->
            socialsMap[key] = value;
        }
    }

    fun openSocialUrl(service: String) {
        // As you can see, I am very good at kotlin.
        socialsMap[service]?.let { app.openUrl(it) }
    }

    var showThemePicker by mutableStateOf(false)
        private set

    fun showThemePicker() {
        showThemePicker = true
    }

    fun dismissThemePicker() {
        showThemePicker = false
    }

    fun setTheme(theme: Theme) {
        prefs.theme = theme
    }

    init {
        viewModelScope.launch {
            loadSocials()
        }
    }
}