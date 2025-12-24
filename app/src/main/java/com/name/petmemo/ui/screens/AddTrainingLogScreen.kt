package com.name.petmemo.ui.screens

import AppBackground
import android.app.Application
import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.name.petmemo.ui.components.PetDetailRow
import com.name.petmemo.ui.viewmodel.PetViewModel
import com.name.petmemo.R
import com.name.petmemo.ui.components.TextFieldRow
import com.name.petmemo.data.model.TrainingLog
import com.name.petmemo.data.model.TrainingStatus
import com.name.petmemo.ui.ThemeViewModel
import com.name.petmemo.ui.ThemeViewModelFactory
import com.name.petmemo.com.name.petmemo.ui.screens.SelectionDialog
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTrainingLogScreen(
    petId: Int,
    logId: Int?,
    onBack: () -> Unit,
    viewModel: PetViewModel
) {
    val application = LocalContext.current.applicationContext as Application
    val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(application))
    val currentAppTheme by themeViewModel.theme.collectAsState()
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    var commandName by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(TrainingStatus.STARTED) }
    var notes by remember { mutableStateOf("") }
    var date by remember { mutableStateOf<Date?>(Date()) }
    var showStatusDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = logId) {
        if (logId != null) {
            val logToEdit = viewModel.getTrainingLogById(logId).firstOrNull()
            if (logToEdit != null) {
                commandName = logToEdit.commandName
                status = logToEdit.status
                notes = logToEdit.notes ?: ""
                date = logToEdit.date
            }
        }
    }

    if (showStatusDialog) {
        SelectionDialog(
            title = stringResource(R.string.training_dialog_select_status),
            options = TrainingStatus.values().map { it.displayName },
            onDismiss = { showStatusDialog = false },
            onSelect = { index ->
                status = TrainingStatus.values()[index]
                showStatusDialog = false
            }
        )
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            val calendar = Calendar.getInstance().apply { set(year, month, day) }
            date = calendar.time
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    AppBackground(appTheme = currentAppTheme) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(if (logId == null) R.string.training_add_title else R.string.training_edit_title)) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад") } },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextFieldRow(
                    value = commandName,
                    onValueChange = { commandName = it },
                    label = stringResource(R.string.training_label_command),
                    icon = painterResource(id = R.drawable.ic_training)
                )

                PetDetailRow(
                    icon = painterResource(id = R.drawable.ic_status),
                    label = stringResource(R.string.training_label_status),
                    value = status.displayName,
                    onClick = { showStatusDialog = true }
                )

                PetDetailRow(
                    icon = painterResource(id = R.drawable.ic_calendar_event_24),
                    label = stringResource(R.string.training_label_start_date),
                    value = date?.let { dateFormatter.format(it) }
                        ?: stringResource(R.string.expense_date_not_selected),
                    onClick = { datePickerDialog.show() }
                )

                TextFieldRow(
                    value = notes,
                    onValueChange = { notes = it },
                    label = stringResource(R.string.training_label_notes),
                    icon = painterResource(id = R.drawable.ic_note_24),
                    modifier = Modifier.height(120.dp)
                )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (commandName.isBlank()) {
                        Toast.makeText(context, context.getString(R.string.validation_enter_command_name), Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val logToSave = TrainingLog(
                        id = logId ?: 0,
                        petId = petId,
                        date = date ?: Date(),
                        commandName = commandName,
                        status = status,
                        notes = notes.ifBlank { null }
                    )
                    if (logId == null) {
                        viewModel.addTrainingLog(logToSave)
                    } else {
                        viewModel.updateTrainingLog(logToSave)
                    }
                    onBack()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}
}