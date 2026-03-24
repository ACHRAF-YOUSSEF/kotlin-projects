package tech.youssefachraf.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import tech.youssefachraf.notesapp.data.NoteState
import tech.youssefachraf.notesapp.screens.NotesListScreen
import tech.youssefachraf.notesapp.ui.theme.NotesAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesAppTheme {
                NotesListScreen(
                    state = NoteState(),
                    onEvent = {}
                )
            }
        }
    }
}