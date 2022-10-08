package app.revanced.manager.patcher.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import app.revanced.manager.R
import app.revanced.manager.Variables.patches
import app.revanced.manager.Variables.selectedAppPackage
import app.revanced.manager.Variables.selectedPatches
import app.revanced.manager.api.API
import app.revanced.manager.patcher.aapt.Aapt
import app.revanced.manager.patcher.aligning.ZipAligner
import app.revanced.manager.patcher.aligning.zip.ZipFile
import app.revanced.manager.patcher.aligning.zip.structures.ZipEntry
import app.revanced.manager.patcher.signing.Signer
import app.revanced.manager.ui.Resource
import app.revanced.manager.ui.viewmodel.Logging
import app.revanced.patcher.Patcher
import app.revanced.patcher.PatcherOptions
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.logging.Logger
import app.revanced.patcher.patch.Patch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class PatcherWorker(context: Context, parameters: WorkerParameters, private val api: API): CoroutineWorker(context, parameters) ,KoinComponent {

    val tag = "ReVanced Manager"
    private val workdir = createWorkDir()
    override suspend fun doWork(): Result {

        if (runAttemptCount > 0) {
            return Result.failure(
                androidx.work.Data.Builder()
                    .putString("error", "Android requested retrying but retrying is disabled")
                    .build()
            ) // don't retry
        }

        val notificationIntent = Intent(applicationContext, PatcherWorker::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val channel = NotificationChannel(
            "revanced-patcher-patching", "Patching", NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager =
            ContextCompat.getSystemService(applicationContext, NotificationManager::class.java)
        notificationManager!!.createNotificationChannel(channel)
        val notification: Notification = Notification.Builder(applicationContext, channel.id)
            .setContentTitle(applicationContext.getText(R.string.patcher_notification_title))
            .setContentText(applicationContext.getText(R.string.patcher_notification_message))
            .setLargeIcon(Icon.createWithResource(applicationContext, R.drawable.manager))
            .setSmallIcon(Icon.createWithResource(applicationContext, R.drawable.manager))
            .setContentIntent(pendingIntent).build()

        setForeground(ForegroundInfo(1, notification))
        return try {
            runPatcher(workdir)
            Result.success()
        } catch (e: Exception) {
            Log.e(tag, "Error while patching", e)
            Result.failure(
                androidx.work.Data.Builder()
                    .putString("error", "Error while patching: ${e.message ?: e::class.simpleName}")
                    .build()
            )
        }
    }

    private suspend fun runPatcher(
        workdir: File
    ): Boolean {
        val aaptPath = Aapt.binary(applicationContext).absolutePath
        val frameworkPath =
            applicationContext.filesDir.resolve("framework").also { it.mkdirs() }.absolutePath
        val integrationsCacheDir =
            applicationContext.filesDir.resolve("integrations-cache").also { it.mkdirs() }
        val appInfo = selectedAppPackage.value.get()

        Logging.log += "Checking prerequisites\n"
        val patches = findPatchesByIds(selectedPatches)
        if (patches.isEmpty()) return true


        Logging.log += "Creating directories\n"
        val inputFile = File(applicationContext.filesDir, "input.apk")
        val patchedFile = File(workdir, "patched.apk")
        val outputFile = File(applicationContext.filesDir, "output.apk")
        val cacheDirectory = workdir.resolve("cache")

        Logging.log += "Downloading integrations\n"
        val integrations = api.downloadIntegrations(integrationsCacheDir)

        Logging.log += "Copying base.apk from device\n"
        withContext(Dispatchers.IO) {
            Files.copy(
                File(appInfo.publicSourceDir).toPath(),
                inputFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )
        }

        try {
            Logging.log += "Decoding resources\n"
            val patcher = Patcher( // start patcher
                PatcherOptions(
                    inputFile,
                    cacheDirectory.absolutePath,
                    aaptPath = aaptPath,
                    frameworkFolderLocation = frameworkPath,
                    logger = object : Logger {
                        override fun error(msg: String) {
                            Log.e(tag, msg)
                        }

                        override fun warn(msg: String) {
                            Log.w(tag, msg)
                        }

                        override fun info(msg: String) {
                            Log.i(tag, msg)
                        }

                        override fun trace(msg: String) {
                            Log.v(tag, msg)
                        }
                    }
                )
            )


            Log.d(tag, "Adding ${patches.size} patch(es)")
            patcher.addPatches(patches)

            Logging.log += "Merging integrations\n"
            patcher.addFiles(listOf(integrations)) {}

            Logging.log += "Applying ${patches.size} patch(es)\n"
            patcher.executePatches().forEach { (patch, result) ->
                if (result.isFailure) {
                    Logging.log +=  "Failed to apply $patch" + result.exceptionOrNull()!!.cause + "\n"
                    return@forEach
                }

            }
            Logging.log += "Saving file\n"

            val result = patcher.save() // compile apk

            if (patchedFile.exists()) withContext(Dispatchers.IO) {
                Files.delete(patchedFile.toPath())
            }

            ZipFile(patchedFile).use { fs -> // somehow this function is the most resource intensive
                result.dexFiles.forEach { Logging.log +=  "Writing dex file ${it.name}\n"
                    fs.addEntryCompressData(ZipEntry.createWithName(it.name), it.stream.readBytes())}

                Logging.log +=  "Aligning apk!\n"
                result.resourceFile?.let {
                    fs.copyEntriesFromFileAligned(ZipFile(it), ZipAligner::getEntryAlignment)
                }
                fs.copyEntriesFromFileAligned(ZipFile(inputFile), ZipAligner::getEntryAlignment)
            }

            Logging.log +=  "Signing apk\n"
            Signer("ReVanced", "s3cur3p@ssw0rd").signApk(patchedFile, outputFile)
            Logging.log +=  "Successfully patched!\n"
        } finally {
            Log.d(tag, "Deleting workdir")
            workdir.deleteRecursively()
        }
        return false
    }

    private fun findPatchesByIds(ids: Iterable<String>): List<Class<out Patch<app.revanced.patcher.data.Context>>> {
        val (patches) = patches.value as? Resource.Success ?: return listOf()
        return patches.filter { patch -> ids.any { it == patch.patchName } }
    }
    private fun createWorkDir(): File {
        return applicationContext.filesDir.resolve("tmp-${System.currentTimeMillis()}")
            .also { it.mkdirs() }
    }
}
