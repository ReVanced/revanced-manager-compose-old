package app.revanced.manager.installer.root

import android.util.Log
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.io.SuFile
import com.topjohnwu.superuser.io.SuFileOutputStream

object RootUtils {
    fun isRootAvailable(): Boolean = Shell.isAppGrantedRoot() ?: false

    fun setPermissions(path: String, ownerGroup: String = "", permissions: String = "", seLinux: String = "") {
        if (ownerGroup.isNotEmpty()) {
            Shell.cmd("chown $ownerGroup $path").exec()
        }
        if (permissions.isNotEmpty()) {
            Shell.cmd("chmod $permissions $path").exec()
        }
        if (seLinux.isNotEmpty()) {
            Shell.cmd("chcon $seLinux $path").exec()
        }
    }

    fun writeToFile(path: String, content: String) {
        val shellFile = SuFile.open(path)
        shellFile.createNewFile()
        SuFileOutputStream.open(shellFile).use {
            it.write(content.toByteArray())
        }
    }

    fun isMounted(packageName: String) : Boolean {
        Log.d("RootUtils", "Checking if $packageName is mounted")
        val res= Shell.cmd("cat /proc/mounts | grep $packageName").exec().out.isNotEmpty()
        Log.d("RootUtils", "Result: $res")
        return res
    }

    fun isAppInstalled(packageName: String): Boolean {
        if (packageName.isEmpty()) return false
        val inManagerDir = Shell.cmd("ls ${RootAppInstaller.managerDirPath}/$packageName").exec().out.isNotEmpty()
        if (inManagerDir) {
            val inServiceDir = Shell.cmd("ls ${RootAppInstaller.serviceDDirPath}/$packageName.sh").exec().out.isNotEmpty()
            return inServiceDir
        }
        return false
    }

    fun getAppPathFromPM(packageName: String): String {
        return Shell.cmd("pm path $packageName | grep base | sed \"s/package://g\"").exec().out.first()
    }
}