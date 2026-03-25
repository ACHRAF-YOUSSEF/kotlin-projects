package tech.youssefachraf.notesapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tech.youssefachraf.notesapp.data.NoteEvent
import tech.youssefachraf.notesapp.data.NoteState

@Composable
fun EditNoteScreen(
    state: NoteState,
    onEvent: (NoteEvent) -> Unit
) {
    Column {
        TextField(
            value = state.title,
            onValueChange = {
                onEvent(NoteEvent.SetTitle(it))
            },
            placeholder = { Text("Title") }
        )

        TextField(
            value = state.content,
            onValueChange = {
                onEvent(NoteEvent.SetContent(it))
            },
            modifier = Modifier.fillMaxSize(),
            placeholder = { Text("Start typing...") }
        )
    }
}