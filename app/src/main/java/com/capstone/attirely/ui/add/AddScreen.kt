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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.capstone.attirely.R
import com.capstone.attirely.ui.home.polyFontFamily

@Composable
fun AddScreen(navController: NavController) {
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
                onClick = { navController.navigateUp() }) {
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
                lineHeight = 35.sp,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 30.dp, end = 60.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        AddSection()
    }
}

@Preview
@Composable
fun AddScreenPreview() {
    AddScreen(rememberNavController())
}