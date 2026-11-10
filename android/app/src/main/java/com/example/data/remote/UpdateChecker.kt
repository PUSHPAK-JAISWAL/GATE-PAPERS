package com.example.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AppReleaseInfo(
    val tagName: String,
    val versionName: String,
    val releaseTitle: String,
    val releaseNotes: String,
    val publishedAt: String,
    val apkDownloadUrl: String,
    val releasePageUrl: String,
    val isNewer: Boolean
)

object UpdateChecker {
    private const val TAG = "UpdateChecker"
    private const val REPO_OWNER = "PUSHPAK-JAISWAL"
    private const val REPO_NAME = "gate-papers"

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun checkLatestRelease(currentVersionName: String): Result<AppReleaseInfo?> = withContext(Dispatchers.IO) {
        val url = "https://api.github.com/repos/$REPO_OWNER/$REPO_NAME/releases/latest"

        try {
            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", "GatePapers-AndroidApp")
                .build()

            val response = client.newCall(request).execute()

            if (response.code == 404) {
                Log.d(TAG, "No GitHub releases published yet for $REPO_NAME")
                return@withContext Result.success(null)
            }

            if (!response.isSuccessful) {
                Log.w(TAG, "GitHub releases API returned code: ${response.code}")
                return@withContext Result.failure(Exception("HTTP error ${response.code}"))
            }

            val body = response.body?.string() ?: return@withContext Result.success(null)
            val json = JSONObject(body)

            val tagName = json.optString("tag_name", "").trim()
            val releaseTitle = json.optString("name", tagName)
            val releaseNotes = json.optString("body", "").trim()
            val publishedAt = json.optString("published_at", "")
            val htmlUrl = json.optString("html_url", "https://github.com/$REPO_OWNER/$REPO_NAME/releases")

            // Find direct APK asset if available
            var apkUrl = "https://github.com/$REPO_OWNER/$REPO_NAME/releases/latest/download/GATE-Papers.apk"
            val assetsArray = json.optJSONArray("assets")
            if (assetsArray != null) {
                for (i in 0 until assetsArray.length()) {
                    val asset = assetsArray.getJSONObject(i)
                    val assetName = asset.optString("name", "")
                    if (assetName.endsWith(".apk", ignoreCase = true)) {
                        val downloadUrl = asset.optString("browser_download_url")
                        if (downloadUrl.isNotEmpty()) {
                            apkUrl = downloadUrl
                            break
                        }
                    }
                }
            }

            val normalizedTag = com.example.util.VersionUtil.normalize(tagName)
            val normalizedCurrent = com.example.util.VersionUtil.normalize(currentVersionName)
            val isNewer = isVersionNewer(normalizedTag, normalizedCurrent)

            val releaseInfo = AppReleaseInfo(
                tagName = "v$normalizedTag",
                versionName = normalizedTag,
                releaseTitle = if (releaseTitle.isNotEmpty()) releaseTitle else "GATE Papers v$normalizedTag",
                releaseNotes = releaseNotes,
                publishedAt = publishedAt,
                apkDownloadUrl = apkUrl,
                releasePageUrl = htmlUrl,
                isNewer = isNewer
            )

            Log.d(TAG, "Found release: $tagName (isNewer=$isNewer vs local=$currentVersionName)")
            Result.success(releaseInfo)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking for app updates", e)
            Result.failure(e)
        }
    }

    /**
     * Compares two semantic version strings e.g. "1.0.2" vs "1.0.1".
     * Returns true if remoteVersion is strictly greater than currentVersion.
     */
    fun isVersionNewer(remoteVersion: String, currentVersion: String): Boolean {
        if (remoteVersion.isBlank() || currentVersion.isBlank()) return false
        if (remoteVersion == currentVersion) return false

        val remoteParts = remoteVersion.split(".", "-").mapNotNull { it.toIntOrNull() }
        val currentParts = currentVersion.split(".", "-").mapNotNull { it.toIntOrNull() }

        val maxLen = maxOf(remoteParts.size, currentParts.size)
        for (i in 0 until maxLen) {
            val r = remoteParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }
        return false
    }
}
