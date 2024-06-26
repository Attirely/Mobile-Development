package com.capstone.attirely.ui.add

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.capstone.attirely.R
import com.capstone.attirely.data.OutfitData
import com.capstone.attirely.helper.ImageClassifierHelper
import com.capstone.attirely.ui.home.polyFontFamily
import com.capstone.attirely.ui.laoding.LoadingScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddScreen(navController: NavController) {
    var showAddResult by remember { mutableStateOf(false) }
    var showLoading by remember { mutableStateOf(false) }
    val outfitWidgets = remember { mutableStateListOf<Pair<Uri?, String>>() }
    var results by remember { mutableStateOf<List<OutfitData>?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val imageClassifierHelper = remember {
        ImageClassifierHelper(
            context = context,
            listener = object : ImageClassifierHelper.ClassifierListener {
                override fun onFailure(error: String) {
                    showLoading = false
                    showAddResult = false
                    Log.e("ImageClassifier", "Classification failed: $error")
                }

                override fun onSuccess(resultList: List<ImageClassifierHelper.ClassificationResult>?) {
                    showLoading = false
                    showAddResult = true
                    resultList?.let {
                        Log.d("ImageClassifier", "Classification successful: $resultList")
                    }
                }

                override fun onModelReady() {
                    Log.d("ImageClassifier", "Model is ready")
                }
            },
            colorModelFileName = "color.tflite",
            typeModelFileName = "type.tflite"
        )
    }

    LaunchedEffect(showLoading) {
        if (showLoading) {
            delay(1200)
            showLoading = false
            showAddResult = true
        }
    }

    if (showLoading) {
        LoadingScreen()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp, horizontal = 26.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier.background(
                        color = colorResource(id = R.color.primary),
                        shape = CircleShape
                    ),
                    onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = stringResource(id = R.string.add),
                    color = colorResource(id = R.color.primary),
                    textAlign = TextAlign.Center,
                    fontFamily = polyFontFamily,
                    lineHeight = 35.sp,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 30.dp, end = 60.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))
            }

            when {
                showAddResult -> {
                    AddResult(
                        outfitData = results.orEmpty(),
                        navController = navController
                    )
                }
                else -> {
                    AddSection(navController = navController, outfitWidgets = outfitWidgets) { validOutfits ->
                        showLoading = true
                        coroutineScope.launch {
                            delay(1500)
                            val resultsList = mutableListOf<OutfitData>()
                            var processedOutfits = 0
                            validOutfits.forEach { outfit ->
                                imageClassifierHelper.classifyImage(outfit.first) { colorResult, typeResult ->
                                    val outfitData = OutfitData(
                                        category = "${colorResult?.label} ${typeResult?.label}",
                                        imageUri = outfit.first.toString(),
                                        text = outfit.second
                                    )
                                    resultsList.add(outfitData)
                                    processedOutfits++
                                    if (processedOutfits == validOutfits.size) {
                                        results = resultsList
                                        showLoading = false
                                        showAddResult = true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AddScreenPreview() {
    AddScreen(rememberNavController())
}