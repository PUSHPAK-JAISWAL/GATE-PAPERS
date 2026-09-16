package com.example.data.remote

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.data.model.VideoStoryboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

data class ConceptAndSolutionResult(
    val topic: String,
    val prerequisiteBasis: String,
    val stepSolution: String,
    val rawAnalysis: String
)

object GroqApiClient {
    private const val TAG = "GroqApiClient"
    private const val CHAT_COMPLETIONS_URL = "https://api.groq.com/openai/v1/chat/completions"
    private const val MODELS_URL = "https://api.groq.com/openai/v1/models"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Tests the provided Groq API key by checking models endpoint.
     */
    suspend fun testApiKey(apiKey: String): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("API Key cannot be empty."))
        }

        try {
            val request = Request.Builder()
                .url(MODELS_URL)
                .header("Authorization", "Bearer ${apiKey.trim()}")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (response.isSuccessful) {
                Result.success("Connection Successful! Groq API is ready.")
            } else {
                val errorMsg = try {
                    val json = JSONObject(body)
                    json.optJSONObject("error")?.optString("message") ?: "HTTP ${response.code}"
                } catch (e: Exception) {
                    "HTTP ${response.code}: $body"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Phase 1: Analyzes the question and optional image/diagram using Groq Vision Model (e.g. qwen/qwen3.6-27b).
     * Extracts:
     * 1. The fundamental prerequisite basis needed before solving.
     * 2. The step-by-step resolution of the question / diagram.
     */
    suspend fun analyzeBasisAndSolution(
        apiKey: String,
        model: String,
        questionText: String,
        bitmap: Bitmap?
    ): Result<ConceptAndSolutionResult> = withContext(Dispatchers.IO) {
        try {
            val systemPrompt = """
                You are a senior GATE (Graduate Aptitude Test in Engineering - CS & DA) professor and visual tutor.
                The user is presenting an exam question or diagram.
                
                YOUR PEDAGOGICAL GOAL:
                Before solving the question directly, a student must thoroughly master the *underlying fundamental basis* that makes the problem solvable.
                
                Analyze the question and any diagram provided, then respond in the following EXACT structured markdown format:
                
                ### TOPIC
                [Name of the exact GATE subject and subtopic, e.g. "Data Structures: Red-Black Tree Rotations" or "Computer Networks: TCP Congestion Control"]
                
                ### PREREQUISITE BASIS & FUNDAMENTAL PRINCIPLES
                [Explain clearly the fundamental definitions, theorems, formulas, or conceptual models needed FIRST to approach this problem. Explain as if teaching the foundation to a student so they understand WHY this problem works.]
                
                ### STEP-BY-STEP PROBLEM RESOLUTION
                [Break down the specific question and diagram:
                - Given parameters and diagram analysis
                - Step-by-step mathematical or logical calculation
                - Verification of options (if MCQ/MSQ) or exact calculation (if NAT)
                - Clear Final Answer]
            """.trimIndent()

            val messagesArray = JSONArray()

            // System message
            messagesArray.put(
                JSONObject().apply {
                    put("role", "system")
                    put("content", systemPrompt)
                }
            )

            // User message with text and optional image
            val userContent = JSONArray()
            val promptText = if (questionText.isNotBlank()) {
                questionText
            } else {
                "Please analyze this GATE question diagram, explain the underlying basis first, and then solve the question step-by-step."
            }

            userContent.put(
                JSONObject().apply {
                    put("type", "text")
                    put("text", promptText)
                }
            )

            if (bitmap != null) {
                val base64Image = bitmapToBase64(bitmap)
                userContent.put(
                    JSONObject().apply {
                        put("type", "image_url")
                        put(
                            "image_url",
                            JSONObject().apply {
                                put("url", "data:image/jpeg;base64,$base64Image")
                            }
                        )
                    }
                )
            }

            messagesArray.put(
                JSONObject().apply {
                    put("role", "user")
                    put("content", userContent)
                }
            )

            val requestJson = JSONObject().apply {
                put("model", model.ifBlank { "qwen/qwen3.6-27b" })
                put("messages", messagesArray)
                put("temperature", 0.3)
                put("max_completion_tokens", 2500)
            }

            val request = Request.Builder()
                .url(CHAT_COMPLETIONS_URL)
                .header("Authorization", "Bearer ${apiKey.trim()}")
                .header("Content-Type", "application/json")
                .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val bodyStr = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = try {
                    JSONObject(bodyStr).optJSONObject("error")?.optString("message") ?: "Error ${response.code}"
                } catch (e: Exception) {
                    "Error ${response.code}: $bodyStr"
                }
                return@withContext Result.failure(Exception(errorMsg))
            }

            val respJson = JSONObject(bodyStr)
            val content = respJson.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")

            // Parse structured sections
            var topic = "GATE Fundamental Concept"
            var basis = ""
            var solution = ""

            val topicMatch = Regex("###\\s*TOPIC\\s*\\n([^#]+)", RegexOption.IGNORE_CASE).find(content)
            if (topicMatch != null) {
                topic = topicMatch.groupValues[1].trim()
            }

            val basisMatch = Regex("###\\s*PREREQUISITE BASIS[^#]*\\n([^#]+)", RegexOption.IGNORE_CASE).find(content)
            if (basisMatch != null) {
                basis = basisMatch.groupValues[1].trim()
            }

            val solMatch = Regex("###\\s*STEP-BY-STEP[^#]*\\n([\\s\\S]+)", RegexOption.IGNORE_CASE).find(content)
            if (solMatch != null) {
                solution = solMatch.groupValues[1].trim()
            }

            if (basis.isBlank() && solution.isBlank()) {
                basis = content
                solution = "See comprehensive analysis above."
            }

            Result.success(
                ConceptAndSolutionResult(
                    topic = topic,
                    prerequisiteBasis = basis,
                    stepSolution = solution,
                    rawAnalysis = content
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error in analyzeBasisAndSolution", e)
            Result.failure(e)
        }
    }

    /**
     * Phase 2: Generates the synchronized Animated Video Script & Timeline JSON using a powerful model (e.g. llama-3.3-70b-versatile or qwen/qwen3.8-27b).
     * The timing strictly matches the script for the video player.
     */
    suspend fun generateVideoStoryboard(
        apiKey: String,
        model: String,
        analysis: ConceptAndSolutionResult
    ): Result<VideoStoryboard> = withContext(Dispatchers.IO) {
        try {
            val systemPrompt = """
                You are an expert educational video animator and director.
                Create a complete synchronized Animated Video Storyboard in JSON format.
                
                The video MUST follow this pedagogical arc:
                - Scene 1: Concept Introduction & Question Overview
                - Scene 2: Prerequisite Foundations & Core Basis (Crucial: first explain the basics)
                - Scene 3: Diagram & Parameter Inspection
                - Scene 4: Step-by-Step Resolution / Math Logic
                - Scene 5: Final Result, Common Exam Traps, and Key Takeaway
                
                EACH scene must have:
                - sceneNumber (1, 2, ...)
                - title (Punchy, max 5-7 words)
                - durationSec (exact integer between 8 and 16 seconds; total video should be between 50 and 75 seconds)
                - narration (2-3 spoken sentences that cleanly fit the duration. The player will narrate this and sync with audio/subtitles)
                - visualType ("basis_concept" | "diagram_breakdown" | "step_solution" | "formula_calc" | "takeaway")
                - bulletPoints (list of 2-3 concise summary strings to draw on canvas)
                - formulaOrCode (optional string for key mathematical expression or formula)
                - diagramLabel (optional label describing the visual focus)
                - keyBadge (short badge label, e.g. "Prerequisite", "Calculation", "Takeaway")
                
                OUTPUT FORMAT:
                Output ONLY raw valid JSON with no conversational text or markdown code fences:
                {
                  "title": "${analysis.topic}",
                  "topic": "${analysis.topic}",
                  "totalDurationSec": 60,
                  "scenes": [
                    {
                      "sceneNumber": 1,
                      "title": "...",
                      "durationSec": 10,
                      "narration": "...",
                      "visualType": "basis_concept",
                      "bulletPoints": ["...", "..."],
                      "formulaOrCode": "...",
                      "diagramLabel": "...",
                      "keyBadge": "Foundation"
                    }
                  ]
                }
            """.trimIndent()

            val userContent = """
                Here is the verified GATE topic and solution analysis:
                
                Topic: ${analysis.topic}
                
                Prerequisite Basis:
                ${analysis.prerequisiteBasis}
                
                Step-by-Step Solution:
                ${analysis.stepSolution}
                
                Generate the exact synchronized Animated Video Storyboard JSON now.
            """.trimIndent()

            val requestJson = JSONObject().apply {
                put("model", model.ifBlank { "llama-3.3-70b-versatile" })
                put(
                    "messages",
                    JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "system")
                            put("content", systemPrompt)
                        })
                        put(JSONObject().apply {
                            put("role", "user")
                            put("content", userContent)
                        })
                    }
                )
                put("temperature", 0.2)
                put("max_completion_tokens", 2500)
            }

            val request = Request.Builder()
                .url(CHAT_COMPLETIONS_URL)
                .header("Authorization", "Bearer ${apiKey.trim()}")
                .header("Content-Type", "application/json")
                .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val bodyStr = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                // If the second model fails, fallback to creating storyboard directly from analysis
                Log.w(TAG, "Storyboard generation failed: HTTP ${response.code}")
                return@withContext Result.success(
                    VideoStoryboard.createFallbackStoryboard(
                        topic = analysis.topic,
                        basisText = analysis.prerequisiteBasis,
                        solutionText = analysis.stepSolution
                    )
                )
            }

            val respJson = JSONObject(bodyStr)
            val rawOutput = respJson.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")

            val storyboard = VideoStoryboard.fromJson(rawOutput)
            Result.success(storyboard)
        } catch (e: Exception) {
            Log.e(TAG, "Fallback storyboard due to error", e)
            Result.success(
                VideoStoryboard.createFallbackStoryboard(
                    topic = analysis.topic,
                    basisText = analysis.prerequisiteBasis,
                    solutionText = analysis.stepSolution
                )
            )
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        // Resize bitmap if very large to optimize bandwidth & latency (max 1280px)
        val maxDim = 1280
        val width = bitmap.width
        val height = bitmap.height
        val scaledBitmap = if (width > maxDim || height > maxDim) {
            val ratio = width.toFloat() / height.toFloat()
            val newWidth = if (ratio > 1f) maxDim else (maxDim * ratio).toInt()
            val newHeight = if (ratio > 1f) (maxDim / ratio).toInt() else maxDim
            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } else {
            bitmap
        }

        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}
