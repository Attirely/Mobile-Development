package com.capstone.attirely.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.capstone.attirely.R
import com.capstone.attirely.ui.home.polyFontFamily
import com.capstone.attirely.viewmodel.SearchViewModel

@Composable
fun SearchScreen(viewModel: SearchViewModel = viewModel()) {
    var showGenderOptions by remember { mutableStateOf(false) }
    var selectedGenderIcon by remember { mutableStateOf(R.drawable.ic_gender) }
    val searchQuery by viewModel.searchQuery.collectAsState()
    val outfits by viewModel.filteredOutfits.collectAsState()
    val imageUrls by viewModel.imageUrls.collectAsState()
    val categories by viewModel.categories.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.updateSearchQuery(viewModel.searchQuery.value)
    }

    LaunchedEffect(categories) {
        viewModel.updateSelectedCategories(categories)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (imageUrls.isNotEmpty() && categories.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(shape = RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                        .background(colorResource(id = R.color.primary))
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxSize()
                            .padding(top = 30.dp, bottom = 10.dp),
                    ) {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            color = Color.White,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            fontSize = 30.sp,
                            fontFamily = polyFontFamily
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            items(imageUrls.zip(categories)) { pair ->
                                val (imageUrl, category) = pair
                                Column(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(model = imageUrl),
                                        contentDescription = category,
                                        modifier = Modifier
                                            .width(120.dp)
                                            .height(120.dp)
                                            .clip(RoundedCornerShape(20.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    IconButton(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                color = colorResource(id = R.color.primary),
                                                shape = CircleShape
                                            ),
                                        onClick = {
                                            val updatedImageUrls = imageUrls.toMutableList().apply { remove(imageUrl) }
                                            val updatedCategories = categories.toMutableList().apply { remove(category) }

                                            if (updatedImageUrls.isEmpty() && updatedCategories.isEmpty()) {
                                                viewModel.clearData()
                                            } else {
                                                viewModel.saveImageUrls(updatedImageUrls)
                                                viewModel.saveCategories(updatedCategories)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_close),
                                            contentDescription = "Delete from datastore",
                                            modifier = Modifier.size(30.dp),
                                            tint = colorResource(id = R.color.white)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(shape = RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                        .background(colorResource(id = R.color.primary))
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxSize()
                            .padding(top = 30.dp, bottom = 10.dp)
                    ) {
                        Text(
                            text = "Search",
                            color = Color.White,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            fontSize = 30.sp,
                            fontFamily = polyFontFamily
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(0.80f)
                                    .height(55.dp)
                                    .padding(start = 22.dp)
                                    .border(
                                        width = 2.dp,
                                        color = colorResource(id = R.color.white),
                                        shape = RoundedCornerShape(50.dp)
                                    )
                                    .clip(RoundedCornerShape(50.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(45.dp)
                                        .fillMaxHeight()
                                        .background(color = Color.Transparent)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_search),
                                        contentDescription = stringResource(id = R.string.search),
                                        tint = colorResource(id = R.color.white),
                                        modifier = Modifier
                                            .padding(start = 16.dp)
                                            .size(30.dp)
                                            .align(Alignment.CenterStart)
                                    )
                                }
                                TextField(
                                    value = searchQuery,
                                    onValueChange = { viewModel.updateSearchQuery(it) },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(start = 16.dp),
                                    textStyle = TextStyle(
                                        color = Color.Black,
                                        fontSize = 16.sp
                                    ),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        cursorColor = colorResource(id = R.color.primary),
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                    ),
                                    placeholder = {
                                        Text(
                                            text = stringResource(id = R.string.search),
                                            color = colorResource(id = R.color.gray),
                                            fontSize = 16.sp
                                        )
                                    }
                                )
                            }
                            IconButton(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(color = Color.White, shape = CircleShape),
                                onClick = { showGenderOptions = !showGenderOptions }
                            ) {
                                Image(
                                    painter = painterResource(id = selectedGenderIcon),
                                    contentDescription = "Choose Gender",
                                    modifier = Modifier.size(40.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            SearchContent(outfits = outfits)
        }
        AnimatedVisibility(
            visible = showGenderOptions,
            enter = androidx.compose.animation.fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = androidx.compose.animation.fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 165.dp, end = 18.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    IconButton(
                        modifier = Modifier
                            .size(50.dp)
                            .background(color = Color.White, shape = CircleShape),
                        onClick = {
                            selectedGenderIcon = R.drawable.ic_male
                            showGenderOptions = false
                            viewModel.updateGenderFilter("male")
                        }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_male),
                            contentDescription = "Male",
                            modifier = Modifier.size(40.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    IconButton(
                        modifier = Modifier
                            .size(50.dp)
                            .background(color = Color.White, shape = CircleShape),
                        onClick = {
                            selectedGenderIcon = R.drawable.ic_female
                            showGenderOptions = false
                            viewModel.updateGenderFilter("female")
                        }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_female),
                            contentDescription = "Female",
                            modifier = Modifier.size(40.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
}