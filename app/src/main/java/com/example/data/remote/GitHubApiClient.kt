package com.example.data.remote

import android.util.Log
import com.example.data.model.PaperEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

data class GitHubRepoConfig(
    val owner: String = "PUSHPAK-JAISWAL",
    val repo: String,
    val sectionName: String,
    val branch: String = "main"
)

object GitHubApiClient {
    private const val TAG = "GitHubApiClient"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    val TARGET_REPOS = listOf(
        GitHubRepoConfig(repo = "gatecs", sectionName = PaperEntity.SECTION_CS),
        GitHubRepoConfig(repo = "gateda", sectionName = PaperEntity.SECTION_DA)
    )

    private val YEAR_PATTERN = Pattern.compile("(19|20)\\d{2}")

    /**
     * Dynamically fetches the list of PDFs from the GitHub repository contents API.
     */
    suspend fun fetchPdfsFromRepo(config: GitHubRepoConfig): List<PaperEntity> = withContext(Dispatchers.IO) {
        val result = mutableListOf<PaperEntity>()
        val url = "https://api.github.com/repos/${config.owner}/${config.repo}/contents?ref=${config.branch}"

        try {
            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", "GatePapersApp")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w(TAG, "Failed to fetch ${config.repo} from GitHub: HTTP ${response.code}")
                return@withContext emptyList()
            }

            val bodyString = response.body?.string() ?: return@withContext emptyList()
            val jsonArray = JSONArray(bodyString)

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val type = item.optString("type")
                val name = item.optString("name")

                // Only consider PDF files
                if (type == "file" && name.endsWith(".pdf", ignoreCase = true)) {
                    val sizeBytes = item.optLong("size", 0L)
                    val rawUrl = item.optString("download_url").ifEmpty {
                        "https://raw.githubusercontent.com/${config.owner}/${config.repo}/${config.branch}/$name"
                    }

                    // Extract year from file name or default to 2026
                    val matcher = YEAR_PATTERN.matcher(name)
                    val year = if (matcher.find()) {
                        matcher.group().toIntOrNull() ?: 2026
                    } else {
                        2026
                    }

                    // Determine set number if name contains CS1, CS2, Set1, Set2, etc.
                    val setNumber = when {
                        name.contains("1", ignoreCase = true) -> 1
                        name.contains("2", ignoreCase = true) -> 2
                        else -> 1
                    }

                    val formattedSize = formatFileSize(sizeBytes)
                    val repoPath = "${config.owner}/${config.repo}"

                    result.add(
                        PaperEntity(
                            id = "${config.repo}_$name",
                            section = config.sectionName,
                            year = year,
                            title = name,
                            subtitle = "$formattedSize • $repoPath",
                            setNumber = setNumber,
                            githubRepo = repoPath,
                            githubFileName = name,
                            rawFileUrl = rawUrl,
                            fileSizeBytes = sizeBytes,
                            totalMarks = 100,
                            durationMinutes = 180,
                            totalQuestions = 65,
                            coreTopics = if (config.sectionName == PaperEntity.SECTION_CS) "CS & IT Exam Paper" else "Data Science & AI Exam Paper"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching from GitHub for ${config.repo}", e)
        }

        // Sort descending by year then by file name
        result.sortedWith(compareByDescending<PaperEntity> { it.year }.thenBy { it.title })
    }

    /**
     * Fetches all PDF papers dynamically from both gatecs and gateda repositories.
     */
    suspend fun fetchAllPapersFromAllRepos(): List<PaperEntity> = withContext(Dispatchers.IO) {
        val all = mutableListOf<PaperEntity>()
        for (config in TARGET_REPOS) {
            val papers = fetchPdfsFromRepo(config)
            all.addAll(papers)
        }
        all
    }

    private fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "PDF Document"
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        return if (mb >= 1.0) {
            String.format("%.1f MB", mb)
        } else {
            String.format("%.0f KB", kb)
        }
    }
}
