package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "papers")
data class PaperEntity(
    @PrimaryKey val id: String,
    val section: String,             // "GATE CS" or "GATE DA"
    val year: Int,
    val title: String,               // e.g. "GATE CS 2025 (Shift 1)"
    val subtitle: String,            // e.g. "Computer Science and Information Technology"
    val setNumber: Int = 1,          // 1, 2, or 0
    val status: String = STATUS_UNATTEMPTED, // "UNATTEMPTED", "PENDING", "FINISHED"
    val completedAt: Long? = null,   // Epoch timestamp when marked finished
    val isFlaggedToRevisit: Boolean = false,
    val notes: String = "",
    val githubRepo: String,          // "PUSHPAK-JAISWAL/gatecs" or "PUSHPAK-JAISWAL/gateda"
    val githubFileName: String,      // e.g. "GATE_CS_2025_Shift1.pdf"
    val rawFileUrl: String,          // Direct raw GitHub link
    val fileSizeBytes: Long = 0L,    // File size in bytes
    val totalMarks: Int = 100,
    val durationMinutes: Int = 180,
    val totalQuestions: Int = 65,
    val coreTopics: String = ""      // e.g. "Algorithms, OS, DBMS, TOC, Networks"
) {
    companion object {
        const val SECTION_CS = "GATE CS"
        const val SECTION_DA = "GATE DA"

        const val STATUS_UNATTEMPTED = "UNATTEMPTED"
        const val STATUS_PENDING = "PENDING"
        const val STATUS_FINISHED = "FINISHED"
    }
}

data class QuestionItem(
    val number: Int,
    val sectionName: String,         // "General Aptitude" or "Core Subject"
    val type: String,                // "MCQ", "MSQ", "NAT"
    val marks: Int,                  // 1 or 2
    val negativeMarks: Double,       // 0.33, 0.66, or 0.0
    val topic: String,
    val questionText: String,
    val options: List<String> = emptyList(),
    val correctAnswer: String,
    val explanation: String
)
