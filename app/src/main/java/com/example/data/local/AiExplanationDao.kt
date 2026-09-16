package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.AiExplanationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiExplanationDao {
    @Query("SELECT * FROM ai_explanations ORDER BY timestamp DESC")
    fun getAllExplanationsFlow(): Flow<List<AiExplanationEntity>>

    @Query("SELECT * FROM ai_explanations WHERE id = :id LIMIT 1")
    suspend fun getExplanationById(id: Long): AiExplanationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExplanation(explanation: AiExplanationEntity): Long

    @Query("DELETE FROM ai_explanations WHERE id = :id")
    suspend fun deleteExplanation(id: Long)

    @Query("DELETE FROM ai_explanations")
    suspend fun clearAll()
}
