package com.name.petmemo.ui.screens

import AppBackground
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.name.petmemo.ui.viewmodel.PetViewModel
import com.name.petmemo.ui.viewmodel.AuthViewModel
import com.name.petmemo.data.model.Gender
import com.name.petmemo.data.model.Pet
import com.name.petmemo.data.model.PetTask
import com.name.petmemo.data.getPetAgeString
import com.name.petmemo.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavHostController
import com.name.petmemo.data.model.getDisplayName
import kotlin.collections.filter
import com.name.petmemo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentAppTheme: AppTheme,
    drawerState: DrawerState,
    scope: CoroutineScope,
    onMenuClick: () -> Unit,
    authViewModel: AuthViewModel,
    petViewModel: PetViewModel,
    onNavigateToPetDetail: (Int) -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToContacts: () -> Unit,
    onNavigateToCalculator: () -> Unit,
    onNavigateToUseful: () -> Unit,
    onNavigateToChecklists: () -> Unit,
    onNavigateToDocuments: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToAddPet: () -> Unit,
    isPremiumUser: Boolean,
    petsList: List<Pet>,
    onManageSubscriptionClick: () -> Unit,
    navController: NavHostController
) {
    val pets by petViewModel.allPets.collectAsState(initial = emptyList())
    val tasks by petViewModel.allTasks.collectAsState(initial = emptyList())

    var petToDelete by remember { mutableStateOf<Pet?>(null) }
    var showRemindersDialog by remember { mutableStateOf(false) }

    val upcomingReminders = tasks
        .filter { it.reminderDateTime?.after(Date()) ?: false }
        .sortedBy { it.reminderDateTime }
        .take(5)

    AppBackground(appTheme = currentAppTheme) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                AppDrawerContent(
                    onNavigateToHome = { /* Already here */ },
                    onNavigateToReports = onNavigateToReports,
                    onNavigateToCalculator = onNavigateToCalculator,
                    onNavigateToUseful = onNavigateToUseful,
                    onNavigateToChecklists = onNavigateToChecklists,
                    onNavigateToDocuments = onNavigateToDocuments,
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToAbout = onNavigateToAbout,
                    onLogoutClick = { authViewModel.logoutUser() },
                    closeDrawer = { scope.launch { drawerState.close() } },
                    navController = navController,
                    isPremiumUser = true,
                    onManageSubscriptionClick = {}


                )
            }
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(R.string.pet_list_title), fontWeight = FontWeight.Bold) },
                        navigationIcon = { IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, "Меню") } },
                        actions = {
                            BadgedBox(
                                badge = {
                                    if (upcomingReminders.isNotEmpty()) {
                                        Badge { Text("${upcomingReminders.size}") }
                                    }
                                }
                            ) {
                                IconButton(onClick = {
                                    onNavigateToAddPet()
                                }) {
                                    Icon(Icons.Default.Notifications, stringResource(R.string.pet_list_notifications_icon_desc))
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary, // Цвет из темы
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    if (pets.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.pet_list_empty_message),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(pets) { pet ->
                                PetCard(
                                    pet = pet,
                                    onCardClick = { onNavigateToPetDetail(pet.id) },
                                    onDeleteClick = { petToDelete = pet }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showRemindersDialog) {
        AlertDialog(
            onDismissRequest = { showRemindersDialog = false },
            titleContentColor = Color.Black,
            textContentColor = Color.Black,
            text = {
                if (upcomingReminders.isEmpty()) {
                    Text(stringResource(R.string.reminder_dialog_empty))
                } else {
                    Column {
                        upcomingReminders.forEach { task ->
                            val petName = pets.find { it.id == task.petId }?.name ?: stringResource(R.string.reminder_dialog_general_task)
                            ReminderItem(task = task, petName = petName)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRemindersDialog = false }) { Text("ОК") }
            },
            containerColor = MaterialTheme.colorScheme.surface,
        )
    }

    if (petToDelete != null) {
        AlertDialog(
            onDismissRequest = { petToDelete = null },
            titleContentColor = Color.Black,
            textContentColor = Color.Black,
            title = { Text(stringResource(R.string.dialog_confirmation_title)) },
            text = { Text(stringResource(R.string.dialog_delete_pet_text, petToDelete!!.name)) },
            confirmButton = {
                TextButton(onClick = { petViewModel.deletePet(petToDelete!!); petToDelete = null }) {
                    Text(stringResource(R.string.dialog_delete_pet_button), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { petToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun ReminderItem(task: PetTask, petName: String) {
    // ИСПРАВЛЕНИЕ: Используем Locale.getDefault()
    val dateFormatter = remember { SimpleDateFormat("dd MMMM, EEE", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(task.title ?: "", fontWeight = FontWeight.Bold, color = Color.Black)
            Text("$petName • ${task.category.getDisplayName()}", style = MaterialTheme.typography.bodySmall, color = Color.Black)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(dateFormatter.format(task.date), color = Color.Black)
            task.reminderDateTime?.let {
                Text(timeFormatter.format(it), fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PetCard(
    pet: Pet,
    onCardClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val glowBrush = Brush.radialGradient(
        0.5f to when (pet.gender) {
            Gender.MALE -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.9f)
            Gender.FEMALE -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.9f)
            else -> Color.Transparent
        },
        1.0f to Color.Transparent
    )
    Card(
        modifier = Modifier.fillMaxWidth().combinedClickable(onClick = onCardClick, onLongClick = onDeleteClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(70.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(brush = glowBrush, shape = CircleShape))
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .border(width = 2.dp, color = MaterialTheme.colorScheme.outlineVariant, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (pet.photoUri != null && pet.photoUri.isNotEmpty()) {
                        val density = LocalDensity.current
                        val targetSizeDp = 40.dp
                        with(density) { targetSizeDp.roundToPx() }

                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(pet.photoUri)
                                .crossfade(true)
                                .size(with(density) { 120.dp.roundToPx() })
                                .build(),
                            contentDescription = stringResource(R.string.pet_card_photo_desc),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_placeholder_pet_avatar),
                            contentDescription = stringResource(R.string.pet_card_photo_desc),
                            modifier = Modifier.size(40.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pet.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${pet.type} • ${getPetAgeString(birthDate = pet.birthDate)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}