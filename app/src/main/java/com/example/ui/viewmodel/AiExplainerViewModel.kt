package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AiExplanationEntity
import com.example.data.model.UserSettings
import com.example.data.model.VideoStoryboard
import com.example.data.remote.GroqApiClient
import com.example.data.repository.AiExplainerRepository
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class AiExplainerUiState(
    val selectedBitmap: Bitmap? = null,
    val questionText: String = "",
    val isGenerating: Boolean = false,
    val currentStepMessage: String = "",
    val activeStoryboard: VideoStoryboard? = null,
    val activeTopic: String = "",
    val activeBasis: String = "",
    val activeSolution: String = "",
    val errorMessage: String? = null
)

class AiExplainerViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val settingsRepo = SettingsRepository(db.settingsDao())
    private val explainerRepo = AiExplainerRepository(db.aiExplanationDao())

    val userSettings: StateFlow<UserSettings> = settingsRepo.userSettingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSettings()
    )

    val pastExplanations: StateFlow<List<AiExplanationEntity>> = explainerRepo.allExplanationsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _uiState = MutableStateFlow(AiExplainerUiState())
    val uiState: StateFlow<AiExplainerUiState> = _uiState.asStateFlow()

    fun setQuestionText(text: String) {
        _uiState.value = _uiState.value.copy(questionText = text, errorMessage = null)
    }

    fun setSelectedBitmap(bitmap: Bitmap?) {
        _uiState.value = _uiState.value.copy(selectedBitmap = bitmap, errorMessage = null)
    }

    fun loadBitmapFromUri(uri: Uri) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                _uiState.value = _uiState.value.copy(selectedBitmap = bitmap, errorMessage = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Failed to load image: ${e.message}")
            }
        }
    }

    fun clearImage() {
        _uiState.value = _uiState.value.copy(selectedBitmap = null)
    }

    fun clearCurrentVideo() {
        _uiState.value = _uiState.value.copy(
            activeStoryboard = null,
            activeTopic = "",
            activeBasis = "",
            activeSolution = "",
            errorMessage = null
        )
    }

    fun generateExplanation() {
        val currentState = _uiState.value
        val apiKey = userSettings.value.groqApiKey

        if (apiKey.isBlank()) {
            _uiState.value = currentState.copy(
                errorMessage = "Groq API Key is not configured. Please add your key in the Settings tab."
            )
            return
        }

        if (currentState.questionText.isBlank() && currentState.selectedBitmap == null) {
            _uiState.value = currentState.copy(
                errorMessage = "Please enter a question or upload a diagram/problem image."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(
                isGenerating = true,
                errorMessage = null,
                currentStepMessage = "Step 1/2: Analyzing question & diagrams with ${userSettings.value.visionModel}..."
            )

            // Step 1: Vision / Multimodal analysis of prerequisite basis and solution
            val basisResult = GroqApiClient.analyzeBasisAndSolution(
                apiKey = apiKey,
                model = userSettings.value.visionModel,
                questionText = currentState.questionText,
                bitmap = currentState.selectedBitmap
            )

            if (basisResult.isFailure) {
                val err = basisResult.exceptionOrNull()?.message ?: "Failed during vision analysis."
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    currentStepMessage = "",
                    errorMessage = err
                )
                return@launch
            }

            val analysis = basisResult.getOrThrow()

            // Step 2: Storyboard & Animated Video Script generation
            _uiState.value = _uiState.value.copy(
                currentStepMessage = "Step 2/2: Generating synchronized animated video timeline with ${userSettings.value.codegenModel}..."
            )

            val storyboardResult = GroqApiClient.generateVideoStoryboard(
                apiKey = apiKey,
                model = userSettings.value.codegenModel,
                analysis = analysis
            )

            val storyboard = storyboardResult.getOrDefault(
                VideoStoryboard.createFallbackStoryboard(
                    analysis.topic,
                    analysis.prerequisiteBasis,
                    analysis.stepSolution
                )
            )

            // Save locally to cache if image present
            var savedImagePath: String? = null
            if (currentState.selectedBitmap != null) {
                savedImagePath = saveBitmapToInternalStorage(currentState.selectedBitmap)
            }

            // Persist to Room SQLite
            val entity = AiExplanationEntity(
                questionText = currentState.questionText,
                imagePath = savedImagePath,
                topic = analysis.topic,
                prerequisiteBasis = analysis.prerequisiteBasis,
                stepSolution = analysis.stepSolution,
                videoScriptJson = "", // storyboard can be re-derived or loaded
                totalDurationSec = storyboard.totalDurationSec
            )
            explainerRepo.saveExplanation(entity)

            _uiState.value = _uiState.value.copy(
                isGenerating = false,
                currentStepMessage = "",
                activeStoryboard = storyboard,
                activeTopic = analysis.topic,
                activeBasis = analysis.prerequisiteBasis,
                activeSolution = analysis.stepSolution,
                errorMessage = null
            )
        }
    }

    fun loadPastExplanation(entity: AiExplanationEntity) {
        viewModelScope.launch {
            var loadedBitmap: Bitmap? = null
            if (!entity.imagePath.isNullOrBlank()) {
                val file = File(entity.imagePath)
                if (file.exists()) {
                    loadedBitmap = BitmapFactory.decodeFile(file.absolutePath)
                }
            }

            val storyboard = VideoStoryboard.createFallbackStoryboard(
                topic = entity.topic,
                basisText = entity.prerequisiteBasis,
                solutionText = entity.stepSolution
            )

            _uiState.value = _uiState.value.copy(
                selectedBitmap = loadedBitmap,
                questionText = entity.questionText,
                activeStoryboard = storyboard,
                activeTopic = entity.topic,
                activeBasis = entity.prerequisiteBasis,
                activeSolution = entity.stepSolution,
                errorMessage = null
            )
        }
    }

    fun deletePastExplanation(id: Long) {
        viewModelScope.launch {
            explainerRepo.deleteExplanation(id)
        }
    }

    private suspend fun saveBitmapToInternalStorage(bitmap: Bitmap): String? = withContext(Dispatchers.IO) {
        try {
            val context = getApplication<Application>()
            val dir = File(context.filesDir, "ai_diagrams").apply { mkdirs() }
            val file = File(dir, "diagram_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
