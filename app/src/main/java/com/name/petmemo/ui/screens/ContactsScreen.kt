package com.name.petmemo.ui.screens

import AppBackground
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import android.Manifest
import android.app.Application
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import com.name.petmemo.ui.viewmodel.PetViewModel
import com.name.petmemo.R
import com.name.petmemo.data.model.Contact
import com.name.petmemo.ui.ThemeViewModel
import com.name.petmemo.ui.ThemeViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ContactCard(contact: Contact, onDeleteClick: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(contact.name, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(contact.phone, color = Color.White)
                }
                Row {
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}"))
                        context.startActivity(intent)
                    }) { Icon(Icons.Default.Phone, stringResource(R.string.contacts_call), tint = MaterialTheme.colorScheme.secondary)
                    }
                    IconButton(onClick = {
                        try {
                            val uri = Uri.parse("whatsapp://send?phone=${contact.phone.replace("+", "").replace(" ", "")}") // WhatsApp требует номер без +, пробелов
                            val whatsappIntent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(whatsappIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, context.getString(R.string.toast_whatsapp_not_installed), Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Message, stringResource(R.string.contacts_whatsapp), tint = MaterialTheme.colorScheme.secondary)
                    }

                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, stringResource(R.string.contacts_delete), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            if (!contact.notes.isNullOrBlank()) {
                Text(contact.notes, modifier = Modifier.padding(top = 8.dp), style = MaterialTheme.typography.bodyMedium, color = Color.Black)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactDialog(
    name: String, onNameChange: (String) -> Unit,
    phone: String, onPhoneChange: (String) -> Unit,
    type: String, onTypeChange: (String) -> Unit,
    notes: String, onNotesChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onImport: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        titleContentColor = Color.Black,
        textContentColor = Color.Black,
        title = { Text(stringResource(R.string.dialog_contact_new_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onImport, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.dialog_contact_import_button))
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                TextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text(stringResource(R.string.dialog_contact_label_name_required)) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color.DarkGray,
                        unfocusedLabelColor = Color.DarkGray,
                        cursorColor = Color.Black

                    )
                )
                TextField(
                    value = phone,
                    onValueChange = onPhoneChange,
                    label = { Text(stringResource(R.string.dialog_contact_label_phone_required)) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color.DarkGray, unfocusedLabelColor = Color.DarkGray,
                        cursorColor = Color.Black
                    )
                )
                TextField(
                    value = type,
                    onValueChange = onTypeChange,
                    label = { Text(stringResource(R.string.dialog_contact_label_type_hint)) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color.DarkGray, unfocusedLabelColor = Color.DarkGray,
                        cursorColor = Color.Black
                    )
                )
                TextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    label = { Text(stringResource(R.string.dialog_contact_label_notes_hint)) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color.DarkGray, unfocusedLabelColor = Color.DarkGray,
                        cursorColor = Color.Black
                    )
                )
            }
        },
        confirmButton = { Button(onClick = onSave) {
            Text(stringResource(R.string.dialog_contact_save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) {
            Text(stringResource(R.string.cancel)) } }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ContactsScreen(
    onBack: () -> Unit,
    viewModel: PetViewModel
) {
    val application = LocalContext.current.applicationContext as Application
    val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(application))
    val currentAppTheme by themeViewModel.theme.collectAsState()
    val context = LocalContext.current
    val contacts by viewModel.allContacts.collectAsState(initial = emptyList())

    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var contactToDelete by remember { mutableStateOf<Contact?>(null) }


    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { contactUri: Uri? ->
        contactUri?.let { uri ->
            scope.launch(Dispatchers.IO) {
                var contactName: String = context.getString(R.string.toast_contact_name_default)
                var contactPhone: String = ""

                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                        contactName = it.getString(nameIndex)

                        val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
                        val contactId = it.getString(idIndex)

                        val phoneCursor = context.contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(contactId),
                            null
                        )
                        phoneCursor?.use { pc ->
                            if (pc.moveToFirst()) {
                                val phoneIndex = pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                contactPhone = pc.getString(phoneIndex)
                            }
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    name = contactName
                    phone = contactPhone
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            contactPickerLauncher.launch()
        } else {
            Toast.makeText(context, context.getString(R.string.toast_permission_contacts_denied), Toast.LENGTH_SHORT).show()
        }
    }

    fun clearDialogState() {
        name = ""; phone = ""; type = ""; notes = ""
    }


    AppBackground(appTheme = currentAppTheme) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.contacts_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) { Icon(Icons.Default.Add, stringResource(R.string.contacts_add), tint = MaterialTheme.colorScheme.onSecondary)
                }
            }
        ) { paddingValues ->
            val groupedContacts = contacts.groupBy { it.type }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp)
            ) {
                if (contacts.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.contacts_empty_list), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f), textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    groupedContacts.forEach { (type, contactsInType) ->
                        stickyHeader {
                            Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)) {
                                Text(
                                    text = type,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                        items(contactsInType) { contact ->
                            ContactCard(
                                contact = contact,
                                onDeleteClick = {
                                    contactToDelete = contact
                                    showDeleteDialog = true
                            }
                        )
                    }
                }
            }


        }
    }

        if (showAddDialog) {
            AddContactDialog(
                name = name, onNameChange = { name = it },
                phone = phone, onPhoneChange = { phone = it },
                type = type, onTypeChange = { type = it },
                notes = notes, onNotesChange = { notes = it },
                onDismiss = { showAddDialog = false; clearDialogState() },
                onSave = {
                    if (name.isNotBlank() && phone.isNotBlank() && type.isNotBlank()) {
                        viewModel.addContact(Contact(name = name, phone = phone, type = type, notes = notes.ifBlank { null }))
                        showAddDialog = false
                        clearDialogState()
                    } else {
                        Toast.makeText(context, context.getString(R.string.toast_contact_required_fields), Toast.LENGTH_SHORT).show()
                    }
                },
                onImport = {
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) -> {
                            contactPickerLauncher.launch()
                        }
                        else -> {
                            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                        }
                    }
                }
            )
        }

        if (showDeleteDialog && contactToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    contactToDelete = null
                },
                containerColor = Color.White,
                titleContentColor = Color.Black,
                textContentColor = Color.Black,
                title = { Text(stringResource(R.string.dialog_delete_contact_title)) },
                text = {
                    // ЗАМЕНА 21: "Вы уверены, что хотите удалить контакт..."
                    Text(stringResource(R.string.dialog_delete_contact_text, contactToDelete!!.name))
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteContact(contactToDelete!!)
                            showDeleteDialog = false
                            contactToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(stringResource(R.string.contacts_delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        contactToDelete = null
                    }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
}
}