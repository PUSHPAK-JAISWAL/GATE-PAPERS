package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

sealed class UpdateDownloadState {
    object Idle : UpdateDownloadState()
    data class Downloading(val progressPercent: Int, val bytesDownloaded: Long, val totalBytes: Long) : UpdateDownloadState()
    data class ReadyToInstall(val apkFile: File) : UpdateDownloadState()
    data class NeedsPermission(val apkFile: File) : UpdateDownloadState()
    data class SignatureConflict(val apkFile: File, val message: String, val isCopiedToDownloads: Boolean = false) : UpdateDownloadState()
    data class Failed(val errorMessage: String) : UpdateDownloadState()
}

data class ApkValidationResult(
    val isValid: Boolean,
    val packageName: String?,
    val hasSignatureConflict: Boolean,
    val reason: String? = null
)

object InAppUpdateDownloader {
    private const val TAG = "InAppUpdateDownloader"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    /**
     * Downloads the APK file to the app's updates directory,
     * overwriting any previous version so multiple APKs are not accumulated.
     */
    suspend fun downloadApk(
        context: Context,
        downloadUrl: String,
        onProgress: (Int, Long, Long) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            // Prefer external files dir (more accessible for system package installer via FileProvider)
            val baseDir = context.getExternalFilesDir(null) ?: context.filesDir
            val updatesDir = File(baseDir, "updates").apply {
                if (!exists()) mkdirs()
            }
            val targetFile = File(updatesDir, "GATE-Papers.apk")
            if (targetFile.exists()) {
                targetFile.delete()
            }

            val request = Request.Builder()
                .url(downloadUrl)
                .header("User-Agent", "GatePapers-AndroidApp")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Download failed with HTTP ${response.code}"))
            }

            val body = response.body ?: return@withContext Result.failure(Exception("Empty response body"))
            val contentLength = body.contentLength()
            var bytesCopied = 0L

            body.byteStream().use { input ->
                FileOutputStream(targetFile).use { output ->
                    val buffer = ByteArray(8 * 1024)
                    var read: Int
                    var lastReportedPercent = -1

                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                        bytesCopied += read

                        if (contentLength > 0) {
                            val percent = ((bytesCopied * 100) / contentLength).toInt().coerceIn(0, 100)
                            if (percent != lastReportedPercent) {
                                lastReportedPercent = percent
                                onProgress(percent, bytesCopied, contentLength)
                            }
                        } else {
                            onProgress(-1, bytesCopied, -1L)
                        }
                    }
                    output.flush()
                }
            }

            Log.d(TAG, "APK successfully downloaded to ${targetFile.absolutePath} (${targetFile.length()} bytes)")
            Result.success(targetFile)
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading update APK", e)
            Result.failure(e)
        }
    }

    /**
     * Inspects the downloaded APK to verify package name and detect cryptographic signature conflicts
     * BEFORE triggering Android's PackageInstaller.
     */
    fun validateDownloadedApk(context: Context, apkFile: File): ApkValidationResult {
        try {
            val pm = context.packageManager
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                PackageManager.GET_SIGNING_CERTIFICATES or @Suppress("DEPRECATION") PackageManager.GET_SIGNATURES
            } else {
                @Suppress("DEPRECATION")
                PackageManager.GET_SIGNATURES
            }

            val archiveInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageArchiveInfo(apkFile.absolutePath, PackageManager.PackageInfoFlags.of(flags.toLong()))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageArchiveInfo(apkFile.absolutePath, flags)
            }

            if (archiveInfo == null) {
                return ApkValidationResult(
                    isValid = false,
                    packageName = null,
                    hasSignatureConflict = false,
                    reason = "Could not parse downloaded APK."
                )
            }

            val downloadedPackage = archiveInfo.packageName
            if (!downloadedPackage.isNullOrEmpty() && downloadedPackage != context.packageName) {
                return ApkValidationResult(
                    isValid = false,
                    packageName = downloadedPackage,
                    hasSignatureConflict = true,
                    reason = "Package mismatch: downloaded package is '$downloadedPackage' while current app is '${context.packageName}'."
                )
            }

            // Extract signatures from both installed app and downloaded APK
            val installedInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(context.packageName, flags)
            }

            val installedSignatures = extractSignatures(installedInfo)
            val downloadedSignatures = extractSignatures(archiveInfo)

            if (installedSignatures.isNotEmpty() && downloadedSignatures.isNotEmpty()) {
                val matches = installedSignatures.any { it in downloadedSignatures }
                if (!matches) {
                    return ApkValidationResult(
                        isValid = false,
                        packageName = downloadedPackage,
                        hasSignatureConflict = true,
                        reason = "Signature conflict: The downloaded release APK is signed with a different key than your installed build."
                    )
                }
            }

            return ApkValidationResult(
                isValid = true,
                packageName = downloadedPackage,
                hasSignatureConflict = false
            )
        } catch (e: Exception) {
            Log.w(TAG, "Signature inspection warning", e)
            return ApkValidationResult(isValid = true, packageName = context.packageName, hasSignatureConflict = false)
        }
    }

    private fun extractSignatures(info: PackageInfo): List<String> {
        val result = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val signingInfo = info.signingInfo
            if (signingInfo != null) {
                val certs = if (signingInfo.hasMultipleSigners()) {
                    signingInfo.apkContentsSigners
                } else {
                    signingInfo.signingCertificateHistory
                }
                certs?.forEach { result.add(it.toCharsString()) }
                signingInfo.apkContentsSigners?.forEach { sig ->
                    val str = sig.toCharsString()
                    if (!result.contains(str)) result.add(str)
                }
            }
        }
        @Suppress("DEPRECATION")
        info.signatures?.forEach { sig ->
            val str = sig.toCharsString()
            if (!result.contains(str)) result.add(str)
        }
        return result
    }

    /**
     * Copies the downloaded APK to the user's public Downloads directory
     * so they can retain the APK if a manual clean install is needed.
     */
    fun copyApkToPublicDownloads(context: Context, apkFile: File): Boolean {
        return try {
            val fileName = "GATE-Papers.apk"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.android.package-archive")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use { outStream ->
                        apkFile.inputStream().use { inStream ->
                            inStream.copyTo(outStream)
                        }
                    }
                    true
                } else {
                    false
                }
            } else {
                @Suppress("DEPRECATION")
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val destFile = File(downloadsDir, fileName)
                apkFile.copyTo(destFile, overwrite = true)
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to copy APK to public downloads", e)
            false
        }
    }

    /**
     * Verifies whether the app has permission to install unknown apps (Android 8.0+).
     */
    fun canInstallApk(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    /**
     * Opens system settings to allow this app to install packages.
     */
    fun requestInstallPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(
                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                Uri.parse("package:${context.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    /**
     * Launches the system Package Installer directly via FileProvider.
     * Grants explicit read permissions to the resolving package installer activities.
     */
    fun launchInstaller(context: Context, apkFile: File): Boolean {
        return try {
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                apkFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

            // Explicitly grant URI permission to resolving package installer targets
            val resolveInfoList = context.packageManager.queryIntentActivities(installIntent, 0)
            for (res in resolveInfoList) {
                val packageName = res.activityInfo?.packageName
                if (!packageName.isNullOrEmpty()) {
                    context.grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }

            context.startActivity(installIntent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch package installer", e)
            false
        }
    }
}
