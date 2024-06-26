package com.capstone.attirely.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.capstone.attirely.R
import com.capstone.attirely.viewmodel.ContentViewModel

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AllContent(viewModel: ContentViewModel = viewModel()) {
    val contentList by viewModel.contentList.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val favorites by viewModel.favorites.observeAsState(emptySet())
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.fetchContent()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            items(contentList.chunked(2)) { rowContent ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowContent.forEach { (contentId, content) ->
                        val isFavorite = favorites.contains(contentId)
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
                                            onClick = { viewModel.toggleFavorite(contentId, content) },
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

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colorResource(id = R.color.primary)
            )
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                if (visibleItems.isNotEmpty() &&
                    visibleItems.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1
                ) {
                    viewModel.fetchContent()
                }
            }
    }
}

@Preview
@Composable
fun AllContentPreview() {
    AllContent(viewModel = ContentViewModel())
}
