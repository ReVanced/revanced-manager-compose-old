package app.revanced.manager

import android.content.pm.ApplicationInfo
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import app.revanced.manager.ui.Resource
import app.revanced.patcher.data.Data
import app.revanced.patcher.patch.Patch
import java.util.*


object Variables {
    val selectedAppPackage = mutableStateOf(Optional.empty<String>())
    val selectedPatches = mutableStateListOf<String>()
    val patches = mutableStateOf<Resource<List<Class<out Patch<Data>>>>>(Resource.Loading)
    val patchesState by patches
    val filteredApps = mutableListOf<ApplicationInfo>()
}