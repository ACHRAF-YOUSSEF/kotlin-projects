package tech.youssefachraf.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import tech.youssefachraf.notesapp.data.NoteDatabase
import tech.youssefachraf.notesapp.data.NoteViewModel
import tech.youssefachraf.notesapp.navigation.NavigationRoot
import tech.youssefachraf.notesapp.ui.theme.NotesAppTheme

class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            NoteDatabase::class.java,
            "notes.db"
        ).build()
    }

    private val noteViewModel by viewModels<NoteViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NoteViewModel(dao = db.dao()) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesAppTheme {
                val noteState = noteViewModel.state.collectAsState()

                NavigationRoot(
                    noteState = noteState,
                    onEvent = noteViewModel::onEvent
                )
            }
        }
    }
}