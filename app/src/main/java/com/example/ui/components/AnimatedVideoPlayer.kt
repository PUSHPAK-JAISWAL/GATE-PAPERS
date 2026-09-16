package com.example.ui.components

import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VideoScene
import com.example.data.model.VideoStoryboard
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.SolidAmber
import com.example.ui.theme.SolidEmerald
import com.example.ui.theme.SolidGateCyan
import com.example.ui.theme.SolidGateOrange
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun AnimatedVideoPlayer(
    storyboard: VideoStoryboard,
    diagramBitmap: Bitmap?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val totalDurationSec = storyboard.totalDurationSec.coerceAtLeast(1)

    // Player state
    var isPlaying by remember { mutableStateOf(true) }
    var currentSec by remember { mutableFloatStateOf(0f) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var isMuted by remember { mutableStateOf(false) }

    // TTS Voice
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }
    var lastSpokenSceneIdx by remember { mutableIntStateOf(-1) }

    DisposableEffect(Unit) {
        var tts: TextToSpeech? = null
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                isTtsReady = true
            }
        }
        ttsEngine = tts

        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    // Active scene determination
    val currentSceneIndex by remember(storyboard, currentSec) {
        derivedStateOf {
            val sList = storyboard.scenes
            val idx = sList.indexOfFirst { scene ->
                currentSec >= scene.startSec && currentSec < scene.endSec
            }
            if (idx != -1) idx else (sList.size - 1).coerceAtLeast(0)
        }
    }

    val activeScene: VideoScene? by remember(storyboard, currentSceneIndex) {
        derivedStateOf {
            if (storyboard.scenes.isNotEmpty() && currentSceneIndex in storyboard.scenes.indices) {
                storyboard.scenes[currentSceneIndex]
            } else null
        }
    }

    // Synchronize TTS speech with active scene
    LaunchedEffect(currentSceneIndex, isPlaying, isMuted, isTtsReady) {
        if (!isMuted && isPlaying && isTtsReady && activeScene != null) {
            if (lastSpokenSceneIdx != currentSceneIndex) {
                lastSpokenSceneIdx = currentSceneIndex
                ttsEngine?.stop()
                val textToSpeak = activeScene?.narration.orEmpty()
                if (textToSpeak.isNotBlank()) {
                    ttsEngine?.setSpeechRate(playbackSpeed)
                    ttsEngine?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "scene_$currentSceneIndex")
                }
            }
        } else if (!isPlaying || isMuted) {
            ttsEngine?.stop()
        }
    }

    // Playback loop
    LaunchedEffect(isPlaying, playbackSpeed, totalDurationSec) {
        while (isPlaying) {
            delay(50L)
            val step = 0.05f * playbackSpeed
            if (currentSec + step >= totalDurationSec.toFloat()) {
                currentSec = totalDurationSec.toFloat()
                isPlaying = false
                break
            } else {
                currentSec += step
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
    ) {
        // --- 16:9 CINEMATIC CANVAS VIEWPORT ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9.5f)
                .background(Color(0xFF070C12))
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
        ) {
            // Dynamic Grid / Particle background
            PlayerCanvasBackground()

            // Visual content layer
            if (activeScene != null) {
                val scene = activeScene!!
                val sceneProgress = ((currentSec - scene.startSec) / scene.durationSec.toFloat()).coerceIn(0f, 1f)

                SceneVisualStage(
                    scene = scene,
                    sceneProgress = sceneProgress,
                    diagramBitmap = diagramBitmap,
                    totalScenes = storyboard.scenes.size
                )
            }

            // Top HUD: Title & Scene badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(SolidGateOrange, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Scene ${currentSceneIndex + 1}/${storyboard.scenes.size}: ${activeScene?.title ?: ""}",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (activeScene?.keyBadge != null) {
                    Box(
                        modifier = Modifier
                            .background(SolidGateCyan.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .border(0.5.dp, SolidGateCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = activeScene?.keyBadge ?: "",
                            color = SolidGateCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Bottom HUD: Subtitles / Narration
            if (!activeScene?.narration.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC000000), Color(0xF0000000))
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = SolidGateOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = activeScene?.narration.orEmpty(),
                            color = Color.White,
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // --- SCRUBBER & TIMELINE CONTROLS ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Scrub Slider
            Slider(
                value = currentSec.coerceIn(0f, totalDurationSec.toFloat()),
                onValueChange = { newSec ->
                    currentSec = newSec
                    lastSpokenSceneIdx = -1 // Reset so TTS plays updated scene
                },
                valueRange = 0f..totalDurationSec.toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = SolidGateOrange,
                    activeTrackColor = SolidGateOrange,
                    inactiveTrackColor = DarkSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .testTag("video_scrub_slider")
            )

            // Time & Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Current / Total Timecode
                Text(
                    text = "${formatTime(currentSec.toInt())} / ${formatTime(totalDurationSec)}",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )

                // Playback Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Previous Scene
                    IconButton(
                        onClick = {
                            val prevIdx = (currentSceneIndex - 1).coerceAtLeast(0)
                            currentSec = storyboard.scenes.getOrNull(prevIdx)?.startSec?.toFloat() ?: 0f
                            lastSpokenSceneIdx = -1
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous Scene",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Play / Pause / Replay
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(SolidGateOrange)
                            .clickable {
                                if (currentSec >= totalDurationSec) {
                                    currentSec = 0f
                                    lastSpokenSceneIdx = -1
                                    isPlaying = true
                                } else {
                                    isPlaying = !isPlaying
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (currentSec >= totalDurationSec) {
                                Icons.Default.Replay
                            } else if (isPlaying) {
                                Icons.Default.Pause
                            } else {
                                Icons.Default.PlayArrow
                            },
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Next Scene
                    IconButton(
                        onClick = {
                            val nextIdx = (currentSceneIndex + 1).coerceAtMost(storyboard.scenes.size - 1)
                            currentSec = storyboard.scenes.getOrNull(nextIdx)?.startSec?.toFloat() ?: totalDurationSec.toFloat()
                            lastSpokenSceneIdx = -1
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next Scene",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Speed & Audio controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Voice Toggle
                    IconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                            contentDescription = "Toggle Narration Voice",
                            tint = if (isMuted) TextMuted else SolidGateCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Speed Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkSurfaceVariant)
                            .clickable {
                                playbackSpeed = when (playbackSpeed) {
                                    1.0f -> 1.25f
                                    1.25f -> 1.5f
                                    else -> 1.0f
                                }
                                ttsEngine?.setSpeechRate(playbackSpeed)
                            }
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${playbackSpeed}x",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Scene Chapters Selector
            Text(
                text = "TIMELINE SCENES",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(storyboard.scenes) { index, scene ->
                    val isSelected = index == currentSceneIndex
                    val chipColor = if (isSelected) SolidGateOrange else DarkSurfaceVariant
                    val textColor = if (isSelected) Color.White else TextSecondary

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(chipColor.copy(alpha = if (isSelected) 1f else 0.5f))
                            .border(
                                1.dp,
                                if (isSelected) SolidGateOrange else DarkBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                currentSec = scene.startSec.toFloat()
                                lastSpokenSceneIdx = -1
                                isPlaying = true
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${index + 1}. ",
                                color = textColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = scene.title,
                                color = textColor,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SceneVisualStage(
    scene: VideoScene,
    sceneProgress: Float,
    diagramBitmap: Bitmap?,
    totalScenes: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 34.dp),
        contentAlignment = Alignment.Center
    ) {
        when (scene.visualType) {
            "diagram_breakdown" -> {
                // Highlight diagram side-by-side with annotations
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (diagramBitmap != null) {
                        Box(
                            modifier = Modifier
                                .weight(0.45f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, SolidGateOrange.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                .background(Color.White)
                        ) {
                            Image(
                                bitmap = diagramBitmap.asImageBitmap(),
                                contentDescription = "Problem Diagram",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(if (diagramBitmap != null) 0.55f else 1f)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = scene.diagramLabel ?: "Diagram Parameter Trace",
                            color = SolidGateCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        scene.bulletPoints.forEachIndexed { idx, pt ->
                            val isRevealed = sceneProgress >= (idx * 0.25f)
                            AnimatedVisibility(
                                visible = isRevealed,
                                enter = fadeIn(tween(250))
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("▸ ", color = SolidGateOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(pt, color = TextPrimary, fontSize = 11.sp, lineHeight = 14.sp)
                                }
                            }
                        }
                    }
                }
            }

            "formula_calc", "step_solution" -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (!scene.formulaOrCode.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F1A24))
                                .border(1.dp, SolidGateCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Functions,
                                    contentDescription = null,
                                    tint = SolidGateCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = scene.formulaOrCode,
                                    color = SolidGateCyan,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Animated Steps
                    Column(modifier = Modifier.fillMaxWidth(0.9f)) {
                        scene.bulletPoints.forEachIndexed { idx, point ->
                            val visible = sceneProgress >= (idx * 0.3f)
                            if (visible) {
                                Row(
                                    modifier = Modifier
                                        .padding(vertical = 3.dp)
                                        .background(DarkSurfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .background(SolidEmerald, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${idx + 1}", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = point,
                                        color = TextPrimary,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                // Basis concept / Introduction / Takeaways
                Column(
                    modifier = Modifier.fillMaxWidth(0.92f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(SolidGateOrange.copy(alpha = 0.2f), CircleShape)
                            .border(1.dp, SolidGateOrange, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (scene.visualType == "takeaway") Icons.Default.School else Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = SolidGateOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = scene.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    scene.bulletPoints.forEachIndexed { idx, point ->
                        val isShown = sceneProgress >= (idx * 0.25f)
                        if (isShown) {
                            Text(
                                text = "• $point",
                                color = TextSecondary,
                                fontSize = 11.5.sp,
                                lineHeight = 15.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerCanvasBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Subtle tech grid lines
        val step = 40f
        var x = 0f
        while (x < width) {
            drawLine(
                color = Color(0x103B82F6),
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 0.5f
            )
            x += step
        }

        var y = 0f
        while (y < height) {
            drawLine(
                color = Color(0x103B82F6),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 0.5f
            )
            y += step
        }

        // Ambient radial glow in center
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x1F3B82F6), Color.Transparent),
                center = Offset(width / 2f, height / 2f),
                radius = width * 0.45f
            )
        )
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format(Locale.US, "%02d:%02d", m, s)
}
