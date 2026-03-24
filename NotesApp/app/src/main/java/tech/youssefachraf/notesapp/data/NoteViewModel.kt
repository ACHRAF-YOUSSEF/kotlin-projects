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
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    private val _state = MutableStateFlow(NoteState())

    val state = combine(_state, _notes) { state, notes ->
        state.copy(
            notes = notes
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
            }

            is NoteEvent.SetContent -> {
                _state.update {
                    it.copy(content = event.content)
                }
            }

            NoteEvent.SaveNote -> {
                val title = state.value.title
                val content = state.value.content

                if (title.isBlank() || content.isBlank()) {
                    return
                }

                viewModelScope.launch {
                    dao.upsert(
                        Note(
                            title = title,
                            content = content
                        )
                    )
                }

                _state.update {
                    it.copy(
                        title = "",
                        content = "",
                        noteId = null
                    )
                }
            }
        }
    }
}