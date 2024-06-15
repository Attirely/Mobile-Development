import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.capstone.attirely.R
import com.capstone.attirely.data.ClosetItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun Closet(viewModel: ProfileViewModel = viewModel()) {
    val closetItems by viewModel.filteredClosetItems.collectAsState()
    val selectedItems by viewModel.selectedClosetItems.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()

    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.80f)){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 4.dp, end = 4.dp, top = 16.dp)
        ) {
            items(closetItems.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowItems.forEach { item ->
                        ClosetItemCard(
                            item = item,
                            isSelected = selectedItems.contains(item),
                            selectionNumber = selectedItems.indexOf(item) + 1,
                            isSelectionMode = isSelectionMode,
                            onSelect = {
                                if (!isSelectionMode) {
                                    viewModel.activateSelectionMode()
                                }
                                viewModel.toggleSelectClosetItem(item)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        if(selectedItems.isNotEmpty()){
            FloatingActionButton(
                onClick = {

                },
                containerColor = colorResource(id = R.color.primary),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .width(320.dp)
                    .height(66.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "See recommendation from ${
                            selectedItems.size
                        } item(s)",
                        color = colorResource(id = R.color.white),
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.width(220.dp).padding(start = 40.dp)
                    )
                    IconButton(
                        modifier = Modifier
                            .height(60.dp)
                            .padding(2.dp)
                            .width(60.dp),
                        onClick = { }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_right_down_alternate),
                            contentDescription = "Analyze",
                            tint = colorResource(id = R.color.white),
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun ClosetItemCard(
    item: ClosetItem,
    isSelected: Boolean,
    selectionNumber: Int,
    isSelectionMode: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .height(200.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        if (!isSelectionMode) {
                            onSelect()
                        }
                    },
                    onTap = {
                        if (isSelectionMode) {
                            onSelect()
                        }
                    }
                )
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 16.dp,
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberAsyncImagePainter(model = item.imageUrl),
                contentDescription = item.text,
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
                                Transparent,
                                Color.Black.copy(alpha = 0.3f)
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
                Text(
                    text = item.text,
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    lineHeight = 16.sp,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .align(Alignment.BottomStart)
                )
            }
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorResource(id = R.color.secondary).copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .padding(8.dp)
                            .background(colorResource(id = R.color.primary), CircleShape)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = selectionNumber.toString(),
                            color = White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

            }
        }
    }
}