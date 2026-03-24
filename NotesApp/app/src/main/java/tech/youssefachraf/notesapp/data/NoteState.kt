package tech.youssefachraf.notesapp.data

data class NoteState(
    val notes: List<Note> = emptyList(),
    val title: String = "",
    val content: String = "",
    val noteId: Int? = null
)