package com.example.data.repository

import android.util.Log
import com.example.data.local.PaperDao
import com.example.data.model.PaperEntity
import com.example.data.remote.GitHubApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PaperRepository(private val dao: PaperDao) {

    val allPapers: Flow<List<PaperEntity>> = dao.getAllPapers()

    fun getPapersBySection(section: String): Flow<List<PaperEntity>> {
        return dao.getPapersBySection(section)
    }

    fun observePaper(id: String): Flow<PaperEntity?> {
        return dao.observePaperById(id)
    }

    suspend fun getPaperById(id: String): PaperEntity? = withContext(Dispatchers.IO) {
        dao.getPaperById(id)
    }

    suspend fun initializeDefaultDataIfNeeded() = withContext(Dispatchers.IO) {
        val initialPapers = DefaultPapers.getInitialPapers()
        val validIds = initialPapers.map { it.id }.toSet()
        val existing = dao.getAllPapersList()
        if (existing.isEmpty()) {
            dao.insertAll(initialPapers)
        } else {
            // Prune any legacy/phantom papers that do not exist on GitHub
            val invalidPapers = existing.filter { it.id !in validIds }
            if (invalidPapers.isNotEmpty()) {
                dao.deletePapersNotIn(validIds.toList())
                Log.d("PaperRepository", "Pruned ${invalidPapers.size} phantom papers from database")
            }
            // Ensure all 27 authentic papers are present
            dao.insertAll(initialPapers)
        }
    }

    /**
     * Dynamically fetches real PDFs from the user's GitHub repositories:
     * - https://github.com/PUSHPAK-JAISWAL/gatecs
     * - https://github.com/PUSHPAK-JAISWAL/gateda
     *
     * Merges with existing database entries to preserve user progress, notes, and flags.
     */
    suspend fun syncWithGitHub(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val remotePapers = GitHubApiClient.fetchAllPapersFromAllRepos()
            if (remotePapers.isEmpty()) {
                Log.w("PaperRepository", "Remote sync returned 0 papers, keeping existing data")
                return@withContext Result.failure(Exception("No papers retrieved from GitHub"))
            }

            val existingList = dao.getAllPapersList()
            val existingById = existingList.associateBy { it.id }
            val existingByName = existingList.associateBy { it.githubFileName }

            val mergedPapers = remotePapers.map { remote ->
                val existing = existingById[remote.id] ?: existingByName[remote.githubFileName]
                if (existing != null) {
                    remote.copy(
                        status = existing.status,
                        completedAt = existing.completedAt,
                        isFlaggedToRevisit = existing.isFlaggedToRevisit,
                        notes = existing.notes
                    )
                } else {
                    remote
                }
            }

            dao.upsertAll(mergedPapers)
            // PRUNE PHANTOMS: Only keep papers that actually exist on GitHub
            val remoteIds = remotePapers.map { it.id }
            dao.deletePapersNotIn(remoteIds)
            Log.d("PaperRepository", "Successfully synced ${mergedPapers.size} papers from GitHub and pruned phantoms")
            Result.success(mergedPapers.size)
        } catch (e: Exception) {
            Log.e("PaperRepository", "GitHub sync failed", e)
            Result.failure(e)
        }
    }

    suspend fun markFinished(id: String) = withContext(Dispatchers.IO) {
        dao.updateStatus(id, PaperEntity.STATUS_FINISHED, System.currentTimeMillis())
        dao.updateRevisitFlag(id, false)
    }

    suspend fun markPending(id: String) = withContext(Dispatchers.IO) {
        dao.updateStatus(id, PaperEntity.STATUS_PENDING, null)
        dao.updateRevisitFlag(id, true)
    }

    suspend fun toggleRevisit(id: String, currentFlag: Boolean) = withContext(Dispatchers.IO) {
        dao.updateRevisitFlag(id, !currentFlag)
        if (!currentFlag) {
            val paper = dao.getPaperById(id)
            if (paper?.status != PaperEntity.STATUS_FINISHED) {
                dao.updateStatus(id, PaperEntity.STATUS_PENDING, null)
            }
        }
    }

    suspend fun resetStatus(id: String) = withContext(Dispatchers.IO) {
        dao.resetStatus(id)
    }

    suspend fun resetAllProgress() = withContext(Dispatchers.IO) {
        dao.resetAllProgress()
    }

    suspend fun updateNotes(id: String, notes: String) = withContext(Dispatchers.IO) {
        dao.updateNotes(id, notes)
    }
}
