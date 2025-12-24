import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.name.petmemo.ui.viewmodel.PetViewModel
import com.name.petmemo.di.UserChecklist
import com.name.petmemo.ui.ThemeViewModel
import com.name.petmemo.ui.ThemeViewModelFactory
import com.name.petmemo.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditChecklistScreen(
    checklistId: Int?,
    viewModel: PetViewModel,
    onBack: () -> Unit
) {
    val application = LocalContext.current.applicationContext as Application
    val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(application))
    val currentAppTheme by themeViewModel.theme.collectAsState()
    val myChecklists by viewModel.userChecklists.collectAsState(initial = emptyList())
    val originalChecklist = myChecklists.find { it.checklist.id == checklistId }
    val context = LocalContext.current
    var checklistName by remember { mutableStateOf("") }
    val checklistItems = remember { mutableStateListOf<String>() }

    LaunchedEffect(originalChecklist) {
        if (originalChecklist != null) {
            checklistName = originalChecklist.checklist.name
            checklistItems.clear()
            checklistItems.addAll(originalChecklist.items.map { it.text })
        } else if (checklistId == null) {
            checklistName = context.getString(R.string.checklist_new_title_default)
            if (checklistItems.isEmpty()) checklistItems.add("")
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    val checklistFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        cursorColor = MaterialTheme.colorScheme.secondary,
        focusedBorderColor = MaterialTheme.colorScheme.secondary,
        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        focusedLabelColor = MaterialTheme.colorScheme.secondary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    AppBackground(appTheme = currentAppTheme) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(if (checklistId == null) R.string.checklist_new_title_default else R.string.checklist_edit_title)) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад") } },
                    actions = {
                        if (checklistId != null && originalChecklist != null) {
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(Icons.Default.Delete, stringResource(R.string.checklist_delete_icon_desc))
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (checklistName.isBlank()) {
                            Toast.makeText(context, context.getString(R.string.toast_checklist_name_empty), Toast.LENGTH_SHORT).show()
                            return@FloatingActionButton
                        }
                        if (checklistId == null) {
                            viewModel.addChecklist(UserChecklist(name = checklistName), checklistItems.filter { it.isNotBlank() })
                        } else {
                            viewModel.updateChecklistWithItems(checklistId, checklistName, checklistItems.filter { it.isNotBlank() })
                        }
                        onBack()
                    },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(Icons.Default.Check, stringResource(R.string.checklist_save_icon_desc), tint = MaterialTheme.colorScheme.onSecondary)
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = checklistName,
                        onValueChange = { checklistName = it },
                        label = { Text(stringResource(R.string.checklist_label_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = checklistFieldColors
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                itemsIndexed(checklistItems) { index, itemText ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = itemText,
                            onValueChange = { checklistItems[index] = it },
                            label = { Text(stringResource(R.string.checklist_label_item_prefix, index + 1)) },
                            modifier = Modifier.weight(1f),
                            colors = checklistFieldColors
                        )
                        IconButton(onClick = { checklistItems.removeAt(index) }) {
                            Icon(Icons.Default.Close, stringResource(R.string.checklist_item_delete_desc), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                item {
                    Button(
                        onClick = { checklistItems.add("") },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    ) { Text(stringResource(R.string.checklist_button_add_item))
                    }
                }
            }
        }

    if (showDeleteDialog && originalChecklist != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            textContentColor = MaterialTheme.colorScheme.onBackground,
            title = { Text(stringResource(R.string.dialog_checklist_delete_title)) },
            text = { Text(stringResource(R.string.dialog_checklist_delete_text, originalChecklist.checklist.name)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteChecklist(originalChecklist.checklist)
                        showDeleteDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.dialog_checklist_delete_button))
                }
            },
            dismissButton = { Button(onClick = { showDeleteDialog = false }) {
                Text(stringResource(R.string.cancel)) } }
        )
    }
}
}