package com.name.petmemo.ui.screens

import AppBackground
import android.app.Application
import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.name.petmemo.data.model.MedicalRecord
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*

// ----- ↓↓↓ УБЕДИТЕСЬ, ЧТО ЭТИ ФАЙЛЫ-ПОМОЩНИКИ СУЩЕСТВУЮТ ↓↓↓ -----
// Они нужны для компонентов TextFieldRow и PetDetailRow
import com.name.petmemo.ui.ThemeViewModel
import com.name.petmemo.ui.ThemeViewModelFactory



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicalRecordScreen(
    petId: Int,
    recordId: Int?,
    onBack: () -> Unit,
    viewModel: PetViewModel
) {
    val application = LocalContext.current.applicationContext as Application
    val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(application))
    val currentAppTheme by themeViewModel.theme.collectAsState()
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    var date by remember { mutableStateOf<Date>(Date()) }
    var place by remember { mutableStateOf("") }
    var specialist by remember { mutableStateOf("") }
    var symptoms by remember { mutableStateOf("") }
    var diagnosis by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }

    LaunchedEffect(key1 = recordId) {
        if (recordId != null && recordId != -1) {
            val recordToEdit = viewModel.getMedicalRecordById(recordId).firstOrNull()

            if (recordToEdit != null) {
                symptoms = recordToEdit.symptoms
                diagnosis = recordToEdit.diagnosis ?: ""
                date = recordToEdit.date

                // --- И здесь тоже загрузите данные для новых полей ---
                place = recordToEdit.clinicName ?: ""
                specialist = recordToEdit.vetName ?: ""
                comment = recordToEdit.notes ?: ""
                // ----------------------------------------------------
            }
        }
    }

    val calendar = Calendar.getInstance().apply { time = date }
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            val newCal = Calendar.getInstance().apply { set(year, month, day) }
            date = newCal.time
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    AppBackground(appTheme = currentAppTheme) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(if (recordId == null) R.string.record_add_title else R.string.record_edit_title)) },
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        PetDetailRow(
                            icon = painterResource(id = R.drawable.ic_calendar_event_24),
                            label = stringResource(R.string.record_label_date),
                            value = dateFormatter.format(date),
                            onClick = { datePickerDialog.show() }
                        )
                    }
                    item {
                        TextFieldRow(
                            value = symptoms, onValueChange = { symptoms = it },
                            label = stringResource(R.string.record_label_symptoms_required),
                            icon = painterResource(id = R.drawable.ic_symptoms),
                            modifier = Modifier.height(100.dp)
                        )
                    }
                    item {
                        TextFieldRow(
                            value = place, onValueChange = { place = it },
                            label = stringResource(R.string.record_label_place_clinic),
                            icon = painterResource(id = R.drawable.ic_place)
                        )
                    }
                    item {
                        TextFieldRow(
                            value = specialist, onValueChange = { specialist = it },
                            label = stringResource(R.string.record_label_specialist),
                            icon = painterResource(id = R.drawable.ic_specialist)
                        )
                    }
                    item {
                        TextFieldRow(
                            value = diagnosis, onValueChange = { diagnosis = it },
                            label = stringResource(R.string.record_label_diagnosis_treatment),
                            icon = painterResource(id = R.drawable.ic_diagnosis),
                            modifier = Modifier.height(120.dp)
                        )
                    }
                    item {
                        TextFieldRow(
                            value = comment, onValueChange = { comment = it },
                            label = stringResource(R.string.record_label_comment),
                            icon = painterResource(id = R.drawable.ic_note_24),
                            modifier = Modifier.height(100.dp)
                        )
                    }
                }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (symptoms.isBlank()) {
                        Toast.makeText(context, context.getString(R.string.validation_enter_symptoms), Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val recordToSave = MedicalRecord(
                        id = recordId ?: 0,
                        petId = petId,
                        date = date,
                        title = symptoms.take(40),
                        symptoms = symptoms,
                        diagnosis = diagnosis.ifBlank { null },
                        treatment = null,
                        vetName = specialist.ifBlank { null },
                        clinicName = place.ifBlank { null },
                        notes = comment.ifBlank { null },
                    )
                    if (recordId == null) {
                        viewModel.addMedicalRecord(recordToSave)
                    } else {
                        viewModel.updateMedicalRecord(recordToSave)
                    }
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(stringResource(R.string.record_save_button), color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    }
}
}