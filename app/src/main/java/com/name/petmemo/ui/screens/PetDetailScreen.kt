package com.name.petmemo.ui.screens

import AppBackground
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.name.petmemo.ui.ThemeViewModel
import com.name.petmemo.ui.ThemeViewModelFactory
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.filled.Event
import com.name.petmemo.ui.viewmodel.PetViewModel
import com.name.petmemo.R
import com.name.petmemo.ui.navigation.Routes
import com.name.petmemo.data.model.Gender
import com.name.petmemo.data.model.MedicalRecord
import com.name.petmemo.data.model.Mood
import com.name.petmemo.data.model.MoodEntry
import com.name.petmemo.data.model.Pet
import com.name.petmemo.data.model.PetTask
import com.name.petmemo.data.model.RecordWithNoteCount
import com.name.petmemo.data.model.TaskCategory
import com.name.petmemo.data.model.WeightEntry
import com.name.petmemo.data.model.TrainingLog
import com.name.petmemo.data.model.TrainingStatus
import com.name.petmemo.ui.components.getGenderString
import com.name.petmemo.ui.theme.OriginalAccentGreen
import com.name.petmemo.ui.theme.OriginalPetPinkGlow
import java.util.*
import kotlin.collections.sortedBy
import kotlin.collections.sortedByDescending
@Composable
fun TrainingDiaryTab(
    petId: Int,
    viewModel: PetViewModel,
    onEditClick: (Int) -> Unit,
    onDeleteClick: (TrainingLog) -> Unit,
    onNavigateToCreateTask: (TrainingLog) -> Unit
) {
    val trainingLogs by viewModel.allTrainingLogs(petId).collectAsState(initial = emptyList())

    if (trainingLogs.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.training_diary_empty),
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(trainingLogs) { log ->
                TrainingLogCard(
                    log = log,
                    onEditClick = { onEditClick(log.id) },
                    onDeleteClick = { onDeleteClick(log) },
                    onNavigateToCreateTask = { onNavigateToCreateTask(log) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrainingLogCard(
    log: TrainingLog,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onNavigateToCreateTask: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }
    val statusColor = when(log.status) {
        TrainingStatus.STARTED -> Color.Yellow.copy(alpha = 0.8f)
        TrainingStatus.IN_PROGRESS -> Color.Blue.copy(alpha = 0.7f)
        TrainingStatus.MASTERED -> MaterialTheme.colorScheme.secondary
    }

    Card(
        modifier = Modifier.fillMaxWidth().combinedClickable(onClick = onEditClick, onLongClick = onDeleteClick),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(id = R.drawable.ic_training), null, tint = Color.White, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(log.commandName, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(dateFormatter.format(log.date), color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onNavigateToCreateTask) {
                Icon(Icons.Default.Event, stringResource(R.string.training_create_task_desc), tint = Color.White)
            }
            Card(shape = RoundedCornerShape(50), colors = CardDefaults.cardColors(containerColor = statusColor)) {
                Text(
                    text = log.status.displayName,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
@Composable
fun PetInfoTab(pet: Pet?) {
    val dateFormatter = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }
    val avatarBorderColor = when (pet?.gender) {
        Gender.MALE -> Color.Blue.copy(alpha = 0.8f)
        Gender.FEMALE -> OriginalPetPinkGlow.copy(alpha = 0.8f)
        else -> Color.Transparent
    }

    if (pet != null) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .border(
                            width = 4.dp,
                            brush = Brush.radialGradient(
                                listOf(
                                    avatarBorderColor,
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(OriginalAccentGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (pet.photoUri != null) {
                        val density = LocalDensity.current
                        val targetSizeDp = 80.dp
                        with(density) { targetSizeDp.roundToPx() }

                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(pet.photoUri)
                                .crossfade(true)
                                .size(with(density) { 120.dp.roundToPx() })
                                .build(),
                            contentDescription = "Фото питомца",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(painter = painterResource(id = R.drawable.ic_placeholder_pet_avatar), contentDescription = "Фото питомца", modifier = Modifier.size(80.dp), colorFilter = ColorFilter.tint(OriginalAccentGreen))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = pet.name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                InfoSectionCard(title = stringResource(R.string.info_section_main)) {
                    DetailInfoRow(icon = painterResource(id = R.drawable.ic_pet_type_24), label = stringResource(
                        R.string.info_label_type), value = pet.type ?: stringResource(R.string.info_label_not_specified))
                    DetailInfoRow(icon = painterResource(id = R.drawable.ic_gender_24), label = stringResource(
                        R.string.info_label_gender), value = getGenderString(pet.gender)
                    )
                    DetailInfoRow(icon = painterResource(id = R.drawable.ic_breed_24), label = stringResource(
                        R.string.info_label_breed), value = pet.breed.takeIf { !it.isNullOrBlank() } ?: stringResource(
                        R.string.info_label_not_specified_f))
                    DetailInfoRow(icon = painterResource(id = R.drawable.ic_color_24), label = stringResource(
                        R.string.info_label_color), value = pet.color.takeIf { !it.isNullOrBlank() } ?: stringResource(
                        R.string.info_label_not_specified))
                }
            }
            item {
                InfoSectionCard(title = stringResource(R.string.info_section_dates)) {
                    DetailInfoRow(icon = painterResource(id = R.drawable.ic_calendar_event_24), label = stringResource(
                        R.string.info_label_birth_date), value = pet.birthDate?.let { dateFormatter.format(it) } ?: stringResource(
                        R.string.info_label_not_specified_f))
                    DetailInfoRow(icon = painterResource(id = R.drawable.ic_family_date_24), label = stringResource(
                        R.string.info_label_family_date), value = pet.familyDate?.let { dateFormatter.format(it) } ?: stringResource(
                        R.string.info_label_not_specified_f))
                }
            }
            item {
                InfoSectionCard(title = stringResource(R.string.info_section_medical)) {
                    DetailInfoRow(icon = painterResource(id = R.drawable.ic_castrated_24), label = stringResource(
                        R.string.info_label_castrated), value = stringResource(id = if (pet.isCastrated) R.string.info_label_yes else R.string.info_label_no))
                    DetailInfoRow(icon = painterResource(id = R.drawable.ic_microchip_24), label = stringResource(
                        R.string.info_label_microchip), value = stringResource(id = if (pet.hasMicrochip) R.string.info_label_yes else R.string.info_label_no))
                }
            }
            if (!pet.note.isNullOrBlank()) {
                item {
                    InfoSectionCard(title = stringResource(R.string.info_section_note)) {
                        Text(text = pet.note, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), color = Color.White)
                    }
                }
            }
        }
    } else {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}

@Composable
fun EventsTab(
    tasks: List<PetTask>,
    petName: String,
    viewModel: PetViewModel,
    onDeleteClick: (PetTask) -> Unit,
    onEditClick: (PetTask) -> Unit
) {
    if (tasks.isEmpty()) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.task_empty_list), color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tasks) { task ->
                TaskCard(
                    task = task,
                    petName = petName,
                    onStatusChange = { viewModel.updateTask(it) },
                    onDeleteClick = { onDeleteClick(task) },
                    onEditClick = { onEditClick(task) }
                )
            }
        }
    }
}

@Composable
fun HealthDiaryTab(
    pet: Pet,
    viewModel: PetViewModel,
    onNavigateToRecordDetail: (Int) -> Unit,
    onDeleteClick: (MedicalRecord) -> Unit,
    onNavigateToCreateTask: (MedicalRecord) -> Unit
) {
    val recordsWithCount by viewModel.getRecordsWithNoteCount(pet.id).collectAsState(initial = emptyList())
    val weightHistory by viewModel.getWeightEntriesForPet(pet.id).collectAsState(initial = emptyList())
    val moodHistory by viewModel.getMoodEntriesForPet(pet.id).collectAsState(initial = emptyList())

    var moodEntryToEdit by remember { mutableStateOf<MoodEntry?>(null) }
    var moodEntryToDelete by remember { mutableStateOf<MoodEntry?>(null) }
    var weightEntryToEdit by remember { mutableStateOf<WeightEntry?>(null) }
    var showWeightDeleteDialog by remember { mutableStateOf(false) }
    var weightEntryToDelete by remember { mutableStateOf<WeightEntry?>(null) }
    var isWeightSectionExpanded by remember { mutableStateOf(false) }
    var isMoodSectionExpanded by remember { mutableStateOf(false) }
    var newWeight by remember { mutableStateOf("") }

    if (weightEntryToEdit != null) {
        EditWeightDialog(
            entry = weightEntryToEdit!!,
            onDismiss = { weightEntryToEdit = null },
            onSave = { updatedEntry ->
                viewModel.updateWeightEntry(updatedEntry)
                weightEntryToEdit = null
            }
        )
    }
    if (showWeightDeleteDialog && weightEntryToDelete != null) {
        AlertDialog(
            onDismissRequest = { showWeightDeleteDialog = false },
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = Color.Black,
            textContentColor = Color.Black,
            title = { Text(stringResource(R.string.health_dialog_delete_title)) },
            text = { Text(stringResource(R.string.health_dialog_delete_confirm_text)) },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteWeightEntry(weightEntryToDelete!!)
                    showWeightDeleteDialog = false
                }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text(stringResource(R.string.health_dialog_delete_button))
                }
            },
            dismissButton = { Button(onClick = { showWeightDeleteDialog = false }) {
                Text(stringResource(R.string.cancel))
            } }
        )
    }
    if (moodEntryToEdit != null) {
        EditMoodDialog(
            entry = moodEntryToEdit!!,
            onDismiss = { moodEntryToEdit = null },
            onSave = { newMood ->
                viewModel.updateMoodEntry(moodEntryToEdit!!.copy(mood = newMood))
                moodEntryToEdit = null
            }
        )
    }
    if (moodEntryToDelete != null) {
        AlertDialog(
            onDismissRequest = { moodEntryToDelete = null },
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = Color.Black,
            textContentColor = Color.Black,
            title = { Text(stringResource(R.string.health_dialog_delete_title)) },
            text = { Text(stringResource(R.string.health_dialog_delete_confirm_text)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteMoodEntry(moodEntryToDelete!!)
                        moodEntryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text(stringResource(R.string.health_dialog_delete_button)) }
            },
            dismissButton = { Button(onClick = { moodEntryToDelete = null }) { Text(stringResource(R.string.cancel)) } }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { isMoodSectionExpanded = !isMoodSectionExpanded }.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.health_tracker_mood), style = MaterialTheme.typography.bodyLarge, color =Color.White.copy(alpha = 0.9f), modifier = Modifier.weight(1f))
                Icon(imageVector = if (isMoodSectionExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, "Свернуть/развернуть", tint = MaterialTheme.colorScheme.onBackground)
            }
        }
        item {
            AnimatedVisibility(visible = isMoodSectionExpanded) {
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(stringResource(R.string.health_tracker_mood_today), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val todayMood = moodHistory.find { entry -> isSameDay(Calendar.getInstance(), entry.date) }?.mood
                        Mood.values().forEach { mood ->
                            IconButton(onClick = { viewModel.addOrUpdateMoodEntry(petId = pet.id, mood = mood) }) {
                                val isSelected = todayMood == mood
                                Box(
                                    modifier = Modifier.size(48.dp).clip(CircleShape).background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Color.Transparent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = mood.emoji,
                                        fontSize = 32.sp,
                                        modifier = Modifier.alpha(if (isSelected || todayMood == null) 1f else 0.5f)
                                    )
                                }
                            }
                        }
                    }
                    Card(modifier = Modifier.fillMaxWidth().height(200.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))) {
                        val moodData = moodHistory.sortedBy { it.date }.map { it.date to it.mood.score.toFloat() }
                        SimpleLineChart(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            data = moodData,
                            lineColor = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    moodHistory.sortedByDescending { it.date }.forEach { entry ->
                        MoodHistoryItem(
                            entry = entry,
                            onEditClick = { moodEntryToEdit = entry },
                            onDeleteClick = { moodEntryToDelete = entry }
                        )
                    }
                }
            }
        }

        item { HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)) }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { isWeightSectionExpanded = !isWeightSectionExpanded }.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.health_tracker_weight), style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.9f), modifier = Modifier.weight(1f))
                Icon(imageVector = if (isWeightSectionExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, "Свернуть/развернуть", tint = MaterialTheme.colorScheme.onBackground)
            }
        }
        item {
            AnimatedVisibility(visible = isWeightSectionExpanded) {
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(modifier = Modifier.fillMaxWidth().height(200.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))) {
                        val weightData = weightHistory.sortedBy { it.date }.map { it.date to it.weightKg.toFloat() }
                        SimpleLineChart(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            data = weightData,
                            lineColor = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        OutlinedTextField(value = newWeight, onValueChange = { newWeight = it.filter { char -> char.isDigit() || char == '.' || char == ',' } }, label = { Text(stringResource(
                            R.string.health_label_weight_kg)) }, modifier = Modifier.weight(1f))
                        Button(onClick = {
                            val weightValue = newWeight.replace(',', '.').toDoubleOrNull()
                            if (weightValue != null && weightValue > 0) {
                                viewModel.addWeightEntry(
                                    WeightEntry(
                                        petId = pet.id,
                                        date = Date(),
                                        weightKg = weightValue
                                    )
                                )
                                newWeight = ""
                            }
                        }, enabled = newWeight.isNotBlank()) { Text(stringResource(R.string.health_tracker_weight_button))
                        }
                    }
                    weightHistory.sortedByDescending { it.date }.forEach { entry ->
                        WeightHistoryItem(
                            entry = entry,
                            onEditClick = { weightEntryToEdit = entry },
                            onDeleteClick = {
                                weightEntryToDelete = entry
                                showWeightDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
            Text(stringResource(R.string.health_record_section), style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.9f), modifier = Modifier.padding(bottom = 4.dp))
        }

        item {
            val context = LocalContext.current

            Column {
                if (recordsWithCount.isEmpty()) {
                    Text(stringResource(R.string.health_record_empty), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                } else {
                    recordsWithCount.forEach { recordWithCount ->
                        MedicalRecordCard(
                            recordWithCount = recordWithCount,
                            onClick = { onNavigateToRecordDetail(recordWithCount.record.id) },
                            onDeleteClick = { onDeleteClick(recordWithCount.record) },
                            onNavigateToCreateTask = { onNavigateToCreateTask(recordWithCount.record) },
                            onShareClick = { medicalRecord ->
                                shareMedicalRecord(context, medicalRecord)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

    }
}


@Composable
fun MoodHistoryItem(
    entry: MoodEntry,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("d MMMM yyyy",  Locale.getDefault()) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text(text = entry.mood.emoji, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = entry.mood.getDisplayName(), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                    Text(text = dateFormatter.format(entry.date), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
            }
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, stringResource(R.string.item_edit_desc), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, stringResource(R.string.item_delete_desc), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWeightDialog(
    entry: WeightEntry,
    onDismiss: () -> Unit,
    onSave: (WeightEntry) -> Unit
) {
    var editedWeight by remember { mutableStateOf(entry.weightKg.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = Color.Black,
        textContentColor = Color.Black,
        title = { Text(stringResource(R.string.health_dialog_edit_weight_title)) },
        text = {
            TextField(
                value = editedWeight,
                onValueChange = { editedWeight = it.filter { char -> char.isDigit() || char == '.' || char == ',' } },
                label = { Text(stringResource(R.string.health_label_weight_kg), color = MaterialTheme.colorScheme.onSurface) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
            Button(onClick = {
                val newWeight = editedWeight.replace(',', '.').toDoubleOrNull()
                if (newWeight != null && newWeight > 0) {
                    onSave(entry.copy(weightKg = newWeight))
                }
            }) { Text(stringResource(R.string.save))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) {
            Text(stringResource(R.string.cancel))
        } }
    )
}

@Composable
fun MoodCalendar(moodHistory: List<MoodEntry>) {
    val moodMap = moodHistory.associate {
        it.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() to it.mood.emoji
    }

    val currentMonth = YearMonth.now()
    val startMonth = currentMonth.minusMonths(100)
    val endMonth = currentMonth.plusMonths(100)
    val firstDayOfWeek = firstDayOfWeekFromLocale()

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                Day(date = day.date, emoji = moodMap[day.date])
            },
            monthHeader = { month ->
                MonthHeader(month = month.yearMonth)
            }
        )
    }
}

@Composable
private fun Day(
    date: LocalDate,
    emoji: String?
) {
    Column(
        modifier = Modifier
            .size(40.dp)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = Color.Black,
            style = MaterialTheme.typography.bodyMedium
        )
        if (emoji != null) {
            Text(
                text = emoji,
                fontSize = 12.sp
            )
        }
    }
}
@Composable
private fun MonthHeader(month: YearMonth) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = month.format(DateTimeFormatter.ofPattern("MMMM yyyy",  Locale.getDefault())),
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )
    }
}
@Composable
fun WeightHistoryItem(
    entry: WeightEntry,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("d MMMM yyyy",  Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Иконка для веса
                Icon(
                    imageVector = Icons.Default.MonitorWeight,
                    contentDescription = stringResource(R.string.item_weight_desc),
                    tint = OriginalAccentGreen
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f кг", entry.weightKg),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = dateFormatter.format(entry.date),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.item_edit_desc), tint = Color.White)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.item_delete_desc), tint = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MedicalRecordCard(
    recordWithCount: RecordWithNoteCount,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onNavigateToCreateTask: () -> Unit,
    onShareClick: (MedicalRecord) -> Unit
) {
    val record = recordWithCount.record
    val dateFormatter = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }
    LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onDeleteClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = record.symptoms,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = onNavigateToCreateTask) {
                    Icon(Icons.Default.Event, stringResource(R.string.training_create_task_desc), tint = Color.White)
                }
                IconButton(onClick = { onShareClick(record) }) {
                    Icon(Icons.Default.Share, stringResource(R.string.record_card_share_desc), tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dateFormatter.format(record.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )

                if (recordWithCount.noteCount > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.AutoMirrored.Filled.Comment,
                            contentDescription = "Комментарии",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${recordWithCount.noteCount}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            if (!record.diagnosis.isNullOrBlank()) {
                Text(
                    text = stringResource(R.string.record_card_diagnosis_format, record.diagnosis),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun getIconForTaskCategory(category: TaskCategory): Painter {
    return when (category) {
        TaskCategory.VET_VISIT -> painterResource(id = R.drawable.ic_vet_visit)
        TaskCategory.VACCINATION -> painterResource(id = R.drawable.ic_vaccination)
        TaskCategory.GROOMING -> painterResource(id = R.drawable.ic_grooming)
        TaskCategory.MEDICATION -> painterResource(id = R.drawable.ic_medication)
        TaskCategory.TRAINING -> painterResource(id = R.drawable.ic_training)
        TaskCategory.SHOPPING -> painterResource(id = R.drawable.ic_shopping)
        TaskCategory.CUSTOM -> painterResource(id = R.drawable.ic_custom_task)
    }
}
@Composable
private fun DetailInfoRow(icon: Painter, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
@Composable
private fun InfoSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp)
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
            content()
        }
    }
}
fun isSameDay(cal1: Calendar, date2: Date): Boolean {
    val cal2 = Calendar.getInstance()
    cal2.time = date2
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

fun isSameMonth(cal1: Calendar, date2: Date): Boolean {
    val cal2 = Calendar.getInstance()
    cal2.time = date2
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
}

@Composable
fun SimpleLineChart(
    modifier: Modifier = Modifier,
    data: List<Pair<Date, Float>>,
    lineColor: Color = MaterialTheme.colorScheme.secondary,
    labelTextColor: Color = MaterialTheme.colorScheme.onSurface,
    labelFontSize: Float = 10f
) {
    if (data.size < 2) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                stringResource(R.string.health_chart_min_entries),
                color = labelTextColor.copy(alpha = 0.7f),    textAlign = TextAlign.Center // ИСПРАВЛЕНО
            )
        }
        return
    }
    val density = LocalDensity.current

    val paint = remember(labelTextColor, labelFontSize, density) {
        Paint().apply {
            color = labelTextColor.toArgb()
            textSize = with(density) { labelFontSize.sp.toPx() }
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
    }

    val dateLabelPaint = remember(labelTextColor, labelFontSize, density) {
        Paint().apply {
            color = labelTextColor.toArgb()
            textSize = with(density) { (labelFontSize - 2).sp.toPx() }
            textAlign = Paint.Align.RIGHT
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
    }


    val (minY, maxY) = remember(data) {
        val values = data.map { it.second }
        val min = (values.minOrNull() ?: 0f) - 0.5f
        val max = (values.maxOrNull() ?: 1f) + 0.5f
        min to max
    }
    val (minXMillis, maxXMillis) = remember(data) {
        val dates = data.map { it.first.time }
        (dates.minOrNull() ?: 0L) to (dates.maxOrNull() ?: 1L)
    }

    Canvas(modifier = modifier) {
        val paddingHorizontal = 30.dp.toPx()
        val paddingVertical = 20.dp.toPx()

        val chartWidth = size.width - 2 * paddingHorizontal
        val chartHeight = size.height - 2 * paddingVertical

        if (chartWidth <= 0 || chartHeight <= 0) return@Canvas

        val xRange = (maxXMillis - minXMillis).toFloat().coerceAtLeast(1f)
        val yRange = (maxY - minY).toFloat().coerceAtLeast(1f)

        drawLine(
            color = labelTextColor.copy(alpha = 0.5f),
            start = Offset(paddingHorizontal, paddingVertical),
            end = Offset(paddingHorizontal, size.height - paddingVertical),
            strokeWidth = 1f
        )
        drawLine(
            color = labelTextColor.copy(alpha = 0.5f),
            start = Offset(paddingHorizontal, size.height - paddingVertical),
            end = Offset(size.width - paddingHorizontal, size.height - paddingVertical),
            strokeWidth = 1f
        )

        val points = data.map {
            val x = paddingHorizontal + ((it.first.time - minXMillis) / xRange) * chartWidth
            val y = size.height - paddingVertical - ((it.second - minY) / yRange) * chartHeight
            Offset(x, y)
        }

        for (i in 0 until points.size - 1) {
            drawLine(
                color = lineColor,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 5f
            )
        }
        points.forEach {
            drawCircle(
                color = lineColor,
                radius = 8f,
                center = it
            )
        }

        val numYLabels = 3
        for (i in 0 until numYLabels) {
            val value = minY + (yRange / (numYLabels - 1)) * i
            val yPos = size.height - paddingVertical - ((value - minY) / yRange) * chartHeight
            drawContext.canvas.nativeCanvas.drawText(
                String.format(Locale.getDefault(), "%.1f", value),
                paddingHorizontal / 2,
                yPos + paint.textSize / 3,
                paint
            )
        }

        val dateFormatter = SimpleDateFormat("dd.MM", Locale.getDefault())
        val numXLabels = minOf(data.size, 3)
        if (numXLabels > 0) {
            val step = (data.size - 1) / (numXLabels - 1).coerceAtLeast(1)
            for (i in 0 until numXLabels) {
                val index = (i * step).coerceAtMost(data.size - 1)
                val date = data[index].first
                val xPos = paddingHorizontal + ((date.time - minXMillis) / xRange) * chartWidth

                val centeredDateLabelPaint = Paint(dateLabelPaint).apply {
                    textAlign = Paint.Align.CENTER
                }

                drawContext.canvas.nativeCanvas.drawText(
                    dateFormatter.format(date),
                    xPos,
                    size.height - paddingVertical / 2 + centeredDateLabelPaint.textSize,
                    centeredDateLabelPaint
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMoodDialog(
    entry: MoodEntry,
    onDismiss: () -> Unit,
    onSave: (Mood) -> Unit
) {
    var selectedMood by remember { mutableStateOf(entry.mood) }
    val dateFormatter = remember { SimpleDateFormat("d MMMM", Locale.getDefault()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        // Цвета для AlertDialog
        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = Color.Black,
        textContentColor = Color.Black,
        title = { Text(stringResource(R.string.health_dialog_edit_mood_title)) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.health_dialog_edit_mood_question, dateFormatter.format(entry.date)))
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    Mood.values().forEach { mood ->
                        val isSelected = selectedMood == mood
                        Box(
                            modifier = Modifier.size(48.dp).clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)) // Обновил фон для невыбранного
                                .clickable { selectedMood = mood },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = mood.emoji, fontSize = 32.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(selectedMood) }) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PetDetailScreen(
    petId: Int,
    navController: NavController,
    onBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onNavigateToRecordDetail: (Int) -> Unit,
    onNavigateToAddMedicalRecord: (Int) -> Unit,
    onNavigateToAddEvent: () -> Unit,
    onNavigateToEditEvent: (Int) -> Unit,
    onNavigateToAddExpense: (petId: Int) -> Unit,
    onNavigateToEditExpense: (expenseId: Int) -> Unit,
    onNavigateToEditTrainingLog: (logId: Int) -> Unit,
    onNavigateToAddTrainingLog: (petId: Int) -> Unit,
    viewModel: PetViewModel
) {
    val application = LocalContext.current.applicationContext as Application
    val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(application))
    val currentAppTheme by themeViewModel.theme.collectAsState()
    val context = LocalContext.current

    val pet by viewModel.getPetById(petId).collectAsState(initial = null)

    val tabs = remember(pet) {
        val info = context.getString(R.string.tab_info)
        val health = context.getString(R.string.tab_health)
        val training = context.getString(R.string.tab_training)

        if (pet?.showTrainingLog == true) listOf(info, health, training)
        else listOf(info, health)
    }
    val pagerState = rememberPagerState { tabs.size }
    val scope = rememberCoroutineScope()

    var recordToDelete by remember { mutableStateOf<MedicalRecord?>(null) }
    var showMedicalRecordDeleteDialog by remember { mutableStateOf(false) }
    var trainingLogToDelete by remember { mutableStateOf<TrainingLog?>(null) }
    var showTrainingLogDeleteDialog by remember { mutableStateOf(false) }

    if (showMedicalRecordDeleteDialog && recordToDelete != null) {
        AlertDialog(
            onDismissRequest = { showMedicalRecordDeleteDialog = false },
            containerColor = MaterialTheme.colorScheme.background,
            title = { Text(stringResource(R.string.dialog_confirmation_title), color = Color.Black) },
            text = { Text(stringResource(R.string.dialog_delete_record_text, recordToDelete!!.symptoms), color = Color.Black) },
            confirmButton = { Button(onClick = { viewModel.deleteMedicalRecord(recordToDelete!!); showMedicalRecordDeleteDialog = false }) { Text("Удалить") } },
            dismissButton = { Button(onClick = { showMedicalRecordDeleteDialog = false }) { Text("Отмена") } }
        )
    }
    if (showTrainingLogDeleteDialog && trainingLogToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showTrainingLogDeleteDialog = false
                trainingLogToDelete = null
            },
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = Color.Black,
            textContentColor = Color.Black,
            title = { Text(stringResource(R.string.dialog_confirmation_title)) },
            text = { Text(stringResource(R.string.dialog_delete_training_text)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTrainingLog(trainingLogToDelete!!)
                        showTrainingLogDeleteDialog = false
                        trainingLogToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text(stringResource(R.string.dialog_delete_button)) }
            },
            dismissButton = {
                Button(onClick = {
                    showTrainingLogDeleteDialog = false
                    trainingLogToDelete = null
                }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    AppBackground(appTheme = currentAppTheme) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(pet?.name ?: stringResource(R.string.pet_detail_loading)) },
                    navigationIcon = { IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                    } },
                    actions = {
                        if (pagerState.currentPage == 0) {
                            IconButton(onClick = { pet?.id?.let { onNavigateToEdit(it) } }) {
                                Icon(Icons.Default.Edit, stringResource(R.string.pet_detail_edit_icon_desc))
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
                if (tabs.getOrNull(pagerState.currentPage) == stringResource(R.string.tab_health)) {
                    FloatingActionButton(onClick = { onNavigateToAddMedicalRecord(petId) }, containerColor = MaterialTheme.colorScheme.secondary) {
                        Icon(Icons.Default.Add, stringResource(R.string.pet_detail_add_record_desc), tint = MaterialTheme.colorScheme.onSecondary)
                    }
                } else if (tabs.getOrNull(pagerState.currentPage) == stringResource(R.string.tab_training)) {
                    FloatingActionButton(onClick = { onNavigateToAddTrainingLog(petId) }, containerColor = MaterialTheme.colorScheme.secondary) {
                        Icon(Icons.Default.Add, stringResource(R.string.pet_detail_add_training_desc), tint = MaterialTheme.colorScheme.onSecondary)
                    }
                }
            }
        ) { paddingValues ->
            val currentPet = pet
            if (currentPet == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                                text = { Text(title, fontSize = 12.sp, maxLines = 1) }
                            )
                        }
                    }

                    HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                        when (page) {
                            0 -> PetInfoTab(pet = currentPet)
                            1 -> HealthDiaryTab(
                                pet = currentPet,
                                viewModel = viewModel,
                                onNavigateToRecordDetail = onNavigateToRecordDetail,
                                onDeleteClick = { record ->
                                    recordToDelete = record
                                    showMedicalRecordDeleteDialog = true
                                },
                                onNavigateToCreateTask = { medicalRecord ->
                                    val title = context.getString(R.string.health_record_task_title_format, medicalRecord.symptoms)
                                    val encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.name())
                                    navController.navigate("${Routes.ADD_EVENT}/${currentPet.id}?defaultTitle=$encodedTitle")
                                }
                            )
                            2 -> TrainingDiaryTab(
                                petId = currentPet.id,
                                viewModel = viewModel,
                                onEditClick = { logId -> onNavigateToEditTrainingLog(logId) },
                                onDeleteClick = { log ->
                                    trainingLogToDelete = log
                                    showTrainingLogDeleteDialog = true
                                },
                                onNavigateToCreateTask = { trainingLog ->
                                    val title = context.getString(R.string.training_task_title_format, trainingLog.commandName)
                                    val encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.name())
                                    navController.navigate("${Routes.ADD_EVENT}/${currentPet.id}?defaultTitle=$encodedTitle")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
private fun shareMedicalRecord(context: Context, record: MedicalRecord) {
    val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

    val shareText = StringBuilder().apply {
        append(context.getString(R.string.share_medical_record_title) + "\n")
        append(context.getString(R.string.share_medical_record_date, dateFormatter.format(record.date)) + "\n")
        append(context.getString(R.string.share_medical_record_symptoms, record.symptoms) + "\n")
        if (!record.diagnosis.isNullOrBlank()) {
            append(context.getString(R.string.share_medical_record_diagnosis, record.diagnosis) + "\n")
        }
        if (!record.treatment.isNullOrBlank()) {
            append(context.getString(R.string.share_medical_record_treatment, record.treatment) + "\n")
        }
        if (!record.vetName.isNullOrBlank()) {
            append(context.getString(R.string.share_medical_record_vet, record.vetName) + "\n")
        }
        if (!record.clinicName.isNullOrBlank()) {
            append(context.getString(R.string.share_medical_record_clinic, record.clinicName) + "\n")
        }
        if (!record.notes.isNullOrBlank()) {
            append(context.getString(R.string.share_medical_record_notes, record.notes) + "\n")
        }
    }.toString()

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_chooser_record)))
}





