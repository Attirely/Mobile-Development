import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.capstone.attirely.R
import com.capstone.attirely.data.ClosetItem

@Composable
fun Closet(viewModel: ProfileViewModel = viewModel()) {
    val closetItems by viewModel.filteredClosetItems.collectAsState()
    val selectedItems by viewModel.selectedClosetItems.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()

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
}

@Composable
fun ClosetItemCard(
    item: ClosetItem,
    isSelected: Boolean,
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
                                Color.Transparent,
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
                    color = Color.White,
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
                        .background(colorResource(id = R.color.secondary).copy(alpha = 0.3f))
                        .clip(RoundedCornerShape(20.dp))
                )
            }
        }
    }
}