package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "ai_explanations")
data class AiExplanationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val questionText: String,
    val imagePath: String? = null,
    val topic: String,
    val prerequisiteBasis: String,
    val stepSolution: String,
    val videoScriptJson: String,
    val totalDurationSec: Int = 60
)

data class VideoScene(
    val sceneNumber: Int,
    val title: String,
    val durationSec: Int,
    val startSec: Int,
    val endSec: Int,
    val narration: String,
    val visualType: String, // "basis_concept", "diagram_breakdown", "step_solution", "formula_calc", "takeaway"
    val bulletPoints: List<String> = emptyList(),
    val formulaOrCode: String? = null,
    val diagramLabel: String? = null,
    val keyBadge: String? = null
)

data class VideoStoryboard(
    val title: String,
    val topic: String,
    val totalDurationSec: Int,
    val scenes: List<VideoScene>
) {
    companion object {
        fun fromJson(jsonStr: String): VideoStoryboard {
            return try {
                val clean = jsonStr.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
                val obj = JSONObject(clean)
                val title = obj.optString("title", "Topic & Problem Explanation")
                val topic = obj.optString("topic", "GATE Fundamental Concepts")
                val scenesArray = obj.optJSONArray("scenes") ?: JSONArray()
                val scenes = mutableListOf<VideoScene>()
                var cumulativeTime = 0

                for (i in 0 until scenesArray.length()) {
                    val sObj = scenesArray.getJSONObject(i)
                    val duration = sObj.optInt("durationSec", 10).coerceIn(4, 30)
                    val start = cumulativeTime
                    val end = start + duration
                    cumulativeTime = end

                    val bullets = mutableListOf<String>()
                    val bArr = sObj.optJSONArray("bulletPoints")
                    if (bArr != null) {
                        for (b in 0 until bArr.length()) {
                            bullets.add(bArr.getString(b))
                        }
                    }

                    scenes.add(
                        VideoScene(
                            sceneNumber = sObj.optInt("sceneNumber", i + 1),
                            title = sObj.optString("title", "Scene ${i + 1}"),
                            durationSec = duration,
                            startSec = start,
                            endSec = end,
                            narration = sObj.optString("narration", ""),
                            visualType = sObj.optString("visualType", "basis_concept"),
                            bulletPoints = bullets,
                            formulaOrCode = sObj.optString("formulaOrCode").takeIf { it.isNotBlank() },
                            diagramLabel = sObj.optString("diagramLabel").takeIf { it.isNotBlank() },
                            keyBadge = sObj.optString("keyBadge").takeIf { it.isNotBlank() }
                        )
                    )
                }

                VideoStoryboard(
                    title = title,
                    topic = topic,
                    totalDurationSec = if (cumulativeTime > 0) cumulativeTime else 60,
                    scenes = scenes
                )
            } catch (e: Exception) {
                // Fallback default storyboard
                createFallbackStoryboard("GATE Topic & Solution", "Concept Basis & Walkthrough")
            }
        }

        fun createFallbackStoryboard(
            topic: String,
            basisText: String,
            solutionText: String = ""
        ): VideoStoryboard {
            val scenes = listOf(
                VideoScene(
                    sceneNumber = 1,
                    title = "Topic Overview & Prerequisite Basis",
                    durationSec = 12,
                    startSec = 0,
                    endSec = 12,
                    narration = "Welcome. Before solving this question, let's understand the core fundamental principles behind $topic.",
                    visualType = "basis_concept",
                    bulletPoints = listOf(
                        "Identify the fundamental GATE concept",
                        "Recall core theorems and properties",
                        "Establish the groundwork before attacking the problem"
                    ),
                    keyBadge = "Foundations First"
                ),
                VideoScene(
                    sceneNumber = 2,
                    title = "Core Principles & Formulas",
                    durationSec = 14,
                    startSec = 12,
                    endSec = 26,
                    narration = if (basisText.isNotBlank()) basisText.take(200) else "Analyze definitions and prerequisites required for this question.",
                    visualType = "formula_calc",
                    bulletPoints = listOf(
                        "Fundamental relation governing this domain",
                        "Key boundary conditions & constraints"
                    ),
                    formulaOrCode = "Recall: Fundamental Theorem & Properties",
                    keyBadge = "Prerequisites"
                ),
                VideoScene(
                    sceneNumber = 3,
                    title = "Diagram & Given State Analysis",
                    durationSec = 12,
                    startSec = 26,
                    endSec = 38,
                    narration = "Now, carefully inspect the given problem statements and visual parameters.",
                    visualType = "diagram_breakdown",
                    bulletPoints = listOf(
                        "Trace given inputs and diagrams",
                        "Isolate test cases and invariants",
                        "Map to our foundation models"
                    ),
                    diagramLabel = "Visual Diagram & Input Trace",
                    keyBadge = "Deep Analysis"
                ),
                VideoScene(
                    sceneNumber = 4,
                    title = "Step-by-Step Resolution",
                    durationSec = 14,
                    startSec = 38,
                    endSec = 52,
                    narration = if (solutionText.isNotBlank()) solutionText.take(220) else "Executing step-by-step logic to deduce the correct result.",
                    visualType = "step_solution",
                    bulletPoints = listOf(
                        "Apply the extracted basis theorems",
                        "Eliminate incorrect options or edge conditions",
                        "Deduce exact answer with mathematical rigor"
                    ),
                    formulaOrCode = "Step 1: Apply basis -> Step 2: Resolve",
                    keyBadge = "Execution"
                ),
                VideoScene(
                    sceneNumber = 5,
                    title = "Final Answer & Exam Takeaways",
                    durationSec = 10,
                    startSec = 52,
                    endSec = 62,
                    narration = "Summary complete. Always remember the fundamental basis before attempting similar GATE questions.",
                    visualType = "takeaway",
                    bulletPoints = listOf(
                        "Key takeaway: Basis understanding simplifies solving",
                        "Watch out for common GATE trap choices",
                        "Practice related variations"
                    ),
                    keyBadge = "Mastery"
                )
            )

            return VideoStoryboard(
                title = topic,
                topic = topic,
                totalDurationSec = 62,
                scenes = scenes
            )
        }
    }
}
