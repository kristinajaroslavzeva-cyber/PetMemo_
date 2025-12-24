


import android.app.Application
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.name.petmemo.ui.components.PetDetailRow
import com.name.petmemo.ui.viewmodel.PetViewModel
import com.name.petmemo.data.model.Pet
import com.name.petmemo.ui.ThemeViewModel
import com.name.petmemo.ui.ThemeViewModelFactory
import com.name.petmemo.com.name.petmemo.ui.screens.SelectionDialog
import com.name.petmemo.ui.theme.OriginalAccentGreen
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.*
import com.name.petmemo.R
import kotlin.math.pow
import com.name.petmemo.ui.components.TextFieldRow
import kotlin.collections.map
@Composable
fun CalculatorMenuScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            CalculatorMenuItem(
                title = stringResource(R.string.menu_food_adult_title),
                description = stringResource(R.string.menu_food_adult_desc),
                icon = painterResource(id = R.drawable.ic_food),
                onClick = { navController.navigate(CalculatorRoutes.FOOD_PORTION_ADULT_STERILIZED) }
            )
        }
        item {
            CalculatorMenuItem(
                title = stringResource(R.string.menu_food_puppy_title),
                description = stringResource(R.string.menu_food_puppy_desc),
                icon = painterResource(id = R.drawable.ic_food),
                onClick = { navController.navigate(CalculatorRoutes.FOOD_PORTION_PUPPY_KITTEN) }
            )
        }
        item {
            CalculatorMenuItem(
                title = stringResource(R.string.menu_food_active_title),
                description = stringResource(R.string.menu_food_active_desc),
                icon = painterResource(id = R.drawable.ic_food),
                onClick = { navController.navigate(CalculatorRoutes.FOOD_PORTION_ACTIVE_UNSTERILIZED) }
            )
        }

        item {
            CalculatorMenuItem(
                title = stringResource(R.string.menu_age_cat_title),
                description = stringResource(R.string.menu_age_cat_desc),
                icon = painterResource(id = R.drawable.ic_age),
                onClick = { navController.navigate(CalculatorRoutes.HUMAN_AGE_CAT) }
            )
        }
        item {
            CalculatorMenuItem(
                title = stringResource(R.string.menu_age_dog_small_title),
                description = stringResource(R.string.menu_age_dog_small_desc),
                icon = painterResource(id = R.drawable.ic_age),
                onClick = { navController.navigate(CalculatorRoutes.HUMAN_AGE_DOG_SMALL) }
            )
        }
        item {
            CalculatorMenuItem(
                title = stringResource(R.string.menu_age_dog_large_title),
                description = stringResource(R.string.menu_age_dog_large_desc),
                icon = painterResource(id = R.drawable.ic_age),
                onClick = { navController.navigate(CalculatorRoutes.HUMAN_AGE_DOG_LARGE) }
            )
        }

        item {
            CalculatorMenuItem(
                title = stringResource(R.string.menu_water_title),
                description = stringResource(R.string.menu_water_desc),
                icon = painterResource(id = R.drawable.ic_water),
                onClick = { navController.navigate(CalculatorRoutes.WATER_INTAKE) }
            )
        }
        item {
            CalculatorMenuItem(
                title = stringResource(R.string.menu_supply_title),
                description = stringResource(R.string.menu_supply_desc),
                icon = painterResource(id = R.drawable.ic_box),
                onClick = { navController.navigate(CalculatorRoutes.FOOD_SUPPLY) }
            )
        }
        item {
            CalculatorMenuItem(
                title = stringResource(R.string.menu_gestation_title),
                description = stringResource(R.string.menu_gestation_desc),
                icon = painterResource(id = R.drawable.ic_family_date_24),
                onClick = { navController.navigate(CalculatorRoutes.GESTATION) }
            )
        }
    }
}

@Composable
fun CalculatorMenuItem(title: String, description: String, icon: Painter, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = icon, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CalculatorPage(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content
    )
}

@Composable
fun CalculatorDescription(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun FoodPortionCalculatorBase(
    factor: Double,
    descriptionText: String,
    buttonColor: Color
) {
    var weight by remember { mutableStateOf("") }
    var caloriesPer100g by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    val context = LocalContext.current

    CalculatorPage {
        CalculatorDescription(text = descriptionText)
        TextFieldRow(
            value = weight,
            onValueChange = { weight = it.replace(",", ".") },
            label = stringResource(R.string.calc_label_weight),
            icon = painterResource(id = R.drawable.ic_weight),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        TextFieldRow(
            value = caloriesPer100g,
            onValueChange = { caloriesPer100g = it.replace(",", ".") },
            label = stringResource(R.string.calc_label_calories),
            icon = painterResource(id = R.drawable.ic_food),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Button(
            onClick = {
                val weightValue = weight.toDoubleOrNull()
                val caloriesValue = caloriesPer100g.toDoubleOrNull()
                if (weightValue != null && caloriesValue != null && weightValue > 0 && caloriesValue > 0) {
                    val rer = 70 * (weightValue.pow(0.75))
                    val dailyCalories = rer * factor
                    val dailyGrams = (dailyCalories / caloriesValue) * 100
                    result = context.getString(R.string.calc_result_daily_portion, dailyGrams.toInt())
                } else {
                    result = context.getString(R.string.calc_result_error)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor) // Используем переданный цвет
        ) { Text(stringResource(R.string.calc_button_calculate), color = MaterialTheme.colorScheme.onSecondary) }

        if (result.isNotBlank()) { ResultCard(text = result) }
    }
}

@Composable
fun FoodPortionAdultSterilizedCalculatorScreen() {
    FoodPortionCalculatorBase(
        factor = 1.6,
        descriptionText = stringResource(R.string.calc_desc_adult_food),
        buttonColor = OriginalAccentGreen
    )
}

@Composable
fun FoodPortionPuppyKittenCalculatorScreen() {
    FoodPortionCalculatorBase(
        factor = 2.5,
        descriptionText = stringResource(R.string.calc_desc_puppy_food),
        buttonColor = OriginalAccentGreen
    )
}

@Composable
fun FoodPortionActiveUnsterilizedCalculatorScreen() {
    FoodPortionCalculatorBase(
        factor = 1.8,
        descriptionText = stringResource(R.string.calc_desc_active_food),
        buttonColor = OriginalAccentGreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HumanAgeCalculatorBase(
    descriptionText: String,
    ageCalculation: (Period, Pet) -> Int,
    viewModel: PetViewModel
) {
    val allPets by viewModel.allPets.collectAsState(initial = emptyList())
    var selectedPet by remember { mutableStateOf<Pet?>(null) }
    var showPetDialog by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(selectedPet) {
        selectedPet?.birthDate?.let { birthDate ->
            val today = LocalDate.now()
            val birthday = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val period = Period.between(birthday, today)
            val humanYears = ageCalculation(period, selectedPet!!)
            result = context.getString(R.string.calc_result_human_age, humanYears)
        }
    }

    if (showPetDialog) {
        SelectionDialog(
            title = stringResource(R.string.calc_dialog_select_pet),
            options = allPets.map { it.name },
            onDismiss = { showPetDialog = false },
            onSelect = { index ->
                selectedPet = allPets[index]
                showPetDialog = false
            }
        )
    }

    CalculatorPage {
        CalculatorDescription(text = descriptionText)
        PetDetailRow(
            icon = painterResource(id = R.drawable.ic_pet_name_24),
            label = stringResource(R.string.calc_label_pet_name),
            value = selectedPet?.name ?: " ",
            onClick = { showPetDialog = true }
        )
        if (result.isNotBlank()) { ResultCard(text = result) }
    }
}

@Composable
fun HumanAgeCatCalculatorScreen(viewModel: PetViewModel = viewModel()) {
    HumanAgeCalculatorBase(
        descriptionText = stringResource(R.string.calc_desc_age_cat),
        ageCalculation = { period, _ ->
            val years = period.years
            when {
                years == 0 -> {
                    val months = period.months
                    when {
                        months <= 2 -> 3
                        months <= 6 -> 10
                        months <= 12 -> 15
                        else -> 15
                    }
                }
                years == 1 -> 15
                years == 2 -> 24
                else -> 24 + (years - 2) * 4
            }
        },
        viewModel = viewModel
    )
}

@Composable
fun HumanAgeDogSmallCalculatorScreen(viewModel: PetViewModel = viewModel()) {
    HumanAgeCalculatorBase(
        descriptionText = stringResource(R.string.calc_desc_age_dog_small),
        ageCalculation = { period, _ ->
            val years = period.years
            when {
                years == 0 -> {
                    val months = period.months
                    when {
                        months <= 3 -> 5
                        months <= 6 -> 10
                        months <= 12 -> 15
                        else -> 15
                    }
                }
                years == 1 -> 15
                years == 2 -> 24
                else -> 24 + (years - 2) * 4
            }
        },
        viewModel = viewModel
    )
}

@Composable
fun HumanAgeDogLargeCalculatorScreen(viewModel: PetViewModel = viewModel()) {
    HumanAgeCalculatorBase(
        descriptionText = stringResource(R.string.calc_desc_age_dog_large),
        ageCalculation = { period, _ ->
            val years = period.years
            when {
                years == 0 -> {
                    val months = period.months
                    when {
                        months <= 3 -> 3
                        months <= 6 -> 7
                        months <= 12 -> 12
                        else -> 12
                    }
                }
                years == 1 -> 12
                years == 2 -> 20
                else -> 20 + (years - 2) * 6
            }
        },
        viewModel = viewModel
    )
}

@Composable
fun WaterIntakeCalculatorScreen() {
    val context = LocalContext.current
    var weight by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    CalculatorPage {
        CalculatorDescription(text = stringResource(R.string.calc_desc_water))
        TextFieldRow(
            value = weight,
            onValueChange = { weight = it.replace(",", ".") },
            label = stringResource(R.string.calc_label_weight),
            icon = painterResource(id = R.drawable.ic_weight),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Button(
            onClick = {
                val weightValue = weight.toDoubleOrNull()
                if (weightValue != null && weightValue > 0) {
                    val waterMl = weightValue * 60
                    result = context.getString(R.string.calc_result_water_intake, waterMl.toInt())
                } else {
                    result = context.getString(R.string.calc_result_error)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OriginalAccentGreen)
        ) { Text(stringResource(R.string.calc_button_calculate)) }

        if (result.isNotBlank()) { ResultCard(text = result) }
    }
}

@Composable
fun FoodSupplyCalculatorScreen() {
    var packWeight by remember { mutableStateOf("") }
    var dailyPortion by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    val context = LocalContext.current

    CalculatorPage {
        TextFieldRow(
            value = packWeight,
            onValueChange = { packWeight = it.replace(",", ".") },
            label = stringResource(R.string.calc_label_pack_weight),
            icon = painterResource(id = R.drawable.ic_box),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        TextFieldRow(
            value = dailyPortion,
            onValueChange = { dailyPortion = it.replace(",",".") },
            label = stringResource(R.string.calc_label_daily_portion),
            icon = painterResource(id = R.drawable.ic_food),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Button(
            onClick = {
                val packWeightKg = packWeight.toDoubleOrNull()
                val dailyPortionG = dailyPortion.toDoubleOrNull()
                if (packWeightKg != null && dailyPortionG != null && packWeightKg > 0 && dailyPortionG > 0) {
                    val days = (packWeightKg * 1000) / dailyPortionG
                    result = context.getString(R.string.calc_result_food_supply, days.toInt())
                } else {
                    result = context.getString(R.string.calc_result_error)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OriginalAccentGreen)
        ) { Text(stringResource(R.string.calc_button_calculate)) }

        if (result.isNotBlank()) { ResultCard(text = result) }
    }
}

@Composable
fun GestationCalculatorScreen() {
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }
    var matingDate by remember { mutableStateOf<Date?>(null) }
    var result by remember { mutableStateOf("") }

    val datePickerDialog = DatePickerDialog(context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            val calendar = Calendar.getInstance().apply { set(year, month, day) }
            matingDate = calendar.time
            calendar.add(Calendar.DAY_OF_YEAR, 63)
            result = context.getString(R.string.calc_result_gestation_date, dateFormatter.format(calendar.time))
        },
        Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    CalculatorPage {
        CalculatorDescription(text = stringResource(R.string.calc_desc_gestation))
        PetDetailRow(
            icon = painterResource(id = R.drawable.ic_calendar_event_24),
            label = stringResource(R.string.calc_label_mating_date),
            value = matingDate?.let { dateFormatter.format(it) } ?: " ",
            onClick = { datePickerDialog.show() }
        )
        if (result.isNotBlank()) { ResultCard(text = result) }
    }
}

@Composable
fun ResultCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(0.9f),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

object CalculatorRoutes {
    const val MENU = "menu"

    // Калькуляторы корма
    const val FOOD_PORTION_ADULT_STERILIZED = "food_portion_adult_sterilized" // Для взрослых, стерилизованных
    const val FOOD_PORTION_PUPPY_KITTEN = "food_portion_puppy_kitten"       // Для щенков/котят
    const val FOOD_PORTION_ACTIVE_UNSTERILIZED = "food_portion_active_unsterilized" // Для активных/нестерилизованных

    // Калькуляторы возраста
    const val HUMAN_AGE_CAT = "human_age_cat"           // Для кошек
    const val HUMAN_AGE_DOG_SMALL = "human_age_dog_small" // Для мелких собак
    const val HUMAN_AGE_DOG_LARGE = "human_age_dog_large" // Для крупных собак

    // Другие (без изменений в маршрутах пока)
    const val WATER_INTAKE = "water_intake"
    const val FOOD_SUPPLY = "food_supply"
    const val GESTATION = "gestation"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(onBack: () -> Unit)  {
    val application = LocalContext.current.applicationContext as Application
    val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(application))
    val currentAppTheme by themeViewModel.theme.collectAsState()
    val calculatorNavController = rememberNavController()
    val navBackStackEntry by calculatorNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val title = when (currentRoute) {
        CalculatorRoutes.FOOD_PORTION_ADULT_STERILIZED -> stringResource(R.string.calculator_title_food_adult)
        CalculatorRoutes.FOOD_PORTION_PUPPY_KITTEN -> stringResource(R.string.calculator_title_food_puppy)
        CalculatorRoutes.FOOD_PORTION_ACTIVE_UNSTERILIZED -> stringResource(R.string.calculator_title_food_active)
        CalculatorRoutes.HUMAN_AGE_CAT -> stringResource(R.string.calculator_title_age_cat)
        CalculatorRoutes.HUMAN_AGE_DOG_SMALL -> stringResource(R.string.calculator_title_age_dog_small)
        CalculatorRoutes.HUMAN_AGE_DOG_LARGE -> stringResource(R.string.calculator_title_age_dog_large)
        CalculatorRoutes.WATER_INTAKE -> stringResource(R.string.calculator_title_water)
        CalculatorRoutes.FOOD_SUPPLY -> stringResource(R.string.calculator_title_supply)
        CalculatorRoutes.GESTATION -> stringResource(R.string.calculator_title_gestation)
        else -> stringResource(R.string.calculator_title_main) // "Калькуляторы"
    }

    AppBackground(appTheme = currentAppTheme) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (calculatorNavController.previousBackStackEntry != null) calculatorNavController.popBackStack() else onBack()
                        }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
    ) { paddingValues ->
            NavHost(
                navController = calculatorNavController,
                startDestination = CalculatorRoutes.MENU,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(CalculatorRoutes.MENU) { CalculatorMenuScreen(navController = calculatorNavController) }
                composable(CalculatorRoutes.FOOD_PORTION_ADULT_STERILIZED) { FoodPortionAdultSterilizedCalculatorScreen() }
                composable(CalculatorRoutes.FOOD_PORTION_PUPPY_KITTEN) { FoodPortionPuppyKittenCalculatorScreen() }
                composable(CalculatorRoutes.FOOD_PORTION_ACTIVE_UNSTERILIZED) { FoodPortionActiveUnsterilizedCalculatorScreen() }
                composable(CalculatorRoutes.HUMAN_AGE_CAT) { HumanAgeCatCalculatorScreen() }
                composable(CalculatorRoutes.HUMAN_AGE_DOG_SMALL) { HumanAgeDogSmallCalculatorScreen() }
                composable(CalculatorRoutes.HUMAN_AGE_DOG_LARGE) { HumanAgeDogLargeCalculatorScreen() }
                composable(CalculatorRoutes.WATER_INTAKE) { WaterIntakeCalculatorScreen() }
                composable(CalculatorRoutes.FOOD_SUPPLY) { FoodSupplyCalculatorScreen() }
                composable(CalculatorRoutes.GESTATION) { GestationCalculatorScreen() }
            }
    }
}


}