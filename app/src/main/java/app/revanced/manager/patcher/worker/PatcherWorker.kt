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
import app.revanced.manager.Variables.selectedPatches
import app.revanced.manager.patcher.aapt.Aapt
import app.revanced.manager.patcher.aligning.ZipAligner
import app.revanced.manager.patcher.aligning.zip.ZipFile
import app.revanced.manager.patcher.aligning.zip.structures.ZipEntry
import app.revanced.manager.patcher.signing.Signer
import app.revanced.manager.ui.Resource
import app.revanced.patcher.Patcher
import app.revanced.patcher.PatcherOptions
import app.revanced.patcher.data.Data
import app.revanced.patcher.extensions.PatchExtensions.dependencies
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.logging.Logger
import app.revanced.patcher.patch.Patch
import app.revanced.patcher.patch.impl.ResourcePatch
import java.io.File
import java.nio.file.Files

class PatcherWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    val tag = "ReVanced Manager"
    private val workdir = File(inputData.getString("workdir")!!)

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

    private fun runPatcher(
        workdir: File
    ): Boolean {
        val aaptPath = Aapt.binary(applicationContext).absolutePath
        val frameworkPath =
            applicationContext.filesDir.resolve("framework").also { it.mkdirs() }.absolutePath

        Log.d(tag, "Checking prerequisites")
        val patches = findPatchesByIds(selectedPatches)
        if (patches.isEmpty()) return true


        Log.d(tag, "Creating directories")

        File(inputData.getString("input")!!).copyTo(
            applicationContext.filesDir.resolve("base.apk"),
            true
        )

        val inputFile = File(applicationContext.filesDir, "base.apk")
        val patchedFile = File(workdir, "patched.apk")
        val outputFile = File(applicationContext.filesDir, "out.apk")
        val cacheDirectory = workdir.resolve("cache")
        val integrations = workdir.resolve("integrations.apk")
        try {
            Log.d(tag, "Creating patcher")
            val patcher = Patcher( // start patcher
                PatcherOptions(
                    inputFile,
                    cacheDirectory.absolutePath,
                    patchResources = checkForResourcePatch(patches),
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

            patcher.addFiles(listOf(integrations)) {}

            patcher.applyPatches().forEach { (patch, result) ->
                if (result.isSuccess) {
                    Log.i(tag, "[success] $patch")
                    return@forEach
                }
                Log.e(tag, "[error] $patch:", result.exceptionOrNull()!!)
            }
            Log.d(tag, "Saving file")

            val result = patcher.save() // compile apk

            if (patchedFile.exists()) Files.delete(patchedFile.toPath())

            ZipFile(patchedFile).use { fs -> // somehow this function is the most resource intensive
                result.dexFiles.forEach { Log.d(tag, "Writing dex file ${it.name}")
                    fs.addEntryCompressData(ZipEntry.createWithName(it.name), it.stream.readBytes())}


                result.resourceFile?.let {
                    fs.copyEntriesFromFileAligned(ZipFile(it), ZipAligner::getEntryAlignment)
                }
                fs.copyEntriesFromFileAligned(ZipFile(inputFile), ZipAligner::getEntryAlignment)
            }

            Signer("ReVanced", "s3cur3p@ssw0rd").signApk(patchedFile, outputFile)
            Log.i(tag, "Successfully patched into $outputFile")
        } finally {
            Log.d(tag, "Deleting workdir")
            // workdir.deleteRecursively()
        }
        return false
    }

    private fun findPatchesByIds(ids: Iterable<String>): List<Class<out Patch<Data>>> {
        val (patches) = patches.value as? Resource.Success ?: return listOf()
        return patches.filter { patch -> ids.any { it == patch.patchName } }
    }

    private fun checkForResourcePatch(patches: List<Class<out Patch<Data>>>): Boolean {
        patches.forEach { patch ->
            patch.dependencies?.forEach {
                if (ResourcePatch::class.java.isAssignableFrom(patch)) { // check for resource patches in normal patches
                    return true
                }
                if (ResourcePatch::class.java.isAssignableFrom(it.java)) { // do the same thing in dependency patches
                    return true
                }
            }
        }
        return false
    }
}
