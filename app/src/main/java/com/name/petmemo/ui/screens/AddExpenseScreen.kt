package com.name.petmemo.ui.screens

import AppBackground
import android.app.Application
import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.name.petmemo.ui.components.PetDetailRow
import com.name.petmemo.ui.viewmodel.PetViewModel
import com.name.petmemo.R
import com.name.petmemo.data.model.SettingsManager
import com.name.petmemo.ui.components.TextFieldRow
import com.name.petmemo.data.model.Expense
import com.name.petmemo.com.name.petmemo.ui.screens.SelectionDialog
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import com.name.petmemo.ui.ThemeViewModel
import com.name.petmemo.ui.ThemeViewModelFactory
import java.util.*
import kotlin.collections.map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    petId: Int,
    expenseId: Int?,
    onBack: () -> Unit,
    viewModel: PetViewModel,
    settingsManager: SettingsManager = SettingsManager(LocalContext.current)
) {
    val application = LocalContext.current.applicationContext as Application
    val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(application))
    val currentAppTheme by themeViewModel.theme.collectAsState()
    val context = LocalContext.current
    val allPets by viewModel.allPets.collectAsState(initial = emptyList())
    val allCategories by viewModel.allExpenseCategories.collectAsState(initial = emptyList())

    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf<Date?>(Date()) }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPetId by remember { mutableStateOf<Int?>(if (petId == -1) null else petId) }

    var showPetDialog by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var showNewCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryText by remember { mutableStateOf("") }
    var showAmountDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = expenseId) {
        if (expenseId != null) {
            val expenseToEdit = viewModel.getExpenseById(expenseId).firstOrNull()
            if (expenseToEdit != null) {
                amount = expenseToEdit.amount.toString()
                date = expenseToEdit.date
                category = expenseToEdit.category
                description = expenseToEdit.description ?: ""
                selectedPetId = if (expenseToEdit.petId == -1) null else expenseToEdit.petId
            }
        }
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

    if (showPetDialog) {
        val petOptions = listOf(stringResource(R.string.expense_total)) + allPets.map { it.name }
        SelectionDialog(
            title = stringResource(R.string.expense_label_expense),
            options = petOptions,
            onDismiss = { showPetDialog = false },
            onSelect = { index ->
                selectedPetId = if (index == 0) null else allPets[index - 1].id
                showPetDialog = false
            }
        )
    }

    if (showNewCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showNewCategoryDialog = false },
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = Color.Black,
            textContentColor = Color.Black,
            title = { Text(stringResource(R.string.expense_new_category_title)) },
            text = {
                TextField(
                    value = newCategoryText,
                    onValueChange = { newCategoryText = it },
                    label = { Text(stringResource(R.string.expense_category_name_label), color = Color.Black) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        focusedIndicatorColor = Color.Black,
                        unfocusedIndicatorColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newCategoryText.isNotBlank()) {
                        category = newCategoryText
                        showNewCategoryDialog = false
                        newCategoryText = ""
                    }
                }) { Text(stringResource(R.string.save)) }
            }
        )
    }

    if (showCategoryDialog) {
        val defaultCategories = listOf(
            stringResource(R.string.expense_default_food),
            stringResource(R.string.expense_default_toys),
            stringResource(R.string.expense_default_health),
            stringResource(R.string.expense_default_care),
            stringResource(R.string.expense_default_other)
        )
        val categoryOptions = (defaultCategories + allCategories).distinct().sorted()
        SelectionDialog(
            title = stringResource(R.string.expense_select_category),
            options = categoryOptions + stringResource(R.string.expense_add_new_category),
            onDismiss = { showCategoryDialog = false },
            onSelect = { index ->
                if (index >= categoryOptions.size) {
                    showNewCategoryDialog = true
                } else {
                    category = categoryOptions[index]
                }
                showCategoryDialog = false
            }
        )
    }
    if (showAmountDialog) {
        var tempAmount by remember { mutableStateOf(amount) }
        AlertDialog(
            onDismissRequest = { showAmountDialog = false },
            containerColor = Color.White,
            titleContentColor = Color.Black,
            textContentColor = Color.Black,
            title = { Text(stringResource(R.string.expense_enter_amount_title)) },
            text = {
                TextField(
                    value = tempAmount,
                    onValueChange = { tempAmount = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text(stringResource(R.string.expense_amount_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.DarkGray,
                        cursorColor = Color.Blue
                    )
                )
            },
            confirmButton = {
                Button(onClick = {
                    amount = tempAmount
                    showAmountDialog = false
                }) { Text("ОК") }
            }
        )
    }

    AppBackground(appTheme = currentAppTheme) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(if (expenseId == null) R.string.expense_add_title else R.string.expense_edit_title)) },
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
                PetDetailRow(
                    icon = painterResource(R.drawable.ic_pet_name_24),
                    label = stringResource(R.string.expense_label_expense),
                    value = allPets.find { it.id == selectedPetId }?.name
                        ?: stringResource(R.string.expense_total),
                    onClick = { showPetDialog = true }
                )
                PetDetailRow(
                    icon = painterResource(R.drawable.ic_money_24),
                    label = stringResource(R.string.expense_amount),
                    value = if (amount.isBlank()) " " else "$amount ${settingsManager.getCurrency()}",
                    onClick = { showAmountDialog = true }
                )
                PetDetailRow(
                    icon = painterResource(R.drawable.ic_category_24),
                    label = stringResource(R.string.expense_category),
                    value = category.ifBlank { " " },
                    onClick = { showCategoryDialog = true }
                )
                PetDetailRow(
                    icon = painterResource(R.drawable.ic_calendar_event_24),
                    label = stringResource(R.string.expense_label_date),
                    value = date?.let {
                        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(
                            it
                        )
                    } ?: stringResource(R.string.expense_date_not_selected),
                    onClick = { datePickerDialog.show() }
                )

                TextFieldRow(
                    value = description,
                    onValueChange = { description = it },
                    label = stringResource(R.string.expense_description_optional),
                    icon = painterResource(R.drawable.ic_note_24),
                    modifier = Modifier.height(100.dp),
                )

            Spacer(modifier = Modifier.weight(1f)) // Пружинка, чтобы прижать кнопку к низу

            Button(
                onClick = {
                    val finalAmount = amount.toDoubleOrNull()

                    if (finalAmount == null || finalAmount <= 0) {
                        Toast.makeText(context, context.getString(R.string.validation_enter_valid_amount), Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (category.isBlank()) {
                        Toast.makeText(context, context.getString(R.string.validation_select_category), Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val expenseToSave = Expense(
                        id = expenseId ?: 0,
                        petId = selectedPetId ?: -1,
                        date = date!!,
                        amount = finalAmount,
                        category = category,
                        description = description.ifBlank { null }
                    )
                    if (expenseId == null) {
                        viewModel.addExpense(expenseToSave)
                    } else {
                        viewModel.updateExpense(expenseToSave)
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
