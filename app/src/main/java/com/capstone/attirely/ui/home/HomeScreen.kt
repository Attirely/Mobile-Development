package com.capstone.attirely.ui.home

import AllContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capstone.attirely.R
import com.capstone.attirely.viewmodel.ContentViewModel

val polyFontFamily = FontFamily(
    Font(R.font.poly_regular, FontWeight.Normal)
)

@Composable
fun HomeScreen() {
    val selectedTab = remember { mutableStateOf("all") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                .background(color = colorResource(id = R.color.primary))
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
                    .padding(top = 26.dp, bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontFamily = polyFontFamily
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .clickable { selectedTab.value = "all" },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.all),
                            color = Color.White,
                            fontSize = 20.sp
                        )
                        Divider(
                            color = (if (selectedTab.value == "all") Color.White else Color.Transparent),
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .width(50.dp)
                                .height(2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(80.dp))
                    Column(
                        modifier = Modifier
                            .clickable { selectedTab.value = "newest" },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.newest),
                            color = Color.White,
                            fontSize = 20.sp
                        )
                        Divider(
                            color = (if (selectedTab.value == "newest") Color.White else Color.Transparent),
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .width(50.dp)
                                .height(2.dp)
                        )
                    }
                }

            }
        }

        if (selectedTab.value == "all") {
            AllContent(
                viewModel = ContentViewModel()
            )
        } else {
            NewestContent()
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
