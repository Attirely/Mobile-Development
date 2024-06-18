import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.capstone.attirely.R
import com.capstone.attirely.data.ClosetItem
import kotlinx.coroutines.launch

@Composable
fun Closet(viewModel: ProfileViewModel = viewModel(), navController: NavHostController) {
    LaunchedEffect(Unit) {
        viewModel.fetchCloset()
    }

    val closetItems by viewModel.filteredClosetItems.collectAsState()
    val selectedItems by viewModel.selectedClosetItems.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
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
                            onDelete = {
                                coroutineScope.launch {
                                    viewModel.deleteClosetItem(item)
                                    viewModel.fetchCloset()
                                }
                            },
                            onEdit = { newText ->
                                coroutineScope.launch {
                                    viewModel.updateClosetItemText(item.id, newText)
                                    viewModel.fetchCloset()
                                }
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
        if (selectedItems.isNotEmpty()) {
            FloatingActionButton(
                onClick = {
                    val selectedItem = selectedItems.first()
                    coroutineScope.launch {
                        viewModel.saveImageUrls(
                            selectedItems.map { it.imageUrl }
                        )
                        viewModel.saveCategories(
                            selectedItems.map { it.category }
                        )
                        viewModel.navigateToSearch(navController)
                    }
                },
                containerColor = colorResource(id = R.color.primary),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .padding(bottom = 122.dp)
                    .align(Alignment.BottomCenter)
                    .width(320.dp)
                    .height(66.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "See recommendation from ${selectedItems.size} item(s)",
                        color = colorResource(id = R.color.white),
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .width(220.dp)
                            .padding(start = 40.dp)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_right_down_alternate),
                        contentDescription = "Analyze",
                        tint = colorResource(id = R.color.white),
                        modifier = Modifier
                            .height(60.dp)
                            .padding(2.dp)
                            .width(60.dp),
                    )
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
    onDelete: () -> Unit,
    onEdit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableStateOf(0f) }
    val offsetAnimatable = remember { Animatable(0f) }
    val dragThreshold = 30f // Reduced threshold for showing delete/edit indicator
    val deleteThreshold = 180f // Threshold for deletion
    val editThreshold = 180f // Threshold for showing edit indicator
    val trashIndicatorShown = offsetX < -dragThreshold
    val editIndicatorShown = offsetX > dragThreshold
    val coroutineScope = rememberCoroutineScope()

    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(offsetX) {
        if (offsetX == 0f) {
            offsetAnimatable.snapTo(0f)
        } else {
            offsetAnimatable.animateTo(
                targetValue = offsetX,
                animationSpec = tween(durationMillis = 300)
            )
        }
    }

    if (showEditDialog) {
        EditTextDialog(
            initialText = item.text,
            onDismiss = { showEditDialog = false },
            onConfirm = { newText ->
                onEdit(newText)
                showEditDialog = false
            }
        )
    }

    Card(
        modifier = modifier
            .padding(8.dp)
            .height(200.dp)
            .draggable(
                state = rememberDraggableState { delta ->
                    offsetX += delta
                    when {
                        offsetX < -deleteThreshold -> {
                            onDelete()
                            offsetX = 0f
                        }
                        offsetX > editThreshold -> {
                            showEditDialog = true
                            offsetX = 0f
                        }
                    }
                },
                orientation = Orientation.Horizontal,
                onDragStarted = { offsetX = 0f },
                onDragStopped = {
                    coroutineScope.launch {
                        offsetAnimatable.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(durationMillis = 300)
                        )
                        offsetX = 0f
                    }
                }
            )
            .offset(x = offsetAnimatable.value.dp)
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
            if (trashIndicatorShown) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Red.copy(alpha = 0.3f))
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Trash",
                        tint = White,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                            .padding(bottom = 8.dp)
                    )
                }
            }
            if (editIndicatorShown) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Green.copy(alpha = 0.3f))
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "Edit",
                        tint = White,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                            .padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EditTextDialog(
    initialText: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Text") },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(text = "Text") }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(text) }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}