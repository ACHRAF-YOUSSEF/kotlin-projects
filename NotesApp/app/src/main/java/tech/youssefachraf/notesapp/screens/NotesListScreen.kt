package tech.youssefachraf.notesapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.youssefachraf.notesapp.data.Note
import tech.youssefachraf.notesapp.data.NoteState
import tech.youssefachraf.notesapp.ui.theme.NeutralColor
import tech.youssefachraf.notesapp.ui.theme.PrimaryColor
import tech.youssefachraf.notesapp.ui.theme.Surface
import tech.youssefachraf.notesapp.ui.theme.TextPrimary
import tech.youssefachraf.notesapp.ui.theme.TextSecondary

@Composable
fun NotesListScreen(
    state: NoteState,
    onAddNoteClick: () -> Unit = {},
    onToggleAllClick: () -> Unit = {},
    onDeleteSelectedClick: () -> Unit = {},
    onLongPress: (Note) -> Unit = {},
    onNoteClick: (Note) -> Unit = {},
) {
    val selectedIds = state.selectedIds
    val notes = state.notes
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAddNoteClick()
                },
                containerColor = PrimaryColor,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Add new note")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NeutralColor)
                .padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                HeaderSection(
                    noteSize = notes.size,
                    isSelectionMode = state.isSelectionMode,
                    selectedCount = state.selectedCount,
                    allSelected = state.allSelected,
                    onToggleAllClick = onToggleAllClick,
                    onDeleteSelectedClick = {
                        showDeleteConfirmation = true
                    },
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(notes) { note ->
                        NoteItem(
                            note = note,
                            isSelectionMode = state.isSelectionMode,
                            isSelected = selectedIds.contains(note.id),
                            onLongPress = {
                                onLongPress(note)
                            },
                            onClick = {
                                onNoteClick(note)
                            }
                        )
                    }
                }
            }

            if (showDeleteConfirmation) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirmation = false },
                    title = { Text(text = "Delete selected notes?") },
                    text = { Text(text = "This action cannot be undone.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onDeleteSelectedClick()
                                showDeleteConfirmation = false
                            }
                        ) {
                            Text(text = "Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirmation = false }) {
                            Text(text = "Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun HeaderSection(
    noteSize: Int,
    isSelectionMode: Boolean,
    selectedCount: Int,
    allSelected: Boolean,
    onToggleAllClick: () -> Unit,
    onDeleteSelectedClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "All Notes",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = if (isSelectionMode) "$selectedCount selected" else "$noteSize notes",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }

        if (isSelectionMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    IconButton(onClick = onToggleAllClick) {
                        Icon(
                            imageVector = if (allSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Toggle all",
                            tint = if (allSelected) PrimaryColor else TextSecondary,
                        )
                    }

                    Text(
                        text = "All",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier
                            .padding(start = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = onDeleteSelectedClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete selected",
                        tint = TextSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun NoteItem(
    note: Note,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onLongPress: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongPress,
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    Text(
                        text = note.content,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            if (isSelectionMode) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) PrimaryColor else Color.White)
                        .border(1.dp, if (isSelected) PrimaryColor else TextSecondary, CircleShape)
                        .align(Alignment.TopStart),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        Text(
            text = note.title,
            fontSize = 16.sp,
            color = TextPrimary,
            modifier = Modifier
                .padding(start = 16.dp, top = 4.dp)
                .fillMaxWidth(),
            maxLines = 1
        )
    }
}