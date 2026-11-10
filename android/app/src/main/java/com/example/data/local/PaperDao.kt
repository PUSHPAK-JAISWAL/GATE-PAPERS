package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PaperEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaperDao {

    @Query("SELECT * FROM papers ORDER BY year DESC, setNumber ASC")
    fun getAllPapers(): Flow<List<PaperEntity>>

    @Query("SELECT * FROM papers ORDER BY year DESC, setNumber ASC")
    suspend fun getAllPapersList(): List<PaperEntity>

    @Query("SELECT * FROM papers WHERE section = :section ORDER BY year DESC, setNumber ASC")
    fun getPapersBySection(section: String): Flow<List<PaperEntity>>

    @Query("SELECT * FROM papers WHERE id = :id LIMIT 1")
    suspend fun getPaperById(id: String): PaperEntity?

    @Query("SELECT * FROM papers WHERE id = :id LIMIT 1")
    fun observePaperById(id: String): Flow<PaperEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(papers: List<PaperEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(papers: List<PaperEntity>)

    @Query("UPDATE papers SET status = :status, completedAt = :completedAt WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, completedAt: Long?)

    @Query("UPDATE papers SET isFlaggedToRevisit = :revisit WHERE id = :id")
    suspend fun updateRevisitFlag(id: String, revisit: Boolean)

    @Query("UPDATE papers SET notes = :notes WHERE id = :id")
    suspend fun updateNotes(id: String, notes: String)

    @Query("UPDATE papers SET status = 'UNATTEMPTED', completedAt = NULL, isFlaggedToRevisit = 0 WHERE id = :id")
    suspend fun resetStatus(id: String)

    @Query("UPDATE papers SET status = 'UNATTEMPTED', completedAt = NULL, isFlaggedToRevisit = 0")
    suspend fun resetAllProgress()

    @Query("DELETE FROM papers WHERE id NOT IN (:validIds)")
    suspend fun deletePapersNotIn(validIds: List<String>)
}
