package com.capstone.attirely.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.capstone.attirely.R
import com.capstone.attirely.viewmodel.ContentViewModel
import kotlin.math.absoluteValue

@Composable
fun NewestContent(viewModel: ContentViewModel = viewModel()) {
    val newestContentList by viewModel.newestContentList.observeAsState(emptyList())
    val isLoadingNewest by viewModel.isLoadingNewest.observeAsState(false)
    val listState = rememberLazyListState()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    LaunchedEffect(Unit) {
        viewModel.fetchNewestContent()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoadingNewest) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colorResource(id = R.color.primary)
            )
        } else {
            LazyRow(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(newestContentList) { index, content ->
                    var cardOffset by remember { mutableStateOf(0f) }
                    var isFavorite by remember { mutableStateOf(false) }

                    val cardModifier = Modifier
                        .width(350.dp)
                        .fillMaxHeight(0.85f)
                        .onGloballyPositioned { coordinates: LayoutCoordinates ->
                            val position = coordinates.boundsInParent()
                            cardOffset = position.left + position.width / 2
                        }
                        .graphicsLayer {
                            val center = screenWidth.toPx() / 2
                            val scale =
                                1f - ((center - cardOffset).absoluteValue / center).coerceIn(
                                    0f,
                                    0.5f
                                )
                            scaleX = 0.8f + 0.2f * scale
                            scaleY = 0.8f + 0.2f * scale
                        }

                    Card(
                        modifier = cardModifier,
                        shape = RoundedCornerShape(30.dp),
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
                                    .height(160.dp)
                                    .fillMaxWidth()
                                    .align(Alignment.BottomStart)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomStart)
                                        .padding(bottom = 26.dp, start = 26.dp, end = 26.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = content.title,
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 20.dp),
                                    )

                                    IconButton(
                                        onClick = { isFavorite = !isFavorite },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                Color.White.copy(alpha = 0.4f),
                                                shape = RoundedCornerShape(50.dp)
                                            )
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(26.dp),
                                            painter = painterResource(
                                                id = if (isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart
                                            ),
                                            contentDescription = null,
                                            tint = if (isFavorite) colorResource(id = R.color.secondary) else Color.White
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
