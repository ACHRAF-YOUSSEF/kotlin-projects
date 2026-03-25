package tech.youssefachraf.notesapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteViewModel(
    private val dao: NoteDao
) : ViewModel() {
    private val _selectedIds = MutableStateFlow<Set<Int>>(emptySet())
    private val _notes = dao.getNotes().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )
    private val _state = MutableStateFlow(NoteState())

    val state = combine(_state, _notes, _selectedIds) { state, notes, selectedIds ->
        state.copy(
            notes = notes,
            selectedIds = selectedIds
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        NoteState()
    )

    fun onEvent(event: NoteEvent) {
        when (event) {
            is NoteEvent.DeleteNote -> {
                viewModelScope.launch {
                    dao.delete(event.note)
                }
            }

            is NoteEvent.SetTitle -> {
                _state.update {
                    it.copy(title = event.title)
                }
                persistNote()
            }

            is NoteEvent.SetContent -> {
                _state.update {
                    it.copy(content = event.content)
                }
                persistNote()
            }

            is NoteEvent.AddNewEmptyNote -> {
                _state.update {
                    it.copy(
                        title = "test",
                        content = "test",
                        noteId = null
                    )
                }

                onEvent(NoteEvent.SaveNote)
            }

            is NoteEvent.StartEditingNote -> {
                _state.update {
                    it.copy(
                        title = event.note.title,
                        content = event.note.content,
                        noteId = event.note.id
                    )
                }
            }

            NoteEvent.SaveNote -> {
                persistNote()
            }
        }
    }

    private fun persistNote() {
        val currentState = _state.value
        val title = currentState.title.trim()
        val content = currentState.content.trim()

        if (title.isEmpty() && content.isEmpty()) {
            return
        }

        viewModelScope.launch {
            val rowId = dao.upsert(
                Note(
                    id = currentState.noteId ?: 0,
                    title = currentState.title,
                    content = currentState.content,
                )
            )

            if (currentState.noteId == null && rowId > 0) {
                _state.update {
                    it.copy(noteId = rowId.toInt())
                }
            }
        }
    }
}