package app.revanced.manager.installer.root

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInstaller
import android.util.Log
import app.revanced.manager.installer.service.InstallService
import com.topjohnwu.superuser.Shell
import java.io.File

// TODO: Implement unpatching/uninstalling
object RootAppInstaller {
    private const val TAG = "RootAppInstaller"

    internal const val managerDirPath = "/data/local/tmp/revanced-manager"
    private const val postFsDataDirPath = "/data/adb/post-fs-data.d"
    internal const val serviceDDirPath = "/data/adb/service.d"

    fun installApp(patchedApk: File, appInfo: ApplicationInfo, context: Context) {
        Log.d(TAG, "Installing app ${appInfo.packageName} with root")
        val packageName = appInfo.packageName

        Shell.cmd("mkdir -p $managerDirPath/$packageName").exec()
        RootUtils.setPermissions("$managerDirPath/$packageName", "shell:shell", "0755")
        saveOriginalApk(packageName)
        installServiceD(packageName)
        installPostFsData(packageName)
        installApk(packageName, patchedApk)
        mountApk(packageName, RootUtils.getAppPathFromPM(packageName))

        // TODO: Do a proper handling for errors
        val serviceIntent = Intent(context, InstallService::class.java)
        serviceIntent.action = InstallService.APP_INSTALL_ACTION

        serviceIntent.putExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_SUCCESS)
        serviceIntent.putExtra(PackageInstaller.EXTRA_STATUS_MESSAGE, "Success")

        context.startService(serviceIntent)
    }

    private fun installServiceD(packageName: String) {
        Log.v(TAG, "Installing service.d script for $packageName")
        val content = """
            #!/system/bin/sh
            while [ "$(getprop sys.boot_completed | tr -d '\r')" != "1" ]; do sleep 3; done
            base_path=$managerDirPath/$packageName/base.apk
            stock_path=$(pm path $packageName | grep base | sed "s/package://g")
            [ ! -z ${'$'}stock_path ] && mount -o bind ${'$'}base_path ${'$'}stock_path
        """.trimIndent()
        val path = "$serviceDDirPath/$packageName.sh"
        RootUtils.writeToFile(path, content)
        RootUtils.setPermissions(path, permissions = "0744")
    }

    private fun installPostFsData(packageName: String) {
        Log.v(TAG, "Installing post-fs-data script for $packageName")
        val content = """
            #!/system/bin/sh
            stock_path=$(pm path $packageName | grep base | sed "s/package://g")
            [ ! -z ${'$'}stock_path ] && umount -l ${'$'}stock_path
        """.trimIndent()
        val path = "$postFsDataDirPath/$packageName.sh"
        RootUtils.writeToFile(path, content)
        RootUtils.setPermissions(path, "", "0744")
    }

    private fun installApk(packageName: String, patchedApk: File) {
        Log.v(TAG, "Installing patched apk for $packageName")
        val newPatchedFilePath = "$managerDirPath/$packageName/base.apk"
        Shell.cmd("cp ${patchedApk.absolutePath} $newPatchedFilePath").exec()
        RootUtils.setPermissions(
            newPatchedFilePath,
            "system:system",
            "0644",
            "u:object_r:apk_data_file:s0"
        )
    }

    private fun mountApk(packageName: String, appOriginalPath: String) {
        Log.v(TAG, "Mounting patched apk for $packageName")
        val newPatchedFilePath = "$managerDirPath/$packageName/base.apk"
        Shell.cmd("am force-stop $packageName").exec()
        Shell.cmd("su -mm -c \"umount -l $appOriginalPath\"").exec()
        Shell.cmd("su -mm -c \"mount -o bind $newPatchedFilePath $appOriginalPath\"").exec()
    }

    fun getOriginalApkPath(packageName: String): String? {
        if (RootUtils.isAppInstalled(packageName) && RootUtils.isMounted(packageName)) {
            val path = "$managerDirPath/$packageName/original.apk"
            RootUtils.setPermissions(path, "system:system", "0644", "u:object_r:apk_data_file:s0")
            return path
        }
        return null
    }

    private fun saveOriginalApk(packageName: String) {
        val originalApkPath = Shell.cmd("pm path $packageName | grep base | sed \"\"s/package://g\"\"")
            .exec().out.joinToString("\n")
        val pathWhereToSave = "$managerDirPath/$packageName/original.apk"

        Shell.cmd("cp $originalApkPath $pathWhereToSave").exec()
        RootUtils.setPermissions(pathWhereToSave, "shell:shell", "0644")
    }
}