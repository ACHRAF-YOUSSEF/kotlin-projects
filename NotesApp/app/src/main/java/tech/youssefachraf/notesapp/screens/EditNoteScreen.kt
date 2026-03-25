package tech.youssefachraf.notesapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.youssefachraf.notesapp.data.NoteEvent
import tech.youssefachraf.notesapp.data.NoteState
import tech.youssefachraf.notesapp.ui.theme.NeutralColor
import tech.youssefachraf.notesapp.ui.theme.PrimaryColor
import tech.youssefachraf.notesapp.ui.theme.Surface
import tech.youssefachraf.notesapp.ui.theme.TextSecondary

@Composable
fun EditNoteScreen(
    state: NoteState,
    onBack: () -> Unit,
    onDeleteClick: () -> Unit,
    onEvent: (NoteEvent) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = NeutralColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NeutralColor)
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                TextField(
                    value = state.title,
                    onValueChange = { onEvent(NoteEvent.SetTitle(it)) },
                    modifier = Modifier
                        .weight(1f),
                    placeholder = { Text("Title") },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = PrimaryColor
                    )
                )

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete selected",
                        tint = TextSecondary,
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .background(Surface)
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            TextField(
                value = state.content,
                onValueChange = {
                    onEvent(NoteEvent.SetContent(it))
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text(
                        "Start typing...",
                        color = Color.Gray
                    )
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color.White,
                    lineHeight = 22.sp
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = PrimaryColor
                )
            )
        }
    }
}