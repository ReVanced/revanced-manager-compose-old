package app.revanced.manager.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.revanced.manager.network.api.ManagerAPI
import app.revanced.manager.patcher.PatcherUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PatcherScreenViewModel(
    patcherUtils: PatcherUtils,
    api: ManagerAPI
) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            api.downloadPatches()
        }
    }

    val selectedPatches = patcherUtils.selectedPatches
    val selectedAppPackage by patcherUtils.selectedAppPackage
    val patches by patcherUtils.patches
}