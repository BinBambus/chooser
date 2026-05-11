package com.example.chooser

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


enum class ChooserState {
    WAITING, COUNTING, HIGHLIGHTING, DEHIGHLIGHTING, WINNER
}

enum class VibrationType {
    TICK,
    WINNER
}

@Composable
fun App(onVibrate: (VibrationType) -> Unit) {
    var activePointers by remember { mutableStateOf<Map<Long, Offset>>(emptyMap()) }
    var gameState by remember { mutableStateOf(ChooserState.WAITING) }
    var winnerId by remember { mutableStateOf<Long?>(null) }

    // Coroutine reacts to finger count
    LaunchedEffect(activePointers.size) {
        if (activePointers.size < 2) {
            gameState = ChooserState.WAITING
            winnerId = null
            return@LaunchedEffect
        }

        // Phase 1: Wait 2 Seconds (Give users, chance to put down their finger)
        gameState = ChooserState.COUNTING
        delay(2000)

        // Make rings pulsate
        for (i in 0..1){
            // Phase 2: Rings light up & phone vibrates
            gameState = ChooserState.HIGHLIGHTING
            onVibrate(VibrationType.TICK)
            delay(750)

            // Phase 3: Rings return to normal
            gameState = ChooserState.DEHIGHLIGHTING
            delay(750)
        }

        // Phase 4: Choose winner & vibrate 1 sec
        winnerId = activePointers.keys.random()
        gameState = ChooserState.WINNER
        onVibrate(VibrationType.WINNER)
    }

    // Build UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF6A1DC0), Color(0xFF29AEAE))))
    ) {
        if (activePointers.isEmpty()){
            Text(
                text = "CHOOSER",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val currentPointers = mutableMapOf<Long, Offset>()

                            event.changes.forEach { change ->
                                if (change.pressed) {
                                    currentPointers[change.id.value] = change.position
                                }
                                change.consume()
                            }
                            activePointers = currentPointers
                        }
                    }
                }
        ) {
            // Draw Rings according to state
            activePointers.forEach { (id, position) ->
                when (gameState) {
                    ChooserState.WAITING, ChooserState.COUNTING -> {
                        // White ring
                        drawCircle(color = Color.White, radius = 150f, center = position, style = Stroke(width = 15f))
                    }
                    ChooserState.HIGHLIGHTING -> {
                        // "Shiny" ring
                        drawCircle(color = Color.White, radius = 170f, center = position, style = Stroke(width = 25f))
                    }
                    ChooserState.DEHIGHLIGHTING -> {
                        // White ring
                        drawCircle(color = Color.White, radius = 170f, center = position, style = Stroke(width = 15f))
                    }
                    ChooserState.WINNER -> {
                        if (id == winnerId) {
                            // Winner, filled circle
                            drawCircle(color = Color.White, radius = 180f, center = position)
                        }
                    }
                }
            }
        }
    }
}