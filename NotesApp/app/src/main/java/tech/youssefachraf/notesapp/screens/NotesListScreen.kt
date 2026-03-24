package tech.youssefachraf.notesapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.youssefachraf.notesapp.data.Note
import tech.youssefachraf.notesapp.data.NoteEvent
import tech.youssefachraf.notesapp.data.NoteState
import tech.youssefachraf.notesapp.ui.theme.NeutralColor
import tech.youssefachraf.notesapp.ui.theme.PrimaryColor
import tech.youssefachraf.notesapp.ui.theme.Surface
import tech.youssefachraf.notesapp.ui.theme.TextPrimary
import tech.youssefachraf.notesapp.ui.theme.TextSecondary

@Composable
fun NotesListScreen(
    state: NoteState,
    onEvent: (NoteEvent) -> Unit,
    onLongPress: (Note) -> Unit = {},
    onNoteClick: (Note) -> Unit = {},
) {
    val selectedIds = state.selectedIds
    val notes = state.notes

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(NoteEvent.AddNewEmptyNote)
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
                HeaderSection(notesSize = notes.size)

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
                            isSelected = selectedIds.contains(note.id),
                            onLongPress = {
                                onLongPress(note)
                            },
                            onClick = { onNoteClick(note) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(
    notesSize: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "All Notes",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Text(
            text = "$notesSize notes",
            fontSize = 14.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun NoteItem(
    note: Note,
    isSelected: Boolean,
    onLongPress: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clickable(onClick = onClick),
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

        Text(
            text = note.title,
            fontSize = 16.sp,
            color = TextPrimary,
            modifier = Modifier
                .padding(start = 4.dp, top = 4.dp)
                .fillMaxWidth(),
            maxLines = 1
        )
    }
}