package com.example.nexoftcontacts.presentation.components

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.*
import com.example.nexoftcontacts.R

@Composable
fun DoneAnimation(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    iterations: Int = 1
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.done)
    )
    
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = iterations,
        isPlaying = isPlaying,
        speed = 1f,
        restartOnPlay = false
    )
    
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}
