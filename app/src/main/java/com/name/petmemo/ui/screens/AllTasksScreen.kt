package com.name.petmemo.ui.screens

import AppBackground
import android.app.Application
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.name.petmemo.data.model.PetTask
import com.name.petmemo.ui.ThemeViewModel
import com.name.petmemo.ui.ThemeViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import androidx.compose.ui.res.stringResource
import com.name.petmemo.ui.viewmodel.PetViewModel
import com.name.petmemo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTasksScreen(
    onNavigateToEditTask: (Int, Int) -> Unit,
    viewModel: PetViewModel
) {
    val application = LocalContext.current.applicationContext as Application
    val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(application))
    val currentAppTheme by themeViewModel.theme.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState(initial = emptyList())
    val allPets by viewModel.allPets.collectAsState(initial = emptyList())
    var showDeleteDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<PetTask?>(null) }
    AppBackground(appTheme = currentAppTheme) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.tasks_all_title)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->
            if (allTasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.tasks_empty_list),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allTasks) { task ->
                        val pet = allPets.find { it.id == task.petId }
                        TaskCard(
                            task = task,
                            petName = pet?.name ?: stringResource(R.string.tasks_general_task),
                            onStatusChange = { viewModel.updateTask(it) },
                            onEditClick = { onNavigateToEditTask(task.petId, task.id) },
                            onDeleteClick = {
                                taskToDelete = task
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog && taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = Color.Black, // Цвет заголовка -> черный
            textContentColor = Color.Black,  // Цвет основного текста -> черный
            title = { Text(stringResource(R.string.tasks_dialog_confirmation)) },
            text = {
                Text(stringResource(R.string.tasks_dialog_delete_text, taskToDelete!!.title ?: ""))
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTask(taskToDelete!!)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text(stringResource(R.string.tasks_dialog_delete)) }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel)) }
            }
        )
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskCard(
    task: PetTask,
    petName: String,
    onStatusChange: (PetTask) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }
    val textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null

    val cardColor = when {
        task.isCompleted -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        task.date.before(Date()) -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        TimeUnit.MILLISECONDS.toDays(task.date.time - Date().time) <= 3 -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    }

    Card(
        modifier = Modifier.fillMaxWidth().combinedClickable(onClick = onEditClick, onLongClick = onDeleteClick),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_checklist),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title ?: "".ifBlank { stringResource(R.string.tasks_no_description) },
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = textDecoration
                )
                Text(
                    text = stringResource(R.string.tasks_pet_label, petName),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    textDecoration = textDecoration
                )
                Text(
                    text = dateFormatter.format(task.date),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    textDecoration = textDecoration
                )
            }
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { isChecked -> onStatusChange(task.copy(isCompleted = isChecked)) }
            )
        }
    }
}