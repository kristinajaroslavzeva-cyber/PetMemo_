package com.name.petmemo.ui.screens

import AppBackground
import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.name.petmemo.data.model.MedicalRecord
import com.name.petmemo.data.model.RecordNote
import java.text.SimpleDateFormat
import java.util.*
import androidx.navigation.NavController
import com.name.petmemo.ui.viewmodel.PetViewModel
import com.name.petmemo.R
import com.name.petmemo.ui.ThemeViewModel
import com.name.petmemo.ui.ThemeViewModelFactory

@Composable
fun MedicalRecordInfoCard(record: MedicalRecord) {
    val dateFormatter = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(all = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = context.getString(R.string.record_detail_symptoms, record.symptoms), style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
            Text(text = context.getString(R.string.record_detail_date, dateFormatter.format(record.date)), color = Color.White.copy(alpha = 0.8f))

            if (!record.clinicName.isNullOrBlank()) {
                Text(text = context.getString(R.string.record_detail_place, record.clinicName), color = Color.White.copy(alpha = 0.8f))
            }
            if (!record.vetName.isNullOrBlank()) {
                Text(text = context.getString(R.string.record_detail_specialist, record.vetName), color = Color.White.copy(alpha = 0.8f))
            }
            if (!record.diagnosis.isNullOrBlank()) {
                Text(text = context.getString(R.string.record_detail_diagnosis, record.diagnosis), color = Color.White.copy(alpha = 0.8f))
            }
            if (!record.treatment.isNullOrBlank()) {
                Text(text = context.getString(R.string.record_detail_treatment, record.treatment), color = Color.White.copy(alpha = 0.8f))
            }
            if (!record.notes.isNullOrBlank()) {
                Text(text = context.getString(R.string.record_detail_note, record.notes), color = Color.White.copy(alpha = 0.8f))
            }
        }
    }
}
@Composable
fun NoteCard(
    note: RecordNote,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(dateFormatter.format(note.date), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                Text(note.note, style = MaterialTheme.typography.bodyLarge, color = Color.White)
            }
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, stringResource(R.string.note_edit_desc), tint = Color.White.copy(alpha = 0.7f))
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, stringResource(R.string.note_delete_desc), tint = Color.White.copy(alpha = 0.7f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteDialog(
    note: RecordNote,
    onDismiss: () -> Unit,
    onSave: (RecordNote) -> Unit
) {
    var editedText by remember { mutableStateOf(note.note) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = Color.Black,
        textContentColor = Color.Black,
        text = {
            TextField(
                value = editedText,
                onValueChange = { editedText = it },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedTextColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        confirmButton = {
            Button(onClick = { onSave(note.copy(note = editedText)) }) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalRecordDetailScreen(
    recordId: Int,
    navController: NavController,
    onBack: () -> Unit,
    onNavigateToEdit: (petId: Int, recordId: Int) -> Unit,
    viewModel: PetViewModel
) {
    val application = LocalContext.current.applicationContext as Application
    val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(application))
    val currentAppTheme by themeViewModel.theme.collectAsState()

    val record by viewModel.getMedicalRecordById(recordId).collectAsState(initial = null)
    val notes by viewModel.getNotesForRecord(recordId).collectAsState(emptyList())

    var newNoteText by remember { mutableStateOf("") }
    var noteToEdit by remember { mutableStateOf<RecordNote?>(null) }
    var noteToDelete by remember { mutableStateOf<RecordNote?>(null) }
    var showNoteDeleteDialog by remember { mutableStateOf(false) }

    AppBackground(appTheme = currentAppTheme) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(record?.symptoms ?: stringResource(R.string.record_detail_loading), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                        }
                    },
                    actions = {
                    IconButton(onClick = { record?.let { onNavigateToEdit(it.petId, it.id) } }) { Icon(Icons.Default.Edit, "Редактировать") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
            bottomBar = {
                Surface(color = Color.Transparent, modifier = Modifier.imePadding()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = newNoteText,
                            onValueChange = { newNoteText = it },
                            label = { Text(stringResource(R.string.record_detail_add_note_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors( // Цвета из темы
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (newNoteText.isNotBlank()) {
                                    viewModel.addRecordNote(RecordNote(recordId = recordId, date = Date(), note = newNoteText))
                                    newNoteText = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = newNoteText.isNotBlank()
                        ) { Text(stringResource(R.string.record_detail_add_note_button))
                        }
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { record?.let { MedicalRecordInfoCard(record = it) } }
                item {
                    Text(
                        stringResource(R.string.record_detail_section_notes),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White, // <-- Изменить здесь на Color.White
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                    )
                }
                items(notes) { note ->
                    NoteCard(
                        note = note,
                        onEditClick = { noteToEdit = note },
                        onDeleteClick = { noteToDelete = note; showNoteDeleteDialog = true }
                    )
                }
            }
        }

    if (noteToEdit != null) {
        EditNoteDialog(
            note = noteToEdit!!,
            onDismiss = { noteToEdit = null },
            onSave = { updatedNote ->
                viewModel.updateNote(updatedNote)
                noteToEdit = null
            }
        )
    }

    if (showNoteDeleteDialog && noteToDelete != null) {
        AlertDialog(
            onDismissRequest = { showNoteDeleteDialog = false; noteToDelete = null },
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = Color.Black,
            textContentColor = Color.Black,
            title = { Text(stringResource(R.string.dialog_note_delete_title)) },
            text = { Text(stringResource(R.string.dialog_note_delete_text)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteNote(noteToDelete!!)
                        showNoteDeleteDialog = false
                        noteToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.note_delete_desc))
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoteDeleteDialog = false; noteToDelete = null }) { Text(stringResource(
                    R.string.cancel)) }
            }
        )
    }
}
}


