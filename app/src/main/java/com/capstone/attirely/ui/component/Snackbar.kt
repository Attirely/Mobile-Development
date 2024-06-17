package com.capstone.attirely.ui.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.capstone.attirely.data.SnackbarState
import kotlinx.coroutines.delay

@Composable
fun Snackbar(
    title: String,
    message: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth(0.95f)
            .padding(top = 10.dp)
            .height(100.dp)
            .clip(shape = RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(
                    radiusX = 500.dp,
                    radiusY = 500.dp,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                )
                .background(backgroundColor),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
            )
        }
    }
}

@Composable
fun SnackbarHost(
    snackbarState: SnackbarState?,
    onDismiss: () -> Unit
) {
    var showSnackbar by remember { mutableStateOf(false) }

    LaunchedEffect(snackbarState) {
        if (snackbarState != null) {
            Log.d("SnackbarHost", "Showing Snackbar: ${snackbarState.title}")
            showSnackbar = true
            delay(4000)
            showSnackbar = false
            onDismiss()
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .zIndex(1f)) {
        AnimatedVisibility(
            visible = showSnackbar,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(durationMillis = 300)
            ),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Snackbar(
                title = snackbarState?.title ?: "",
                message = snackbarState?.message ?: "",
                backgroundColor = snackbarState?.backgroundColor ?: Color.Green
            )
        }
    }
}