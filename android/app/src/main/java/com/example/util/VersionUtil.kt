package com.example.util

/**
 * Utility for formatting and normalizing versions according to the format:
 * 1-x.0-9.0-9 (Major is >= 1, Minor is strictly 0..9, Patch is strictly 0..9).
 * Ensures no version strings like "1.0.37" or "1.0.12" are displayed.
 */
object VersionUtil {

    /**
     * Normalizes a version string (e.g. "1.0.12", "v1.0.37", "1.1.2") to match 1-x.0-9.0-9.
     */
    fun normalize(versionStr: String): String {
        val clean = versionStr.trim().removePrefix("v").removePrefix("V")
        val parts = clean.split(".", "-").mapNotNull { it.toIntOrNull() }
        if (parts.isEmpty()) return "1.0.0"

        var major = maxOf(1, parts.getOrElse(0) { 1 })
        var minor = parts.getOrElse(1) { 0 }
        var patch = parts.getOrElse(2) { 0 }

        // Rollover patch if > 9
        if (patch > 9) {
            minor += patch / 10
            patch %= 10
        }

        // Rollover minor if > 9
        if (minor > 9) {
            major += minor / 10
            minor %= 10
        }

        return "$major.$minor.$patch"
    }

    /**
     * Formats a sequential integer (like build or run number) into 1-x.0-9.0-9.
     * e.g., 12 -> "1.1.2", 37 -> "1.3.7", 100 -> "2.0.0"
     */
    fun fromRunNumber(runNumber: Int): String {
        val safe = maxOf(0, runNumber)
        val major = 1 + (safe / 100)
        val minor = (safe / 10) % 10
        val patch = safe % 10
        return "$major.$minor.$patch"
    }
}
