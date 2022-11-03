package app.revanced.manager.domain.manager

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import android.webkit.URLUtil
import app.revanced.manager.util.get
import app.revanced.manager.util.tag
import com.vk.knet.core.Knet
import java.io.File

class DownloadManager(
    app: Application,
    val client: Knet
) {
    @SuppressLint("ServiceCast")
    val downloadManager =
        app.getSystemService(Context.DOWNLOAD_SERVICE) as android.app.DownloadManager

    fun downloadAsset(
        workdir: File, downloadUrl: String, contentType: String
    ): File {
        val name = URLUtil.guessFileName(downloadUrl, null, contentType)
        val out = workdir.resolve(name)
        if (out.exists()) {
            Log.d(
                tag, "Skipping downloading asset $name because it exists in cache!"
            )
            return out
        }
        val file = client.get(downloadUrl).body!!.asBytes()
        out.writeBytes(file)
        return out
    }
}