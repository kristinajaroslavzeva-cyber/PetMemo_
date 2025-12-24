package com.name.petmemo.ui.screens

import AppBackground
import android.Manifest
import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.name.petmemo.data.*
import com.name.petmemo.ui.ThemeViewModel
import com.name.petmemo.ui.ThemeViewModelFactory
import com.name.petmemo.ui.theme.*
import kotlinx.coroutines.flow.firstOrNull
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalDensity
import coil.request.ImageRequest
import com.name.petmemo.ui.components.PetDetailRow
import com.name.petmemo.ui.viewmodel.PetViewModel
import com.name.petmemo.R
import com.name.petmemo.ui.components.SwitchCardRow
import com.name.petmemo.ui.components.TextFieldRow
import com.name.petmemo.data.model.Gender
import com.name.petmemo.data.model.Pet
import com.name.petmemo.data.model.PetType
import com.name.petmemo.ui.components.getGenderString

private fun createImageUri(context: Context): Uri {
    val imageFile = File(context.cacheDir, "images/pet_photo_${System.currentTimeMillis()}.jpg")
    imageFile.parentFile?.mkdirs()
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(
    petId: Int?,
    onBack: () -> Unit,
    viewModel: PetViewModel
) {
    val application = LocalContext.current.applicationContext as Application
    val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(application))
    val currentAppTheme by themeViewModel.theme.collectAsState()
    val context = LocalContext.current
    var petName by remember { mutableStateOf("") }
    var petPhotoUri by remember { mutableStateOf<String?>(null) }
    var selectedPetType by remember { mutableStateOf(PetType.DOG) }
    var selectedGender by remember { mutableStateOf(Gender.UNKNOWN) }
    var selectedBreed by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf<Date?>(null) }
    var familyDate by remember { mutableStateOf<Date?>(null) }
    var isCastrated by remember { mutableStateOf(false) }
    var hasMicrochip by remember { mutableStateOf(false) }
    var showTrainingLog by remember { mutableStateOf(true) }
    var petNote by remember { mutableStateOf("") }

    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }


    //... inside AddPetScreen composable

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // FIX: Provide a unique file name for the new file.
            val newFileName = "pet_photo_${System.currentTimeMillis()}.jpg"
            val permanentUri = copyFileToInternalStorage(context, uri, newFileName)
            petPhotoUri = permanentUri?.toString()
        }
    }

//... rest of the file


    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isSuccess: Boolean ->
        if (isSuccess) {
            petPhotoUri = tempPhotoUri?.toString()
        }
    }

    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        } else {
            Toast.makeText(context, context.getString(R.string.permission_gallery_denied), Toast.LENGTH_SHORT).show()
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val newUri = createImageUri(context)
            tempPhotoUri = newUri
            cameraLauncher.launch(newUri)
        } else {
            Toast.makeText(context, context.getString(R.string.permission_camera_denied), Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(key1 = petId) {
        if (petId != null) {
            val petToEdit = viewModel.getPetById(petId).firstOrNull()
            if (petToEdit != null) {
                petName = petToEdit.name
                petPhotoUri = petToEdit.photoUri
                selectedPetType = stringToPetType(petToEdit.type)
                selectedGender = petToEdit.gender
                selectedBreed = petToEdit.breed ?: ""
                selectedColor = petToEdit.color ?: ""
                birthDate = petToEdit.birthDate
                familyDate = petToEdit.familyDate
                isCastrated = petToEdit.isCastrated
                hasMicrochip = petToEdit.hasMicrochip
                showTrainingLog = petToEdit.showTrainingLog
                petNote = petToEdit.note ?: ""
            }
        }
    }
    when (selectedGender) {
        Gender.MALE -> Color.Blue.copy(alpha = 0.8f)
        Gender.FEMALE -> OriginalPetPinkGlow.copy(alpha = 0.8f)
        else -> Color.Transparent
    }


    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    var showTypeDialog by remember { mutableStateOf(false) }
    var showGenderDialog by remember { mutableStateOf(false) }
    var showColorDialog by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val birthDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            calendar.set(year, month, day)
            birthDate = calendar.time
        },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )

    val familyDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            calendar.set(year, month, day)
            familyDate = calendar.time
        },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )

    val avatarBorderColor = when (selectedGender) {
        Gender.MALE -> Color.Blue
        Gender.FEMALE -> Color.Magenta
        else -> Color.Transparent
    }

    val onSave = {
        val currentPetName = petName.trim()
        if (currentPetName.isBlank()) {
            Toast.makeText(context, context.getString(R.string.validation_pet_name_empty), Toast.LENGTH_SHORT).show()
        } else {
            val petToSave = Pet(
                id = petId ?: 0,
                name = currentPetName,
                photoUri = petPhotoUri,
                type = selectedPetType.getRawString(context),
                gender = selectedGender,
                breed = selectedBreed.trim().ifBlank { null },
                color = selectedColor.trim().ifBlank { null },
                birthDate = birthDate,
                familyDate = familyDate,
                isCastrated = isCastrated,
                hasMicrochip = hasMicrochip,
                showTrainingLog = showTrainingLog,
                note = petNote.trim().ifBlank { null }
            )

            if (petId == null) {
                viewModel.addPet(petToSave)
            } else {
                viewModel.updatePet(petToSave)
            }
            onBack()
        }
    }

    AppBackground(appTheme = currentAppTheme) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(if (petId == null) R.string.add_pet_title else R.string.pet_edit_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(
                                R.string.back))
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
                    onClick = onSave,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ) {
                    Icon(Icons.Filled.Check, stringResource(R.string.save))
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .border(
                                width = 4.dp,
                                brush = Brush.radialGradient(listOf(avatarBorderColor, Color.Transparent)),
                                shape = CircleShape
                            )
                            .padding(6.dp)
                            .clip(CircleShape)
                            .background(OriginalAccentGreen.copy(alpha = 0.1f))
                            .clickable { showImageSourceDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (petPhotoUri != null) {
                            val density = LocalDensity.current
                            val targetSizeDp = 100.dp
                            with(density) { targetSizeDp.roundToPx() }

                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(petPhotoUri)
                                    .crossfade(true)
                                    .size(with(density) { 120.dp.roundToPx() })
                                    .build(),
                                contentDescription = stringResource(R.string.pet_photo_description),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_placeholder_pet_avatar),
                                contentDescription = stringResource(R.string.pet_photo_description),
                                modifier = Modifier.size(80.dp),
                                colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.7f))
                            )
                        }
                        Icon(
                            Icons.Default.CameraAlt,
                            stringResource(R.string.pet_add_photo),
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = (-4).dp, y = (-4).dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(OriginalAccentGreen)
                                .padding(6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                    item {
                        TextFieldRow(
                            value = petName,
                            onValueChange = { petName = it },
                            label = stringResource(R.string.pet_label_name),
                            icon = painterResource(id = R.drawable.ic_pet_name_24),
                            singleLine = true
                        )
                    }
                    item {
                        PetDetailRow(
                            icon = painterResource(id = R.drawable.ic_pet_type_24),
                            label = stringResource(R.string.pet_label_type),
                            value = getPetTypeString(selectedPetType),
                            onClick = { showTypeDialog = true },
                        )
                    }
                    item {
                        PetDetailRow(
                            icon = painterResource(id = R.drawable.ic_gender_24),
                            label = stringResource(R.string.pet_label_gender),
                            value = getGenderString(selectedGender),
                            onClick = { showGenderDialog = true }
                        )
                    }
                    item {
                        TextFieldRow(
                            value = selectedBreed, onValueChange = { selectedBreed = it },
                            label = stringResource(R.string.pet_label_breed),
                            icon = painterResource(id = R.drawable.ic_breed_24), singleLine = true
                        )
                    }
                    item {
                        PetDetailRow(
                            icon = painterResource(id = R.drawable.ic_color_24),
                            label = stringResource(R.string.pet_label_color),
                            value = selectedColor.ifBlank { stringResource(R.string.pet_color_not_specified) },
                            onClick = { showColorDialog = true }
                        )
                    }
                    item {
                        PetDetailRow(
                            icon = painterResource(id = R.drawable.ic_calendar_event_24),
                            label = stringResource(R.string.pet_label_birth_date),
                            value = birthDate?.let { dateFormatter.format(it) }
                                ?: stringResource(R.string.expense_date_not_selected),
                            onClick = { birthDatePickerDialog.show() }
                        )
                    }
                    item {
                        PetDetailRow(
                            icon = painterResource(id = R.drawable.ic_family_date_24),
                            label = stringResource(R.string.pet_label_family_date),
                            value = familyDate?.let { dateFormatter.format(it) }
                                ?: stringResource(R.string.expense_date_not_selected),
                            onClick = { familyDatePickerDialog.show() }
                        )
                    }
                    item {
                        SwitchCardRow(
                            icon = painterResource(id = R.drawable.ic_castrated_24),
                            label = stringResource(R.string.pet_label_castrated),
                            checked = isCastrated, onCheckedChange = { isCastrated = it }
                        )
                    }
                    item {
                        SwitchCardRow(
                            icon = painterResource(id = R.drawable.ic_microchip_24),
                            label = stringResource(R.string.pet_label_microchip),
                            checked = hasMicrochip, onCheckedChange = { hasMicrochip = it }
                        )
                    }
                    item {
                        SwitchCardRow(
                            icon = painterResource(id = R.drawable.ic_training),
                            label = stringResource(R.string.pet_label_show_training),
                            checked = showTrainingLog,
                            onCheckedChange = { showTrainingLog = it }
                        )
                    }
                    item {
                        TextFieldRow(
                            value = petNote, onValueChange = { petNote = it },
                            label = stringResource(R.string.pet_label_note),
                            icon = painterResource(id = R.drawable.ic_note_24),
                            modifier = Modifier.height(100.dp)
                        )
                    }
                }
            }
        }

    if (showTypeDialog) {
        AlertDialog(
            onDismissRequest = { showTypeDialog = false },
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = Color.Black, // Цвет заголовка -> черный
            textContentColor = Color.Black,  // Цвет основного текста -> черный
            title = { Text(stringResource(R.string.dialog_select_type_title)) },
            text = {
                LazyColumn {
                    items(PetType.values()) { type ->
                        Text(
                            text = getPetTypeString(type),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPetType = type
                                    showTypeDialog = false
                                }
                                .padding(16.dp)
                        )
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showTypeDialog = false }) { Text(stringResource(
                R.string.cancel)) } }
        )
    }

    if (showGenderDialog) {
        AlertDialog(
            onDismissRequest = { showGenderDialog = false },
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = Color.Black, // Цвет заголовка -> черный
            textContentColor = Color.Black,  // Цвет основного текста -> черный
            title = { Text(stringResource(R.string.dialog_select_gender_title)) },
            text = {
                Column {
                    Gender.values().forEach { gender ->
                        Text(
                            text = getGenderString(gender),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedGender = gender
                                    showGenderDialog = false
                                }
                                .padding(16.dp)
                        )
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showGenderDialog = false }) { Text(stringResource(
                R.string.cancel)) } }
        )
    }

        if (showColorDialog) {
            var customColor by remember { mutableStateOf("") }
            val defaultColors = listOf(
                stringResource(R.string.color_black), stringResource(R.string.color_white),
                stringResource(R.string.color_gray), stringResource(R.string.color_ginger),
                stringResource(R.string.color_spotted), stringResource(R.string.color_tricolor)
            )
            val dialogTextFieldColors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            AlertDialog(
                onDismissRequest = { showColorDialog = false },
                containerColor = MaterialTheme.colorScheme.background,
                title = { Text(stringResource(R.string.dialog_select_color_title), color = Color.Black) },
                text = {
                    Column {
                        TextField(
                            value = customColor,
                            onValueChange = { customColor = it },
                            label = { Text(stringResource(R.string.dialog_new_color_label), color = Color.Black) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = dialogTextFieldColors
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn {
                            items(defaultColors) { color ->
                                Text(
                                    text = color,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedColor = color; showColorDialog = false
                                        }
                                        .padding(vertical = 12.dp),
                                    color = Color.Black
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (customColor.isNotBlank()) selectedColor = customColor
                        showColorDialog = false
                    }) { Text(stringResource(R.string.confirm)) }
                },
                dismissButton = {
                    TextButton(onClick = { showColorDialog = false }) { Text(stringResource(R.string.cancel)) }
                }
            )
        }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = Color.Black,
            textContentColor = Color.Black,
            title = { Text(stringResource(R.string.dialog_image_source_title)) },
            text = {
                Column {
                    Text(
                        stringResource(R.string.dialog_source_camera),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                when (PackageManager.PERMISSION_GRANTED) {
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) -> {
                                        val newUri = createImageUri(context)
                                        tempPhotoUri = newUri
                                        cameraLauncher.launch(newUri)
                                    }

                                    else -> {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                }
                                showImageSourceDialog = false
                            }
                            .padding(vertical = 12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.dialog_source_gallery),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val permission =
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        Manifest.permission.READ_MEDIA_IMAGES
                                    } else {
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                    }
                                when (PackageManager.PERMISSION_GRANTED) {
                                    ContextCompat.checkSelfPermission(context, permission) -> {
                                        imagePickerLauncher.launch("image/*")
                                    }

                                    else -> {
                                        galleryPermissionLauncher.launch(permission)
                                    }
                                }
                                showImageSourceDialog = false
                            }
                            .padding(vertical = 12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showImageSourceDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun YesNoDialog(
    title: String,
    onDismiss: () -> Unit,
    onResult: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        titleContentColor = MaterialTheme.colorScheme.onBackground,
        textContentColor = MaterialTheme.colorScheme.onBackground,
        text = { Text(stringResource(R.string.dialog_status_title)) },
        confirmButton = {
            TextButton(onClick = { onResult(true) }) {
                Text(stringResource(R.string.dialog_yes))
            }
        },
        dismissButton = {
            TextButton(onClick = { onResult(false) }) {
                Text(stringResource(R.string.dialog_no))
            }
        }
    )
}
}

