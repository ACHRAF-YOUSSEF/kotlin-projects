package tech.youssefachraf.notesapp.data

sealed interface NoteEvent {
    object SaveNote : NoteEvent

    data class SetTitle(val title: String) : NoteEvent
    data class SetContent(val content: String) : NoteEvent

    data class DeleteNote(val note: Note) : NoteEvent

    data class StartEditingNote(val note: Note) : NoteEvent

    object AddNewEmptyNote : NoteEvent
}