package com.capstone.attirely.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capstone.attirely.R
import com.capstone.attirely.ui.home.polyFontFamily

@Composable
fun AddScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 26.dp, horizontal = 26.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.background(
                    color = colorResource(id = R.color.primary),
                    shape = CircleShape
                ),
                onClick = { /*TODO*/ }) {
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
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 30.dp, end = 60.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
                .background(color = colorResource(id = R.color.primary))
                .padding(36.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(36.dp)
            ) {
                items(3) {
                    AddOutfitWidget()
                }
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
            FloatingActionButton(
                onClick = { /*TODO*/ },
                containerColor = colorResource(id = R.color.white),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(66.dp)
                    .align(Alignment.BottomEnd),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.analyze),
                        color = colorResource(id = R.color.primary),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    IconButton(
                        modifier = Modifier
                            .height(60.dp)
                            .padding(2.dp)
                            .width(60.dp),
                        onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_right_down),
                            contentDescription = "Analyze",
                            tint = colorResource(id = R.color.primary),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddOutfitWidget() {
    val stroke = Stroke(
        width = 7f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 36.dp)
    ) {
        Box(
            modifier = Modifier
                .height(170.dp)
                .width(170.dp)
                .drawBehind {
                    drawRoundRect(
                        color = Color.White,
                        style = stroke,
                        cornerRadius = CornerRadius(60f, 60f)
                    )
                }
        ) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(color = Color.White, shape = CircleShape),
                onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_add),
                    contentDescription = "Camera",
                    tint = colorResource(id = R.color.primary)
                )
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
        TextField(
            value = "",
            onValueChange = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .align(Alignment.CenterVertically),
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 16.sp
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = colorResource(id = R.color.primary),
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,
            ),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.outfit_name),
                    color = colorResource(id = R.color.lightGray),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        )
    }
}

@Preview
@Composable
fun AddScreenPreview() {
    AddScreen()
}