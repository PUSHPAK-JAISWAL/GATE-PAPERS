package com.example.data.repository

import com.example.data.local.SettingsDao
import com.example.data.model.AppSettingEntity
import com.example.data.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val dao: SettingsDao) {

    val userSettingsFlow: Flow<UserSettings> = dao.getAllSettingsFlow().map { list ->
        val map = list.associate { it.key to it.value }
        UserSettings(
            groqApiKey = map[AppSettingEntity.KEY_GROQ_API_KEY] ?: "",
            visionModel = map[AppSettingEntity.KEY_VISION_MODEL] ?: AppSettingEntity.DEFAULT_VISION_MODEL,
            codegenModel = map[AppSettingEntity.KEY_CODEGEN_MODEL] ?: AppSettingEntity.DEFAULT_CODEGEN_MODEL
        )
    }

    suspend fun getUserSettings(): UserSettings {
        val apiKey = dao.getSetting(AppSettingEntity.KEY_GROQ_API_KEY)?.value ?: ""
        val visionModel = dao.getSetting(AppSettingEntity.KEY_VISION_MODEL)?.value ?: AppSettingEntity.DEFAULT_VISION_MODEL
        val codegenModel = dao.getSetting(AppSettingEntity.KEY_CODEGEN_MODEL)?.value ?: AppSettingEntity.DEFAULT_CODEGEN_MODEL
        return UserSettings(
            groqApiKey = apiKey,
            visionModel = visionModel,
            codegenModel = codegenModel
        )
    }

    suspend fun saveApiKey(key: String) {
        dao.setSetting(AppSettingEntity(AppSettingEntity.KEY_GROQ_API_KEY, key.trim()))
    }

    suspend fun saveVisionModel(model: String) {
        dao.setSetting(AppSettingEntity(AppSettingEntity.KEY_VISION_MODEL, model.trim()))
    }

    suspend fun saveCodegenModel(model: String) {
        dao.setSetting(AppSettingEntity(AppSettingEntity.KEY_CODEGEN_MODEL, model.trim()))
    }

    suspend fun clearApiKey() {
        dao.deleteSetting(AppSettingEntity.KEY_GROQ_API_KEY)
    }
}
