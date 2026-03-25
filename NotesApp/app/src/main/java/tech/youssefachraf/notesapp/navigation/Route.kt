package tech.youssefachraf.notesapp.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import tech.youssefachraf.notesapp.data.Note

@Serializable
sealed interface Route : NavKey {
    @Serializable
    data object NotesListScreen : Route, NavKey

    @Serializable
    data class EditNoteScreen(val note: Note) : Route, NavKey
}