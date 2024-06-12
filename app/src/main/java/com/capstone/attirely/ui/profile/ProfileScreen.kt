package com.capstone.attirely.ui.profile

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.capstone.attirely.R
import com.capstone.attirely.ui.home.polyFontFamily
import com.capstone.attirely.viewmodel.ProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel()) {
    val selectedTab = remember { mutableStateOf("favorites") }
    var boxHeight by remember { mutableStateOf(if (selectedTab.value == "closet") 250.dp else 140.dp) }
    val animatedBoxHeight by animateDpAsState(
        targetValue = boxHeight,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = ""
    )

    var minHeight by remember { mutableStateOf(140.dp) }
    var maxHeight by remember { mutableStateOf(300.dp) }

    LaunchedEffect(selectedTab.value) {
        if (selectedTab.value == "closet") {
            minHeight = 250.dp
            maxHeight = 380.dp
            boxHeight = 250.dp
        } else {
            minHeight = 140.dp
            maxHeight = 300.dp
            boxHeight = 140.dp
        }
    }

    val user by viewModel.user.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedBoxHeight)
                .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                .background(color = colorResource(id = R.color.primary))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            boxHeight = if (boxHeight > (minHeight + maxHeight) / 2) {
                                maxHeight
                            } else {
                                minHeight
                            }
                        }
                    ) { change, dragAmount ->
                        val (_, y) = dragAmount
                        change.consume()
                        boxHeight += y.dp
                        if (boxHeight < minHeight) boxHeight = minHeight
                        if (boxHeight > maxHeight) boxHeight = maxHeight
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
                    .padding(top = 30.dp, bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                if (boxHeight == minHeight) {
                    Text(
                        text = stringResource(id = R.string.profile),
                        color = Color.White,
                        fontSize = 32.sp,
                        fontFamily = polyFontFamily
                    )
                } else {
                    user?.let { userInfo ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = userInfo.avatarUrl),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .border(
                                        2.dp,
                                        colorResource(id = R.color.secondary),
                                        CircleShape
                                    )
                                    .background(Color.White),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = userInfo.username,
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = userInfo.email,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .clickable {
                                selectedTab.value = "favorites"
                                boxHeight = 140.dp
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.favorites),
                            color = Color.White,
                            fontSize = 20.sp
                        )
                        Divider(
                            color = (if (selectedTab.value == "favorites") Color.White else Color.Transparent),
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .width(50.dp)
                                .height(2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(80.dp))
                    Column(
                        modifier = Modifier
                            .clickable {
                                selectedTab.value = "closet"
                                boxHeight = 250.dp
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.closet),
                            color = Color.White,
                            fontSize = 20.sp
                        )
                        Divider(
                            color = (if (selectedTab.value == "closet") Color.White else Color.Transparent),
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .width(50.dp)
                                .height(2.dp)
                        )
                    }
                }
                if (selectedTab.value == "closet") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.80f)
                                .height(55.dp)
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
                                    .background(color= Color.Transparent)

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
                                value = "",
                                onValueChange = { /*TODO*/ },
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
                        Spacer(modifier = Modifier.width(16.dp))
                        IconButton(
                            onClick = { /* Handle add button click */ },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(colorResource(id = R.color.white))
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_add),
                                contentDescription = stringResource(id = R.string.add),
                                tint = colorResource(id = R.color.primary)
                            )
                        }
                    }
                }
            }
        }

        if (selectedTab.value == "favorites") {
            Favorites()
        } else {
            Closet()
        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
