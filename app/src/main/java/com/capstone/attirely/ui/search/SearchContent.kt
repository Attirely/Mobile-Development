package com.capstone.attirely.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.capstone.attirely.data.Outfit
import com.capstone.attirely.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

@Composable
fun SearchContent(outfits: List<Outfit>) {
    val viewModel: SearchViewModel = viewModel()
    val favorites by viewModel.favorites.collectAsState(initial = emptySet())
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.fetchFavorites()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp)
    ) {
        items(outfits.chunked(2)) { rowOutfits ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowOutfits.forEach { outfit ->
                    var isFavorite by remember { mutableStateOf(favorites.contains(outfit.imageurl)) }

                    LaunchedEffect(favorites) {
                        isFavorite = favorites.contains(outfit.imageurl)
                    }

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
                                painter = rememberAsyncImagePainter(model = outfit.imageurl),
                                contentDescription = outfit.filename,
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
                                        text = outfit.classes.joinToString(", "),
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
                                            isFavorite = !isFavorite
                                            coroutineScope.launch {
                                                viewModel.toggleFavorite(outfit)
                                            }
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