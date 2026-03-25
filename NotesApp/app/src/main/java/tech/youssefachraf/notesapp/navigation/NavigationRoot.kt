package tech.youssefachraf.notesapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import tech.youssefachraf.notesapp.data.Note
import tech.youssefachraf.notesapp.data.NoteEvent
import tech.youssefachraf.notesapp.data.NoteState
import tech.youssefachraf.notesapp.screens.EditNoteScreen
import tech.youssefachraf.notesapp.screens.NotesListScreen

@Composable
fun NavigationRoot(
    noteState: State<NoteState>,
    onEvent: (NoteEvent) -> Unit
) {
    val backStack = rememberNavBackStack(Route.NotesListScreen)

    NavDisplay(
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
        ),
        backStack = backStack,
        entryProvider = { key ->
            when (key) {
                is Route.NotesListScreen -> {
                    NavEntry(key) {
                        NotesListScreen(
                            state = noteState.value,
                            onAddNoteClick = {
                                onEvent(NoteEvent.AddNewEmptyNote)
                                backStack.add(Route.EditNoteScreen(Note()))
                            },
                            onLongPress = {

                            },
                            onNoteClick = {
                                onEvent(NoteEvent.StartEditingNote(it))
                                backStack.add(Route.EditNoteScreen(it))
                            }
                        )
                    }
                }

                is Route.EditNoteScreen -> {
                    NavEntry(key) {
                        EditNoteScreen(
                            state = noteState.value,
                            onBack = {
                                backStack.removeLastOrNull()
                            },
                            onEvent = onEvent,
                        )
                    }
                }

                else -> error("Unknown route: $key")
            }
        }
    )
}