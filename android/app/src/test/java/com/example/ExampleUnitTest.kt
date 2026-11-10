package com.example

import com.example.data.remote.UpdateChecker
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests verifying core business logic including update checker version comparisons.
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun updateChecker_isVersionNewer_correctlyIdentifiesNewReleases() {
    assertTrue(UpdateChecker.isVersionNewer("1.0.2", "1.0.1"))
    assertTrue(UpdateChecker.isVersionNewer("1.1.0", "1.0.9"))
    assertTrue(UpdateChecker.isVersionNewer("2.0.0", "1.9.9"))
    assertTrue(UpdateChecker.isVersionNewer("1.0.1", "1.0.0"))

    assertFalse(UpdateChecker.isVersionNewer("1.0.0", "1.0.0"))
    assertFalse(UpdateChecker.isVersionNewer("1.0.0", "1.0.1"))
    assertFalse(UpdateChecker.isVersionNewer("1.0.0", "2.0.0"))
    assertFalse(UpdateChecker.isVersionNewer("", "1.0.0"))
    assertFalse(UpdateChecker.isVersionNewer("1.0.0", ""))
  }
}

