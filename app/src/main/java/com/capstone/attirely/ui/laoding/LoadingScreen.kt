package com.capstone.attirely.ui.laoding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capstone.attirely.R
import com.capstone.attirely.ui.home.polyFontFamily

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize().background(color = colorResource(id = R.color.primary))){
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top) {
            Text(
                text = stringResource(id = R.string.analyzing),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontFamily = polyFontFamily,
                modifier = Modifier.padding(100.dp)
            )
            CircularProgressIndicator(
                color = colorResource(id = R.color.white),
                modifier = Modifier.padding(16.dp)
            )
        }
        Image(
            painter = painterResource(id = R.drawable.choosing_outfit),
            contentDescription = "Choosing Outfit Illustration",
            modifier = Modifier.padding(16.dp).fillMaxWidth().align(Alignment.BottomCenter)
        )
    }
}

@Preview
@Composable
fun LoadingScreenPreview() {
    LoadingScreen()
}