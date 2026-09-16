package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingEntity(
    @PrimaryKey val key: String,
    val value: String
) {
    companion object {
        const val KEY_GROQ_API_KEY = "groq_api_key"
        const val KEY_VISION_MODEL = "vision_model"
        const val KEY_CODEGEN_MODEL = "codegen_model"

        const val DEFAULT_VISION_MODEL = "qwen/qwen3.6-27b"
        const val DEFAULT_CODEGEN_MODEL = "llama-3.3-70b-versatile"
    }
}

data class UserSettings(
    val groqApiKey: String = "",
    val visionModel: String = AppSettingEntity.DEFAULT_VISION_MODEL,
    val codegenModel: String = AppSettingEntity.DEFAULT_CODEGEN_MODEL
) {
    val isKeyConfigured: Boolean
        get() = groqApiKey.trim().isNotBlank()
}
