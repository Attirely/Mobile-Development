package com.capstone.attirely.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capstone.attirely.R
import com.capstone.attirely.ui.home.polyFontFamily
import com.capstone.attirely.viewmodel.SearchViewModel

@Composable
fun SearchScreen(viewModel: SearchViewModel = viewModel()) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val outfits by viewModel.filteredOutfits.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .padding(horizontal = 22.dp)
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
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        SearchContent(outfits = outfits)
    }
}
