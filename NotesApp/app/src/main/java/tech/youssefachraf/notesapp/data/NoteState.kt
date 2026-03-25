package tech.youssefachraf.notesapp.data

data class NoteState(
    val notes: List<Note> = emptyList(),
    val selectedIds: Set<Int> = emptySet(),
    val title: String = "",
    val content: String = "",
    val noteId: Int? = null
) {
    val noteSize: Int
        get() = notes.size

    val selectedCount: Int
        get() = selectedIds.size

    val allSelected: Boolean
        get() = notes.isNotEmpty() && selectedIds.size == notes.size

    val isSelectionMode: Boolean
        get() = selectedIds.isNotEmpty()
}
