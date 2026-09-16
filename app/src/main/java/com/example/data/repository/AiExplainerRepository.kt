package com.example.data.repository

import com.example.data.local.AiExplanationDao
import com.example.data.model.AiExplanationEntity
import kotlinx.coroutines.flow.Flow

class AiExplainerRepository(private val dao: AiExplanationDao) {

    val allExplanationsFlow: Flow<List<AiExplanationEntity>> = dao.getAllExplanationsFlow()

    suspend fun saveExplanation(explanation: AiExplanationEntity): Long {
        return dao.insertExplanation(explanation)
    }

    suspend fun getExplanationById(id: Long): AiExplanationEntity? {
        return dao.getExplanationById(id)
    }

    suspend fun deleteExplanation(id: Long) {
        dao.deleteExplanation(id)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}
