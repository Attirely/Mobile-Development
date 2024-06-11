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
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.capstone.attirely.R
import com.capstone.attirely.ui.home.polyFontFamily
import com.capstone.attirely.viewmodel.ProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel()) {
    val selectedTab = remember { mutableStateOf("favorites") }
    var boxHeight by remember { mutableStateOf(140.dp) }
    val animatedBoxHeight by animateDpAsState(
        targetValue = boxHeight,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = ""
    )

    val minHeight = 140.dp
    val maxHeight = 300.dp
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
                    androidx.compose.material3.Text(
                        text = stringResource(id = R.string.profile),
                        color = Color.White,
                        fontSize = 32.sp,
                        fontFamily = polyFontFamily
                    )
                } else {
                    user?.let { userInfo ->
                        Image(
                            painter = rememberAsyncImagePainter(model = userInfo.avatarUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(2.dp, colorResource(id = R.color.secondary), CircleShape)
                                .background(Color.White),
                            contentScale = ContentScale.Crop
                        )
                        androidx.compose.material3.Text(
                            text = userInfo.username,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        androidx.compose.material3.Text(
                            text = userInfo.email,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .clickable { selectedTab.value = "favorites" },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        androidx.compose.material3.Text(
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
                            .clickable { selectedTab.value = "closet" },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        androidx.compose.material3.Text(
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