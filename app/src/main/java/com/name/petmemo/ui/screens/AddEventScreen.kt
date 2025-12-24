package com.name.petmemo.ui.screens

import AppBackground
import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.name.petmemo.notifications.NotificationScheduler
import com.name.petmemo.ui.components.PetDetailRow
import com.name.petmemo.ui.viewmodel.PetViewModel
import com.name.petmemo.R
import com.name.petmemo.ui.components.TextFieldRow
import com.name.petmemo.data.model.PetTask
import com.name.petmemo.data.model.TaskCategory
import com.name.petmemo.ui.ThemeViewModel
import com.name.petmemo.ui.ThemeViewModelFactory
import com.name.petmemo.com.name.petmemo.ui.screens.SelectionDialog
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import com.name.petmemo.data.model.getDisplayName
// В AddEventScreen.kt, в секции import:
import java.util.*
import kotlin.collections.find
import kotlin.collections.map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    petId: Int,
    taskId: Int?,
    defaultTitle: String?,
    onBack: () -> Unit,
    viewModel: PetViewModel
) {
    val application = LocalContext.current.applicationContext as Application
    val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(application))
    val currentAppTheme by themeViewModel.theme.collectAsState()
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val allPets by viewModel.allPets.collectAsState(initial = emptyList())
    var taskCategory by remember { mutableStateOf(TaskCategory.VET_VISIT) }
    var taskTitle by remember { mutableStateOf(if (taskId == null) defaultTitle ?: "" else "") }
    var taskDate by remember { mutableStateOf<Date?>(null) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var remindMe by remember { mutableStateOf(false) }
    var reminderDateTime by remember { mutableStateOf<Calendar?>(null) }
    var selectedPetId by remember { mutableStateOf<Int?>(if (petId == -1) null else petId) }
    val calendar = Calendar.getInstance()
    var showPetDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = taskId) {
        if (taskId != null) {
            val taskToEdit = viewModel.getTaskById(taskId).firstOrNull()
            if (taskToEdit != null) {
                taskCategory = taskToEdit.category
                taskTitle = taskToEdit.title ?: ""
                taskDate = taskToEdit.date
                selectedPetId = taskToEdit.petId
                if (taskToEdit.reminderDateTime != null) {
                    remindMe = true
                    reminderDateTime = Calendar.getInstance().apply { time = taskToEdit.reminderDateTime!! }
                }
            }
        }
    }

    val taskDatePicker = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            val newDate = Calendar.getInstance().apply { set(year, month, day) }
            taskDate = newDate.time
        },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )

    val reminderDatePicker = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            val newCalendar = (reminderDateTime ?: Calendar.getInstance()).clone() as Calendar
            newCalendar.set(year, month, day)
            reminderDateTime = newCalendar
        },
        reminderDateTime?.get(Calendar.YEAR) ?: calendar.get(Calendar.YEAR),
        reminderDateTime?.get(Calendar.MONTH) ?: calendar.get(Calendar.MONTH),
        reminderDateTime?.get(Calendar.DAY_OF_MONTH) ?: calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val newCalendar = (reminderDateTime ?: Calendar.getInstance()).clone() as Calendar
            newCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            newCalendar.set(Calendar.MINUTE, minute)
            reminderDateTime = newCalendar
        },
        reminderDateTime?.get(Calendar.HOUR_OF_DAY) ?: 9,
        reminderDateTime?.get(Calendar.MINUTE) ?: 0,
        true
    )
    if (showPetDialog) {
        SelectionDialog(
            options = allPets.map { it.name },
            title = stringResource(R.string.task_pet_selection_title),
            onDismiss = { showPetDialog = false },
            onSelect = { index ->
                selectedPetId = allPets[index].id
                showPetDialog = false
            }
        )
    }
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = Color.Black, // Цвет заголовка -> черный
            textContentColor = Color.Black,  // Цвет основного текста -> черный
            title = { Text(stringResource(R.string.task_category_selection_title)) },
            text = {
                LazyColumn {
                    items(TaskCategory.values()) { category ->
                        Text(
                            text = category.getDisplayName(),
                            modifier = Modifier.fillMaxWidth().clickable {
                                taskCategory = category
                                showCategoryDialog = false
                            }.padding(vertical = 16.dp)
                        )
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showCategoryDialog = false }) { Text(stringResource(
                R.string.cancel)) } }
        )
    }
    AppBackground(appTheme = currentAppTheme) {

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(if (taskId == null) R.string.task_add_title else R.string.task_edit_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                        }
                    },
                    actions = {
                        if (taskId != null) {
                            IconButton(onClick = {
                                val petName = allPets.find { it.id == selectedPetId }?.name ?: ""
                                val shareText = buildString {
                                    appendLine(context.getString(R.string.share_task_pet, petName))
                                    appendLine(context.getString(R.string.share_task_title, taskTitle))
                                    taskDate?.let { appendLine(context.getString(R.string.share_task_date, dateFormatter.format(it))) }
                                }

                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, context.getString(
                                    R.string.share_task_chooser))
                                context.startActivity(shareIntent)
                            }) {
                                Icon(Icons.Default.Share, stringResource(R.string.task_share))
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
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            item {
                PetDetailRow(
                    icon = painterResource(id = R.drawable.ic_pet_name_24),
                    label = stringResource(R.string.task_label_pet),
                    value = allPets.find { it.id == selectedPetId }?.name
                        ?: stringResource(R.string.task_pet_select_pet),
                    onClick = { showPetDialog = true }
                )
            }
            item {
                PetDetailRow(
                    icon = getIconForTaskCategory(category = taskCategory),
                    label = stringResource(R.string.task_label_category),
                    value = taskCategory.getDisplayName(),
                    onClick = { showCategoryDialog = true },
                )
            }
            item {
                TextFieldRow(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = stringResource(R.string.task_label_description),
                    icon = painterResource(id = R.drawable.ic_edit_24),
                    modifier = Modifier.height(120.dp)
                )
            }
            item {
                PetDetailRow(
                    icon = painterResource(id = R.drawable.ic_calendar_event_24),
                    label = stringResource(R.string.task_label_date),
                    value = taskDate?.let { dateFormatter.format(it) }
                        ?: stringResource(R.string.expense_date_not_selected),
                    onClick = { taskDatePicker.show() }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.task_label_remind_me), color = Color.White, style = MaterialTheme.typography.bodyLarge)
                    Switch(checked = remindMe, onCheckedChange = { remindMe = it })
                }
            }

            if (remindMe) {
                item {
                    PetDetailRow(
                        icon = painterResource(id = R.drawable.ic_calendar_event_24),
                        label = stringResource(R.string.task_label_date),
                        value = reminderDateTime?.time?.let { dateFormatter.format(it) } ?: "",
                        onClick = {
                            if (taskDate == null) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.validation_select_date_first),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                reminderDatePicker.show()
                            }
                        }
                    )
                }
                item {
                    PetDetailRow(
                        icon = painterResource(id = R.drawable.ic_time_24),
                        label = stringResource(R.string.task_label_time),
                        value = reminderDateTime?.time?.let { timeFormatter.format(it) }
                            ?: stringResource(R.string.task_default_time),
                        onClick = {
                            if (taskDate == null) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.validation_select_date_first),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                timePickerDialog.show()
                            }
                        }
                    )
                }
            }


            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (taskDate == null) {
                            Toast.makeText(context, context.getString(R.string.validation_select_task_date), Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val taskToSave = PetTask(
                            id = taskId ?: 0,
                            petId = petId,
                            date = taskDate!!,
                            category = taskCategory,
                            title = taskTitle,
                            note = null,
                            reminderDateTime = if (remindMe) reminderDateTime?.time else null,
                            dueDate = taskDate
                        )

                        if (taskId == null) {
                            viewModel.addTask(taskToSave)
                        } else {
                            viewModel.updateTask(taskToSave)
                        }

                        if (remindMe && reminderDateTime != null) {
                            val defaultPetName = context.getString(R.string.notification_pet_name_default)
                            val noDescription = context.getString(R.string.notification_task_no_description)

                            NotificationScheduler.scheduleNotification(
                                context = context,
                                time = reminderDateTime!!,
                                petName = defaultPetName,
                                eventTitle = "${context.getString(taskCategory.resourceId)}: ${taskTitle.ifBlank { noDescription }}"
                            )
                        }

                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth(0.9f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}
}