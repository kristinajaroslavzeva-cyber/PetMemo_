package com.name.petmemo.com.name.petmemo.ui.screens

import AppBackground
import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.name.petmemo.ui.viewmodel.PetViewModel
import com.name.petmemo.data.model.Expense
import com.name.petmemo.data.model.Pet
import com.name.petmemo.ui.ThemeViewModel
import com.name.petmemo.ui.ThemeViewModelFactory
import com.name.petmemo.ui.theme.OriginalPetPurple
import com.name.petmemo.ui.theme.OriginalTextDark
import java.text.SimpleDateFormat
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box // Это from Foundation, а не Material3
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.name.petmemo.R
import java.util.*
import kotlin.collections.filter
import kotlin.collections.map
const val ALL_EXPENSES_ID = -1
const val GENERAL_EXPENSES_ID = 0
data class DateRange(val startDateMillis: Long, val endDateMillis: Long)

data class LocalizedDateFilter(
    val filter: DateFilter,
    val displayName: String
)
enum class DateFilter {
    TODAY,
    THIS_MONTH,
    LAST_MONTH,
    THIS_YEAR,
    ALL_TIME,
    CUSTOM
}

@Composable
fun getDateFilterOptions(): List<LocalizedDateFilter> {
    return listOf(
        LocalizedDateFilter(DateFilter.TODAY, stringResource(R.string.date_filter_today)),
        LocalizedDateFilter(DateFilter.THIS_MONTH, stringResource(R.string.date_filter_this_month)),
        LocalizedDateFilter(DateFilter.LAST_MONTH, stringResource(R.string.date_filter_last_month)),
        LocalizedDateFilter(DateFilter.THIS_YEAR, stringResource(R.string.date_filter_this_year)),
        LocalizedDateFilter(DateFilter.ALL_TIME, stringResource(R.string.date_filter_all_time)),
        LocalizedDateFilter(DateFilter.CUSTOM, stringResource(R.string.date_filter_custom))
    )
}

data class PieChartEntry(val value: Float, val color: Color, val label: String)

@Composable
fun ChartRenderer(
    expenses: List<Expense>,
    pets: List<Pet>,
    selectedPetFilterId: Int,
    selectedCategory: String?
) {
    val generalExpenseLabel = stringResource(R.string.expense_general)
    val petsById = pets.associateBy { it.id }
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        Color(0xFF6200EE), Color(0xFF03DAC5), Color(0xFFFFC107)
    )

    val chartEntries = remember(expenses, selectedPetFilterId, selectedCategory) {
        val shouldGroupByPet = (selectedPetFilterId == ALL_EXPENSES_ID && selectedCategory == null)

        if (shouldGroupByPet) {
            expenses.groupBy { it.petId }
                .mapValues { (_, exp) -> exp.sumOf { it.amount }.toFloat() }
                .entries
                .mapIndexedNotNull { index, entry ->
                    if (entry.value > 0) PieChartEntry(
                        value = entry.value,
                        color = colors[index % colors.size],
                        label = petsById[entry.key]?.name ?: generalExpenseLabel
                    ) else null
                }
        } else {
            expenses.groupBy { it.category }
                .mapValues { (_, exp) -> exp.sumOf { it.amount }.toFloat() }
                .entries
                .mapIndexedNotNull { index, entry ->
                    if (entry.value > 0) PieChartEntry(
                        value = entry.value,
                        color = colors[index % colors.size],
                        label = entry.key
                    ) else null
                }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
    ) {
        if (chartEntries.isNotEmpty()) {
            SimplePieChart(entries = chartEntries)
        } else {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.data_not_available), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onBack: () -> Unit,
    onNavigateToEditExpense: (Int, Int) -> Unit,
    onNavigateToAddExpense: () -> Unit,
    viewModel: PetViewModel
) {
    val application = LocalContext.current.applicationContext as Application
    val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(application))
    val currentAppTheme by themeViewModel.theme.collectAsState()
    val allPets by viewModel.allPets.collectAsState(initial = emptyList())
    val allExpenses by viewModel.allExpenses.collectAsState(initial = emptyList())

    var selectedPetFilterId by remember { mutableStateOf(ALL_EXPENSES_ID) }
    var selectedDateFilter by remember { mutableStateOf(DateFilter.ALL_TIME) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var isChartExpanded by remember { mutableStateOf(true) }
    var showDatePicker by remember { mutableStateOf(false) }
    var customDateRange by remember { mutableStateOf<DateRange?>(null) }

    var showDeleteExpenseDialog by rememberSaveable { mutableStateOf(false) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    val petsById = remember(allPets) { allPets.associateBy { it.id } }
    val expenseCategories = remember(allExpenses) {
        allExpenses.map { it.category }.distinct().sorted()
    }

    val filteredExpenses = remember(allExpenses, selectedPetFilterId, selectedDateFilter, selectedCategory, customDateRange) {
        val now = Calendar.getInstance()
        val currentPetIds = petsById.keys

        allExpenses.filter { expense ->
            val matchesPet = when (selectedPetFilterId) {
                ALL_EXPENSES_ID -> true
                else -> expense.petId == selectedPetFilterId
            }
            val matchesCategory = selectedCategory == null || expense.category == selectedCategory
            val expenseDate = Calendar.getInstance().apply { time = expense.date }
            val matchesDate = when (selectedDateFilter) {
                DateFilter.CUSTOM -> {
                    customDateRange?.let {
                        val expenseTime = expense.date.time
                        val endCalendar = Calendar.getInstance().apply { timeInMillis = it.endDateMillis }
                        endCalendar.set(Calendar.HOUR_OF_DAY, 23)
                        endCalendar.set(Calendar.MINUTE, 59)
                        endCalendar.set(Calendar.SECOND, 59)
                        expenseTime >= it.startDateMillis && expenseTime <= endCalendar.timeInMillis
                    } ?: false
                }
                DateFilter.TODAY -> now.get(Calendar.DAY_OF_YEAR) == expenseDate.get(Calendar.DAY_OF_YEAR) && now.get(Calendar.YEAR) == expenseDate.get(Calendar.YEAR)
                DateFilter.THIS_MONTH -> now.get(Calendar.MONTH) == expenseDate.get(Calendar.MONTH) && now.get(Calendar.YEAR) == expenseDate.get(Calendar.YEAR)
                DateFilter.LAST_MONTH -> {
                    val lastMonth = (now.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
                    lastMonth.get(Calendar.MONTH) == expenseDate.get(Calendar.MONTH) && lastMonth.get(Calendar.YEAR) == expenseDate.get(Calendar.YEAR)
                }
                DateFilter.THIS_YEAR -> now.get(Calendar.YEAR) == expenseDate.get(Calendar.YEAR)
                DateFilter.ALL_TIME -> true
            }
            matchesPet && matchesDate && matchesCategory
        }
    }
    val totalAmount = remember(filteredExpenses) {
        filteredExpenses.sumOf { it.amount }
    }

    if (showDatePicker) {
        val datePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = customDateRange?.startDateMillis,
            initialSelectedEndDateMillis = customDateRange?.endDateMillis
        )
        DatePickerDialog(
            colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        customDateRange = datePickerState.selectedStartDateMillis?.let { start ->
                            datePickerState.selectedEndDateMillis?.let { end ->
                                DateRange(start, end)
                            }
                        }
                    },
                    enabled = datePickerState.selectedEndDateMillis != null
                ) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.cancel_button)) } }
        ) {
            DateRangePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = stringResource(R.string.expense_filter_date_range),
                        modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp)
                    )
                },
                headline = {
                    val formatter = remember { SimpleDateFormat("d MMM", Locale.getDefault()) }
                    val startDate = datePickerState.selectedStartDateMillis?.let { formatter.format(Date(it)) } ?: stringResource(R.string.date_range_start)
                    val endDate = datePickerState.selectedEndDateMillis?.let { formatter.format(Date(it)) } ?: stringResource(R.string.date_range_end)

                    Row(
                        modifier = Modifier.padding(start = 24.dp, end = 12.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = startDate,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("–")
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = endDate,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }

            )
        }
    }

    AppBackground(appTheme = currentAppTheme) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.expense_reports_title)) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues))
            {

                FilterControls(
                    allPets = allPets,
                    expenseCategories = expenseCategories,
                    selectedPetId = selectedPetFilterId,
                    onPetSelected = { selectedPetFilterId = it },
                    selectedDateFilter = selectedDateFilter,
                    onDateFilterSelected = {
                        selectedDateFilter = it
                        if (it == DateFilter.CUSTOM) {
                            showDatePicker = true
                        }
                    },
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    customDateRange = customDateRange
                )

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isChartExpanded = !isChartExpanded }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.expense_structure), style = MaterialTheme.typography.titleLarge, color = Color.White, modifier = Modifier.weight(1f))
                        // ✅ ЗАМЕНА 21: Свернуть/развернуть
                        Icon(imageVector = if (isChartExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, stringResource(R.string.checklists_expand_desc), tint = Color.White)
                    }
                    AnimatedVisibility(visible = isChartExpanded) {
                        Column {
                            ChartRenderer(
                                expenses = filteredExpenses,
                                pets = allPets,
                                selectedPetFilterId = selectedPetFilterId,
                                selectedCategory = selectedCategory
                            )
                            if (filteredExpenses.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(R.string.expense_total),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        text = String.format(Locale.getDefault(), "%,.2f", totalAmount),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredExpenses, key = { it.id }) { expense ->
                        ReportExpenseCard(
                            expense = expense,
                            pet = petsById[expense.petId],
                            onEditClick = { onNavigateToEditExpense(expense.petId, expense.id) },
                            onLongPress = {
                                expenseToDelete = it
                                showDeleteExpenseDialog = true
                            }

                        )
                    }
                }
            }
        }
    }

    if (showDeleteExpenseDialog && expenseToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteExpenseDialog = false
                expenseToDelete = null // Сбрасываем расход при отмене
            },
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = Color.Black,
            textContentColor = Color.Black,
            title = { Text(stringResource(R.string.expense_delete_confirm)) },
            text = { Text(stringResource(R.string.expense_delete_text)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteExpense(expenseToDelete!!) // Вызываем метод ViewModel для удаления
                        showDeleteExpenseDialog = false
                        expenseToDelete = null // Сбрасываем расход после удаления
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text(stringResource(R.string.delete_button)) }
            },
            dismissButton = {
                Button(onClick = {
                    showDeleteExpenseDialog = false
                    expenseToDelete = null // Сбрасываем расход при отмене
                }) { Text(stringResource(R.string.cancel_button)) }
            }
        )
    }
}

@Composable
fun SimplePieChart(
    modifier: Modifier = Modifier,
    entries: List<PieChartEntry>
) {
    val totalSum = entries.sumOf { it.value.toDouble() }.toFloat()

    Row(
        modifier = modifier.padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                entries.forEach { entry ->
                    val sweepAngle = (entry.value / totalSum) * 360f
                    drawArc(
                        color = entry.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 50f, cap = StrokeCap.Butt)
                    )
                    startAngle += sweepAngle
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            entries.forEach { entry ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Box(modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(entry.color))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${entry.label} (${String.format("%.0f", entry.value)})",
                        fontSize = 14.sp,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryBarChart(expenses: List<Expense>) {
    val dataByCategory = expenses
        .groupBy { it.category }
        .mapValues { (_, value) -> value.sumOf { it.amount }.toFloat() }
        .entries.toList()

    val colors = listOf(
        Color(0xFF6200EE), Color(0xFF03DAC5), Color(0xFFFFC107),
        Color(0xFF2196F3), Color(0xFFE91E63), Color(0xFF4CAF50)
    )

    val chartEntries = dataByCategory.mapIndexed { index, entry ->
        PieChartEntry(
            value = entry.value,
            color = colors[index % colors.size],
            label = entry.key
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        if (chartEntries.isNotEmpty()) {
            SimplePieChart(entries = chartEntries)
        } else {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.data_not_available), color = Color.White.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun PetComparisonBarChart(expenses: List<Expense>, pets: List<Pet>) {
    val expensesByPet = expenses.groupBy { it.petId }
    val petsById = pets.associateBy { it.id }

    val colors = listOf(
        Color(0xFF6200EE), Color(0xFF03DAC5), Color(0xFFFFC107),
        Color(0xFF2196F3), Color(0xFFE91E63), Color(0xFF4CAF50)
    )

    val chartEntries = expensesByPet.entries.mapIndexed { index, entry ->
        PieChartEntry(
            value = entry.value.sumOf { it.amount }.toFloat(),
            color = colors[index % colors.size],
            label = petsById[entry.key]?.name ?: stringResource(R.string.expense_general)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        if (chartEntries.isNotEmpty()) {
            SimplePieChart(entries = chartEntries)
        } else {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.data_not_available), color = Color.White.copy(alpha = 0.7f))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReportExpenseCard(
    expense: Expense,
    pet: Pet?,
    onEditClick: (Expense) -> Unit,
    onLongPress: (Expense) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onEditClick(expense) },
                onLongClick = { onLongPress(expense) }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = OriginalPetPurple)
    ) {
        Row(

            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(OriginalPetPurple.copy(alpha = 0.3f)),
            ) {if (pet?.photoUri != null && pet.photoUri.isNotEmpty()) {
                val density = LocalDensity.current
                val targetSizeDp = 40.dp
                with(density) { targetSizeDp.roundToPx() }

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(pet.photoUri)
                        .crossfade(true)
                        .size(with(density) { 120.dp.roundToPx() })
                        .build(),
                    contentDescription = pet.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Расход", tint = Color.White, modifier = Modifier.size(24.dp)) // Цвет иконки остается белым
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = expense.category,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = pet?.name ?: stringResource(R.string.expense_general),
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    String.format(Locale.getDefault(), "%,.2f", expense.amount),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    dateFormatter.format(expense.date),
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionDialog(title: String, options: List<String>, onDismiss: () -> Unit, onSelect: (Int) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        textContentColor = MaterialTheme.colorScheme.onBackground,

        title = {
            Text(
                title,
                color = OriginalTextDark
            )
        },
        text = {
            LazyColumn {
                items(options.size) { index ->
                    Text(
                        text = options[index],
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(index) }
                            .padding(vertical = 12.dp),
                        color = Color.Black
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_button),
                    color = Color.Black
                )
            }
        }
    )
}

@Composable
fun FilterControls(
    allPets: List<Pet>,
    expenseCategories: List<String>,
    selectedPetId: Int,
    onPetSelected: (Int) -> Unit,
    selectedDateFilter: DateFilter,
    onDateFilterSelected: (DateFilter) -> Unit,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    customDateRange: DateRange?
) {
    var petMenuExpanded by remember { mutableStateOf(false) }
    var dateMenuExpanded by remember { mutableStateOf(false) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    val selectedPetName = when (selectedPetId) {
        ALL_EXPENSES_ID -> stringResource(R.string.expense_all)
        GENERAL_EXPENSES_ID -> stringResource(R.string.expense_general)
        else -> allPets.find { it.id == selectedPetId }?.name ?: stringResource(R.string.expense_unknown_pet)
    }
    val dateFilterText = if (selectedDateFilter == DateFilter.CUSTOM && customDateRange != null) {
        val formatter = remember { SimpleDateFormat("dd.MM.yy", Locale.getDefault()) }
        "${formatter.format(Date(customDateRange.startDateMillis))} - ${formatter.format(Date(customDateRange.endDateMillis))}"
    } else {
        when (selectedDateFilter) {
            DateFilter.TODAY -> stringResource(R.string.date_filter_today)
            DateFilter.THIS_MONTH -> stringResource(R.string.date_filter_this_month)
            DateFilter.LAST_MONTH -> stringResource(R.string.date_filter_last_month)
            DateFilter.THIS_YEAR -> stringResource(R.string.date_filter_this_year)
            DateFilter.ALL_TIME -> stringResource(R.string.date_filter_all_time)
            DateFilter.CUSTOM -> stringResource(R.string.date_filter_custom)
        }
    }


    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(onClick = { petMenuExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(selectedPetName, color = Color.White, overflow = TextOverflow.Ellipsis, maxLines = 1)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                }
                DropdownMenu(
                    expanded = petMenuExpanded,
                    onDismissRequest = { petMenuExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    DropdownMenuItem(text = { Text(stringResource(R.string.expense_all), color = Color.Black) }, onClick = { onPetSelected(ALL_EXPENSES_ID); petMenuExpanded = false })
                    DropdownMenuItem(text = { Text(stringResource(R.string.expense_general), color = Color.Black) }, onClick = { onPetSelected(GENERAL_EXPENSES_ID); petMenuExpanded = false })
                    allPets.forEach { pet ->
                        DropdownMenuItem(text = { Text(pet.name, color = Color.Black) }, onClick = { onPetSelected(pet.id); petMenuExpanded = false })
                    }
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(onClick = { dateMenuExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(dateFilterText, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                }
                DropdownMenu(
                    expanded = dateMenuExpanded,
                    onDismissRequest = { dateMenuExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    DateFilter.values().forEach { filter ->
                        val filterText = when (filter) {
                            DateFilter.TODAY -> stringResource(R.string.date_filter_today)
                            DateFilter.THIS_MONTH -> stringResource(R.string.date_filter_this_month)
                            DateFilter.LAST_MONTH -> stringResource(R.string.date_filter_last_month)
                            DateFilter.THIS_YEAR -> stringResource(R.string.date_filter_this_year)
                            DateFilter.ALL_TIME -> stringResource(R.string.date_filter_all_time)
                            DateFilter.CUSTOM -> stringResource(R.string.date_filter_custom)
                        }
                        DropdownMenuItem(
                            text = { Text(filterText, color = Color.Black) },
                            onClick = { onDateFilterSelected(filter); dateMenuExpanded = false }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { categoryMenuExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selectedCategory ?: stringResource(R.string.expense_filter_all_cats), color = Color.White, overflow = TextOverflow.Ellipsis, maxLines = 1)
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
            }
            DropdownMenu(
                expanded = categoryMenuExpanded,
                onDismissRequest = { categoryMenuExpanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                DropdownMenuItem(text = { Text(stringResource(R.string.expense_filter_all_cats), color = Color.Black) }, onClick = { onCategorySelected(null); categoryMenuExpanded = false })
                expenseCategories.forEach { category ->
                    DropdownMenuItem(text = { Text(category, color = Color.Black) }, onClick = { onCategorySelected(category); categoryMenuExpanded = false })
                }
            }
        }
    }
}


