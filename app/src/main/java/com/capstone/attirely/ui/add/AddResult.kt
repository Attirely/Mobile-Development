package com.capstone.attirely.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capstone.attirely.R

@Composable
fun AddResult() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(shape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
            .background(color = colorResource(id = R.color.primary))
            .padding(36.dp)
    ) {
        LazyColumn {
            item {
                AddResultComponen()
            }

        }
    }
}

@Composable
fun AddResultComponen() {
    Column(modifier = Modifier.fillMaxWidth()) {
        //image palcement
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .height(300.dp)
                .background(color = colorResource(id = R.color.white))
        ) {

        }
        Row(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = "",
                onValueChange = { },
                modifier = Modifier
                    .width(120.dp)
                    .height(70.dp)
                    .align(Alignment.CenterVertically),
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                ),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.outfit_name),
                        color = colorResource(id = R.color.lightGray),
                        fontSize = 14.sp,
                    )
                }
            )
            TextField(
                value = "",
                onValueChange = { },
                modifier = Modifier
                    .width(120.dp)
                    .height(70.dp)
                    .align(Alignment.CenterVertically),
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                ),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.category),
                        color = colorResource(id = R.color.lightGray),
                        fontSize = 14.sp,
                    )
                }
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = stringResource(id = R.string.outfit_name2),
                color = Color.White,
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = stringResource(id = R.string.category),
                color = Color.White,
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 8.dp, end = 80.dp)
            )
        }
    }
}

@Preview
@Composable
fun AddSectionPreview() {
    AddResult()
}