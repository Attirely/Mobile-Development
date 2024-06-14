import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.capstone.attirely.R
import com.capstone.attirely.data.OutfitData

@Composable
fun AddResult(outfitData: List<OutfitData>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(shape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
            .background(color = colorResource(id = R.color.primary))
            .padding(36.dp)
    ) {
        LazyColumn {
            itemsIndexed(outfitData) { index, data ->
                AddResultComponent(
                    outfitData = data,
                    isLastItem = index == outfitData.size - 1
                )
            }
        }
        FloatingActionButton(
            onClick = {

            },
            containerColor = colorResource(id = R.color.white),
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxWidth()
                .height(66.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add ${outfitData.size} Outfit",
                    color = colorResource(id = R.color.primary),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                IconButton(
                    modifier = Modifier
                        .height(60.dp)
                        .padding(2.dp)
                        .width(60.dp),
                    onClick = { }) {
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

@Composable
fun AddResultComponent(outfitData: OutfitData, isLastItem: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLastItem) 102.dp else 32.dp, top = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .height(300.dp)
                .background(color = colorResource(id = R.color.white))
        ) {
            if (outfitData.imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = outfitData.imageUri),
                    contentDescription = "Selected Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(top = 30.dp, start = 8.dp, end = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                CustomBasicTextField(
                    text = outfitData.text,
                    placeholder = stringResource(id = R.string.outfit_name)
                )
                Text(
                    text = stringResource(id = R.string.outfit_name2),
                    fontSize = 12.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
            Column {
                CustomBasicTextField(
                    text = "",
                    placeholder = stringResource(id = R.string.category)
                )
                Text(
                    text = stringResource(id = R.string.category),
                    fontSize = 12.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}

@Composable
fun CustomBasicTextField(
    text: String,
    placeholder: String
) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .height(70.dp)
            .background(Color.Transparent)
            .padding(horizontal = 0.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        BasicTextField(
            value = text,
            onValueChange = { },
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 16.sp
            ),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.padding(start = 0.dp)) {
                    if (text.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = colorResource(id = R.color.lightGray),
                            fontSize = 14.sp
                        )
                    }
                    innerTextField()
                }
            }
        )
        Divider(color = Color.White, thickness = 1.dp)
    }
}