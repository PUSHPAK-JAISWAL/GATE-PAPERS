package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.UserSettings
import com.example.data.remote.GroqApiClient
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

sealed class KeyTestStatus {
    object Idle : KeyTestStatus()
    object Loading : KeyTestStatus()
    data class Success(val message: String) : KeyTestStatus()
    data class Error(val message: String) : KeyTestStatus()
}

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(
        AppDatabase.getInstance(application).settingsDao()
    )

    val settings: StateFlow<UserSettings> = repository.userSettingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSettings()
    )

    private val _testStatus = MutableStateFlow<KeyTestStatus>(KeyTestStatus.Idle)
    val testStatus: StateFlow<KeyTestStatus> = _testStatus.asStateFlow()

    private val _cacheSizeMb = MutableStateFlow<Double>(0.0)
    val cacheSizeMb: StateFlow<Double> = _cacheSizeMb.asStateFlow()

    init {
        calculateCacheSize()
    }

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            repository.saveApiKey(key)
            _testStatus.value = KeyTestStatus.Idle
        }
    }

    fun saveVisionModel(model: String) {
        viewModelScope.launch {
            repository.saveVisionModel(model)
        }
    }

    fun saveCodegenModel(model: String) {
        viewModelScope.launch {
            repository.saveCodegenModel(model)
        }
    }

    fun testConnection(apiKey: String) {
        viewModelScope.launch {
            _testStatus.value = KeyTestStatus.Loading
            val keyToTest = apiKey.ifBlank { settings.value.groqApiKey }
            if (keyToTest.isBlank()) {
                _testStatus.value = KeyTestStatus.Error("Please enter an API Key first.")
                return@launch
            }

            val result = GroqApiClient.testApiKey(keyToTest)
            result.onSuccess { msg ->
                _testStatus.value = KeyTestStatus.Success(msg)
            }.onFailure { err ->
                _testStatus.value = KeyTestStatus.Error(err.message ?: "Connection failed.")
            }
        }
    }

    fun clearApiKey() {
        viewModelScope.launch {
            repository.clearApiKey()
            _testStatus.value = KeyTestStatus.Idle
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            try {
                val cacheDir = getApplication<Application>().cacheDir
                deleteDirContent(cacheDir)
                calculateCacheSize()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun calculateCacheSize() {
        viewModelScope.launch {
            try {
                val cacheDir = getApplication<Application>().cacheDir
                val bytes = getDirSize(cacheDir)
                _cacheSizeMb.value = bytes.toDouble() / (1024.0 * 1024.0)
            } catch (e: Exception) {
                _cacheSizeMb.value = 0.0
            }
        }
    }

    private fun getDirSize(dir: File): Long {
        var size = 0L
        dir.listFiles()?.forEach { file ->
            size += if (file.isDirectory) getDirSize(file) else file.length()
        }
        return size
    }

    private fun deleteDirContent(dir: File) {
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                deleteDirContent(file)
            }
            file.delete()
        }
    }
}
