package com.name.petmemo.ui.screens

import AppBackground
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.name.petmemo.ui.viewmodel.PetViewModel
import com.name.petmemo.R
import com.name.petmemo.data.model.Document
import com.name.petmemo.data.model.Pet
import com.name.petmemo.ui.ThemeViewModel
import com.name.petmemo.ui.ThemeViewModelFactory
import com.name.petmemo.utils.copyFileToInternalStorage
import com.name.petmemo.utils.getFileExtension
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

private fun getMimeType(filePath: String): String {
    val extension = MimeTypeMap.getFileExtensionFromUrl(filePath)
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
}

fun getFileExtension(context: Context, uri: Uri): String? {
    return context.contentResolver.getType(uri)?.let {
        MimeTypeMap.getSingleton().getExtensionFromMimeType(it)
    }
}
fun copyFileToInternalStorage(context: Context, uri: Uri, newFileName: String): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, newFileName)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDocumentDialog(
    pets: List<Pet>,
    documentName: String,
    onDocumentNameChange: (String) -> Unit,
    selectedPetId: Int?,
    onPetSelected: (Int?) -> Unit,
    selectedFileUri: Uri?,
    onFilePick: () -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var petDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        titleContentColor = Color.Black,
        textContentColor = Color.Black,
        title = { Text(stringResource(R.string.dialog_document_new_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                TextField(
                    value = documentName,
                    onValueChange = onDocumentNameChange,
                    label = { Text(stringResource(R.string.dialog_document_label_name)) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color.DarkGray, unfocusedLabelColor = Color.DarkGray,
                        cursorColor = Color.Black
                    )
                )

                ExposedDropdownMenuBox(
                    expanded = petDropdownExpanded,
                    onExpandedChange = { petDropdownExpanded = !petDropdownExpanded }
                ) {
                    val petName = pets.find { it.id == selectedPetId }?.name ?: stringResource(R.string.documents_common_name)
                    TextField(
                        value = petName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.dialog_document_label_pet_bind)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = petDropdownExpanded) },
                        modifier = Modifier.menuAnchor(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.DarkGray, unfocusedLabelColor = Color.DarkGray,
                            cursorColor = Color.Black
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = petDropdownExpanded,
                        onDismissRequest = { petDropdownExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(text = { Text(stringResource(R.string.documents_common_name), color = Color.Black) }, onClick = { onPetSelected(null); petDropdownExpanded = false })
                        pets.forEach { pet ->
                            DropdownMenuItem(text = { Text(pet.name, color = Color.Black) }, onClick = { onPetSelected(pet.id); petDropdownExpanded = false })
                        }
                    }
                }

                Button(onClick = onFilePick) {
                    val buttonText = if (selectedFileUri == null) R.string.dialog_document_button_select_file else R.string.dialog_document_button_file_selected
                    Text(stringResource(buttonText)) // Текст кнопки обычно берет цвет из Theme.onPrimary/onSecondary
                }
            }
        },
        confirmButton = { Button(onClick = onSave) {
            Text(stringResource(R.string.dialog_document_save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) {
            Text(stringResource(R.string.cancel)) }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DocumentCard(
    document: Document,
    petName: String,
    onClick: (Document) -> Unit,
    onDeleteClick: (Document) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = { onClick(document) },
                onLongClick = { onDeleteClick(document) }
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.FilePresent, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) // Или Color.DarkGray
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(document.name, color = Color.White, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Pets, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) // Или Color.DarkGray
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "$petName • ${dateFormatter.format(document.dateAdded)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }
            IconButton(onClick = { onDeleteClick(document) }) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.documents_delete_content_desc), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DocumentsScreen(
    onBack: () -> Unit,
    viewModel: PetViewModel
) {
    val application = LocalContext.current.applicationContext as Application
    val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(application))
    val currentAppTheme by themeViewModel.theme.collectAsState()

    val context = LocalContext.current
    val allPets by viewModel.allPets.collectAsState(initial = emptyList())
    val allDocuments by viewModel.allDocuments.collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var documentToDelete by remember { mutableStateOf<Document?>(null) }
    var documentName by remember { mutableStateOf("") }
    var selectedPetId by remember { mutableStateOf<Int?>(null) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedFileUri = uri
        }
    )

    if (showAddDialog) {
        AddDocumentDialog(
            pets = allPets,
            documentName = documentName,
            onDocumentNameChange = { documentName = it },
            selectedPetId = selectedPetId,
            onPetSelected = { selectedPetId = it },
            selectedFileUri = selectedFileUri,
            onFilePick = { filePickerLauncher.launch("*/*") },
            onDismiss = { showAddDialog = false },
            onSave = {
                if (documentName.isBlank() || selectedFileUri == null) {
                    // ЗАМЕНА 11: Toast-сообщение
                    Toast.makeText(context, context.getString(R.string.toast_document_required_fields), Toast.LENGTH_SHORT).show()
                } else {
                    // ИСПРАВЛЕНО: Создаем имя файла С РАСШИРЕНИЕМ
                    val extension = getFileExtension(context, selectedFileUri!!)
                    val newFileName = "doc_${System.currentTimeMillis()}" + if (extension != null) ".$extension" else ""

                    val newFilePath = copyFileToInternalStorage(context, selectedFileUri!!, newFileName)
                    if (newFilePath != null) {
                        viewModel.addDocument(Document(petId = selectedPetId ?: -1, name = documentName, filePath = newFilePath, dateAdded = Date()))
                        showAddDialog = false
                    } else {
                        Toast.makeText(context, context.getString(R.string.toast_document_save_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
    if (showDeleteDialog && documentToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color.White,
            titleContentColor = Color.Black,
            textContentColor = Color.Black,
            title = { Text(stringResource(R.string.dialog_document_confirmation)) },
            text = { Text(stringResource(R.string.dialog_document_delete_text, documentToDelete!!.name)) },
            confirmButton = {
                Button(
                    onClick = {
                        documentToDelete?.let { document ->
                            viewModel.deleteDocument(document)
                            val file = File(document.filePath)
                            if (file.exists()) {
                                if (file.delete()) {
                                    Toast.makeText(context, context.getString(R.string.toast_file_deleted), Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, context.getString(R.string.toast_file_delete_failed), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        showDeleteDialog = false
                        documentToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text(stringResource(R.string.dialog_document_delete_button))
                }
            },
            dismissButton = { Button(onClick = { showDeleteDialog = false }) {
                Text(stringResource(R.string.cancel)) } }
        )
    }

    AppBackground(appTheme = currentAppTheme) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.documents_title)) },
                    navigationIcon = { IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                    } },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    documentName = ""
                    selectedPetId = null
                    selectedFileUri = null
                    showAddDialog = true
                },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, stringResource(R.string.documents_add_fab), tint = MaterialTheme.colorScheme.onSecondary)
            }
        }
    ) { paddingValues ->
        val groupedDocuments = allDocuments.groupBy { it.petId }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            if (groupedDocuments.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.documents_empty_list), color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)
                    }
                }
            } else {
                groupedDocuments.forEach { (petId, documents) ->
                    val petName = allPets.find { it.id == petId }?.name ?: context.getString(R.string.documents_common_header)

                    stickyHeader {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = petName,
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    items(documents) { document ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            DocumentCard(
                                document = document,
                                petName = petName,
                                onClick = { clickedDocument ->
                                    val file = File(clickedDocument.filePath)

                                    if (file.exists()) {
                                        try {
                                            val fileUri: Uri = FileProvider.getUriForFile(
                                                context,
                                                "${context.packageName}.provider",
                                                file
                                            )
                                            val mimeType = getMimeType(file.absolutePath)
                                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                                setDataAndType(fileUri, mimeType)
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, context.getString(R.string.toast_file_open_failed, e.message), Toast.LENGTH_LONG).show()
                                            e.printStackTrace()
                                        }
                                    } else {
                                        Toast.makeText(context, context.getString(R.string.toast_file_not_found, clickedDocument.filePath), Toast.LENGTH_LONG).show()
                                    }
                                },
                                onDeleteClick = { clickedDocument ->
                                    documentToDelete = clickedDocument
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
}