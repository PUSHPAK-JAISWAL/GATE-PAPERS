package com.example

import com.example.data.model.UserSettings
import com.example.data.model.VideoStoryboard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun userSettings_detectsConfiguredKey() {
    val unconfigured = UserSettings()
    assertFalse(unconfigured.isKeyConfigured)

    val configured = UserSettings(groqApiKey = "gsk_test123456789")
    assertTrue(configured.isKeyConfigured)
  }

  @Test
  fun videoStoryboard_parsesValidJson() {
    val json = """
      {
        "title": "Dijkstra's Algorithm Walkthrough",
        "topic": "Graph Theory",
        "totalDurationSec": 50,
        "scenes": [
          {
            "sceneNumber": 1,
            "title": "Basis: Greedy Strategy & Non-Negative Weights",
            "durationSec": 12,
            "narration": "First, recall why Dijkstra requires non-negative edge weights.",
            "visualType": "basis_concept",
            "bulletPoints": ["Non-negative weights invariant", "Greedy relaxation"],
            "formulaOrCode": "dist[v] = min(dist[v], dist[u] + w(u,v))",
            "keyBadge": "Foundation"
          },
          {
            "sceneNumber": 2,
            "title": "Step-by-Step Graph Execution",
            "durationSec": 15,
            "narration": "Relax node A then node B in order.",
            "visualType": "step_solution",
            "bulletPoints": ["Relax node A", "Relax node B"],
            "keyBadge": "Execution"
          }
        ]
      }
    """.trimIndent()

    val storyboard = VideoStoryboard.fromJson(json)
    assertEquals("Dijkstra's Algorithm Walkthrough", storyboard.title)
    assertEquals("Graph Theory", storyboard.topic)
    assertEquals(2, storyboard.scenes.size)
    assertEquals(0, storyboard.scenes[0].startSec)
    assertEquals(12, storyboard.scenes[0].endSec)
    assertEquals(12, storyboard.scenes[1].startSec)
    assertEquals(27, storyboard.scenes[1].endSec)
  }

  @Test
  fun videoStoryboard_createsFallbackGracefully() {
    val fallback = VideoStoryboard.createFallbackStoryboard(
      topic = "Pipelining Hazards",
      basisText = "CPI calculation and stalls",
      solutionText = "Stall cycles = 2"
    )

    assertEquals("Pipelining Hazards", fallback.title)
    assertTrue(fallback.scenes.isNotEmpty())
    assertEquals(5, fallback.scenes.size)
    assertEquals(0, fallback.scenes.first().startSec)
  }
}
