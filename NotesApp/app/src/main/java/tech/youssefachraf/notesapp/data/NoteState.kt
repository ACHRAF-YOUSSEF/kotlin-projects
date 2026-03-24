package tech.youssefachraf.notesapp.data

data class NoteState(
    val notes: List<Note> = emptyList(),
    val selectedIds: Set<Int> = emptySet(),
    val title: String = "",
    val content: String = "",
    val noteId: Int? = null
)