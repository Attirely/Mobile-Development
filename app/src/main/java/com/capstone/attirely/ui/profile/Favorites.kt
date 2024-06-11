package com.capstone.attirely.ui.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.capstone.attirely.R
import com.capstone.attirely.viewmodel.ProfileViewModel

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Favorites(viewModel: ProfileViewModel = viewModel()) {
    val favoritesList by viewModel.favoritesList.observeAsState(emptyList())
    val isLoadingFavorites by viewModel.isLoadingFavorites.observeAsState(false)
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.fetchFavorites()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoadingFavorites) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colorResource(id = R.color.primary)
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                items(favoritesList.chunked(2)) { rowContent ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        rowContent.forEach { (contentId, content) ->
                            val isFavorite by remember { mutableStateOf(true) }
                            Card(
                                modifier = Modifier
                                    .width(0.dp)
                                    .weight(1f)
                                    .padding(8.dp)
                                    .height(250.dp),
                                shape = RoundedCornerShape(20.dp),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 16.dp,
                                )
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(model = content.imageUrl),
                                        contentDescription = content.title,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(20.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        Color.Black.copy(alpha = 0.7f)
                                                    ),
                                                    startY = 300f
                                                )
                                            )
                                            .clip(RoundedCornerShape(20.dp))
                                    )
                                    Box(
                                        modifier = Modifier
                                            .height(120.dp)
                                            .fillMaxWidth()
                                            .align(Alignment.BottomStart)
                                            .padding(16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.BottomStart),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = content.title,
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 2,
                                                lineHeight = 16.sp,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(end = 8.dp)
                                            )

                                            IconButton(
                                                onClick = {
                                                    viewModel.toggleFavorite(
                                                        contentId,
                                                        content
                                                    )
                                                },
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .background(
                                                        Color.White.copy(alpha = 0.4f),
                                                        shape = RoundedCornerShape(50.dp)
                                                    )
                                                    .padding(4.dp)
                                            ) {
                                                Icon(
                                                    modifier = Modifier.size(18.dp),
                                                    painter = painterResource(
                                                        id = if (isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart
                                                    ),
                                                    contentDescription = null,
                                                    tint = if (isFavorite) colorResource(id = R.color.primary) else Color.White
                                                )
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
    }
}