package com.capstone.attirely.ui.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.capstone.attirely.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AddSection(navController: NavController) {
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val outfitWidgets = remember { mutableStateListOf<Pair<Uri?, String>>() }

    if (outfitWidgets.isEmpty()) {
        repeat(3) { outfitWidgets.add(Pair(null, "")) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(shape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
            .background(color = colorResource(id = R.color.primary))
            .padding(36.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(36.dp)
        ) {
            items(outfitWidgets.size) { index ->
                AddOutfitWidget(
                    selectedImageUri = outfitWidgets[index].first,
                    text = outfitWidgets[index].second,
                    onImageSelected = { uri -> outfitWidgets[index] = outfitWidgets[index].copy(first = uri) },
                    onTextChanged = { text -> outfitWidgets[index] = outfitWidgets[index].copy(second = text) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            AnimatedVisibility(
                visible = showError,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LaunchedEffect(Unit) {
                    delay(2000)
                    showError = false
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .background(Color.White, shape = RoundedCornerShape(10.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
            val validOutfitCount = outfitWidgets.count { it.first != null && it.second.isNotBlank() }
            FloatingActionButton(
                onClick = {
                    showError = false
                    errorMessage = ""
                    val allWidgetsFilled = outfitWidgets.any { it.first != null && it.second.isNotBlank() }
                    if (!allWidgetsFilled) {
                        errorMessage = "Please fill at least one image and text."
                        showError = true
                    } else {
                        navController.navigate("loading_screen")
                    }
                },
                containerColor = colorResource(id = R.color.white),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(66.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Analyze $validOutfitCount outfit(s)",
                        color = colorResource(id = R.color.primary),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    IconButton(
                        modifier = Modifier
                            .height(60.dp)
                            .padding(2.dp)
                            .width(60.dp),
                        onClick = { navController.navigate("loading_screen") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_right_down),
                            contentDescription = "Analyze",
                            tint = colorResource(id = R.color.primary),
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun AddOutfitWidget(
    selectedImageUri: Uri?,
    text: String,
    onImageSelected: (Uri) -> Unit,
    onTextChanged: (String) -> Unit
) {
    val stroke = Stroke(
        width = 7f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
    )

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { onImageSelected(it) }
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 36.dp)
    ) {
        if (selectedImageUri == null) {
            Box(
                modifier = Modifier
                    .height(170.dp)
                    .width(170.dp)
                    .drawBehind {
                        drawRoundRect(
                            color = Color.White,
                            style = stroke,
                            cornerRadius = CornerRadius(60f, 60f)
                        )
                    }
                    .clickable { galleryLauncher.launch("image/*") }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Add Image",
                    tint = colorResource(id = R.color.primary),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(Color.White, CircleShape)
                        .size(50.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .height(170.dp)
                    .width(170.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.White)
                    .clickable { galleryLauncher.launch("image/*") }
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = selectedImageUri),
                    contentDescription = "Selected Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(30.dp))
                )
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
        TextField(
            value = text,
            onValueChange = { onTextChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .align(Alignment.CenterVertically),
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 16.sp
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = Color.White,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,
            ),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.outfit_name),
                    color = colorResource(id = R.color.lightGray),
                    fontSize = 14.sp,
                )
            }
        )
    }
}