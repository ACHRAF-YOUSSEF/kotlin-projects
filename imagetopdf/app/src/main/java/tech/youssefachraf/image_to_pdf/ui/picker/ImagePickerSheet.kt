package tech.youssefachraf.image_to_pdf.ui.picker

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import tech.youssefachraf.image_to_pdf.ui.theme.AccentBlue
import tech.youssefachraf.image_to_pdf.ui.theme.DarkBackground
import tech.youssefachraf.image_to_pdf.ui.theme.DarkCard
import tech.youssefachraf.image_to_pdf.ui.theme.PrimaryText
import tech.youssefachraf.image_to_pdf.ui.theme.SecondaryText
import tech.youssefachraf.image_to_pdf.viewmodel.PickerState
import tech.youssefachraf.image_to_pdf.viewmodel.PickerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePickerSheet(
    pickerViewModel: PickerViewModel,
    onDismiss: () -> Unit,
    onPdfCreated: () -> Unit,
) {
    val uiState by pickerViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val mediaPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) pickerViewModel.addImages(uris)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        }
    }

    fun launchPicker() {
        val granted = ContextCompat.checkSelfPermission(
            context, mediaPermission
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (granted) {
            imagePickerLauncher.launch("image/*")
        } else {
            permissionLauncher.launch(mediaPermission)
        }
    }

    if (uiState.selectedImages.isEmpty() && uiState.pickerState is PickerState.Idle && !uiState.showPreview) {
        LaunchedEffect(Unit) {
            val granted = ContextCompat.checkSelfPermission(
                context, mediaPermission
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (granted) {
                imagePickerLauncher.launch("image/*")
            } else {
                permissionLauncher.launch(mediaPermission)
            }
        }
    }

    if (uiState.showNameDialog) {
        PdfNameDialog(
            name = uiState.pdfName,
            onNameChange = pickerViewModel::setPdfName,
            onGenerate = { pickerViewModel.generatePdf() },
            onDismiss = { pickerViewModel.dismissNameDialog() },
        )
    }

    val pickerState = uiState.pickerState
    if (pickerState is PickerState.FileConflict) {
        FileConflictDialog(
            originalName = pickerState.originalName,
            suggestedName = pickerState.suggestedName,
            onOverwrite = { pickerViewModel.generatePdf(pickerState.originalName) },
            onSaveAs = { pickerViewModel.generatePdf(pickerState.suggestedName) },
            onCancel = { pickerViewModel.dismissConflict() },
        )
    }

    if (pickerState is PickerState.Success) {
        LaunchedEffect(pickerState) {
            onPdfCreated()
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            pickerViewModel.reset()
            onDismiss()
        },
        sheetState = sheetState,
        containerColor = DarkCard,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        if (uiState.showPreview) {
            PreviewReorderContent(
                images = uiState.selectedImages,
                pickerState = pickerState,
                onMove = pickerViewModel::moveImage,
                onRemove = pickerViewModel::removeImage,
                onAddMore = ::launchPicker,
                onBack = pickerViewModel::backToSelection,
                onContinue = pickerViewModel::confirmPreview,
            )
        } else {
            SelectionContent(
                images = uiState.selectedImages,
                pickerState = pickerState,
                onLaunchPicker = ::launchPicker,
                onRemove = pickerViewModel::removeImage,
                onConfirm = pickerViewModel::confirmSelection,
            )
        }
    }
}

@Composable
private fun SelectionContent(
    images: List<Uri>,
    pickerState: PickerState,
    onLaunchPicker: () -> Unit,
    onRemove: (Uri) -> Unit,
    onConfirm: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "Select Images",
            style = MaterialTheme.typography.titleLarge,
            color = PrimaryText,
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (pickerState is PickerState.Converting) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = AccentBlue)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Generating PDF…", color = SecondaryText, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (pickerState is PickerState.Error) {
            Text("Error: ${pickerState.message}", color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (images.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                items(images, key = { it.toString() }) { uri ->
                    SmallImageThumbnail(uri = uri, onRemove = { onRemove(uri) })
                }
                item {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF21262D))
                            .clickable(onClick = onLaunchPicker),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.Add, "Add more", tint = SecondaryText, modifier = Modifier.size(28.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "${images.size} image${if (images.size != 1) "s" else ""} selected",
                color = SecondaryText,
                fontSize = 13.sp,
            )
        } else {
            Box(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No images selected", color = SecondaryText, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onLaunchPicker) {
                        Text("Pick Images", color = AccentBlue)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth(),
            enabled = images.isNotEmpty() && pickerState !is PickerState.Converting,
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("Confirm Selection", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
private fun PreviewReorderContent(
    images: List<Uri>,
    pickerState: PickerState,
    onMove: (Int, Int) -> Unit,
    onRemove: (Uri) -> Unit,
    onAddMore: () -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(
        lazyListState = lazyListState,
        onMove = { from, to -> onMove(from.index, to.index) }
    )
    var previewUri by remember { mutableStateOf<Uri?>(null) }

    previewUri?.let { uri ->
        FullscreenImagePreview(
            uri = uri,
            pageLabel = "Page ${images.indexOf(uri) + 1}",
            onDismiss = { previewUri = null },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = PrimaryText)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Preview & Reorder",
                    style = MaterialTheme.typography.titleLarge,
                    color = PrimaryText,
                )
                Text(
                    "${images.size} image${if (images.size != 1) "s" else ""} · Drag to reorder",
                    color = SecondaryText,
                    fontSize = 13.sp,
                )
            }
            IconButton(onClick = onAddMore) {
                Icon(Icons.Default.Add, "Add more images", tint = AccentBlue)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (pickerState is PickerState.Converting) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), color = AccentBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Generating PDF…", color = SecondaryText, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .height(360.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(images.size, key = { images[it].toString() }) { index ->
                val uri = images[index]
                ReorderableItem(reorderableState, key = uri.toString()) { isDragging ->
                    val elevation by animateDpAsState(
                        targetValue = if (isDragging) 8.dp else 1.dp,
                        label = "drag_elevation"
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkBackground),
                        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            IconButton(
                                modifier = Modifier.draggableHandle(),
                                onClick = {},
                            ) {
                                Icon(
                                    Icons.Default.DragHandle,
                                    contentDescription = "Drag to reorder",
                                    tint = SecondaryText,
                                )
                            }

                            AsyncImage(
                                model = uri,
                                contentDescription = "Page ${index + 1}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { previewUri = uri },
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { previewUri = uri }
                            ) {
                                Text(
                                    "Page ${index + 1}",
                                    color = PrimaryText,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                )
                                Text(
                                    "Tap to preview",
                                    color = AccentBlue,
                                    fontSize = 12.sp,
                                )
                            }

                            IconButton(onClick = { onRemove(uri) }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color(0xFFE74C3C),
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            enabled = images.isNotEmpty() && pickerState !is PickerState.Converting,
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("Continue", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
private fun SmallImageThumbnail(uri: Uri, onRemove: () -> Unit) {
    Box(modifier = Modifier.size(80.dp)) {
        AsyncImage(
            model = uri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(2.dp)
                .size(22.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Close, "Remove", tint = Color.White, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
private fun PdfNameDialog(
    name: String,
    onNameChange: (String) -> Unit,
    onGenerate: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = { Text("Name your PDF", color = PrimaryText) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                singleLine = true,
                label = { Text("File name") },
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            Button(
                onClick = onGenerate,
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
            ) { Text("Generate") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = SecondaryText) }
        }
    )
}

@Composable
private fun FileConflictDialog(
    originalName: String,
    suggestedName: String,
    onOverwrite: () -> Unit,
    onSaveAs: () -> Unit,
    onCancel: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        containerColor = DarkCard,
        title = { Text("File Already Exists", color = PrimaryText) },
        text = {
            Text(
                "A file named \"$originalName.pdf\" already exists. Overwrite or save as \"$suggestedName.pdf\"?",
                color = SecondaryText,
            )
        },
        confirmButton = {
            Button(
                onClick = onOverwrite,
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
            ) { Text("Overwrite") }
        },
        dismissButton = {
            Column {
                TextButton(onClick = onSaveAs) { Text("Save as $suggestedName", color = AccentBlue) }
                TextButton(onClick = onCancel) { Text("Cancel", color = SecondaryText) }
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
//  Fullscreen image preview (pinch-to-zoom, tap anywhere to dismiss)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FullscreenImagePreview(
    uri: Uri,
    pageLabel: String,
    onDismiss: () -> Unit,
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f))
                .pointerInput(Unit) {
                    detectTapGestures { onDismiss() }
                },
            contentAlignment = Alignment.Center,
        ) {
            // Close button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.15f), CircleShape),
            ) {
                Icon(Icons.Default.Close, "Close", tint = Color.White)
            }

            Text(
                text = pageLabel,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(20.dp),
            )

            // Zoomable image
            AsyncImage(
                model = uri,
                contentDescription = "Full preview",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y,
                    )
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                            if (scale > 1f) {
                                offset = Offset(offset.x + pan.x, offset.y + pan.y)
                            } else {
                                offset = Offset.Zero
                            }
                        }
                    },
            )

            // Hint at bottom
            Text(
                text = "Pinch to zoom · Tap to close",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
            )
        }
    }
}


