package com.name.petmemo.ui.screens
import AppBackground
import EditChecklistScreen
import android.app.Application
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.name.petmemo.ui.viewmodel.PetViewModel
import com.name.petmemo.R
import com.name.petmemo.di.UserChecklist
import com.name.petmemo.di.UserChecklistItem
import com.name.petmemo.data.models.Checklist
import com.name.petmemo.ui.ThemeViewModel
import com.name.petmemo.ui.ThemeViewModelFactory
import java.util.Locale

fun getCurrentChecklistContent(): Map<String, List<Checklist>> {
    return if (Locale.getDefault().language == "ru") {
        ChecklistContent.checklists
    } else {
        ChecklistContentEn.checklists
    }
}

object ChecklistContent {
    val checklists: Map<String, List<Checklist>> = mapOf(
        "Кошки" to listOf(
            Checklist("Стоит ли заводить кошку?", listOf(
                "Долгосрочное планирование: Готовы к обязательствам на 15-20 лет?",
                "Финансы: Рассчитали ежемесячные расходы (корм, наполнитель, игрушки) и бюджет на ветеринара?",
                "Время: Готовы уделять кошке время на игры и общение?",
                "Путешествия: Есть у вас план, кто будет ухаживать за кошкой во время вашего отъезда?",
                "Чистота: Готовы к регулярной уборке шерсти и чистке лотка ежедневно?",
                "Аллергия: Проверили вы всех членов семьи на аллергию?",
                "Безопасность дома: Установлены сетки \"антикошка\" на окнах?",
                "Стерилизация: Согласны вы на стерилизацию/кастрацию кошки в возрасте 6-8 месяцев?"
            )),
            Checklist("Аптечка первой помощи для кошки", listOf(
                "Термометр (электронный, для ректального измерения)",
                "Перевязочные материалы: Бинт, ватные диски, стерильные салфетки",
                "Антисептик: Хлоргексидин или Мирамистин (НЕ спиртовой!)",
                "Энтеросорбенты (Энтеросгель, Смекта) (ТОЛЬКО по согласованию с врачом)",
                "Кровоостанавливающее средство (Крахмал, мука или спец. порошок)",
                "Шприцы (без игл) для выпаивания или дачи лекарств",
                "Когтерез",
                "Ветеринарный воротник",
                "Телефон круглосуточной ветклиники"
            )),
            Checklist("Сборы в дорогу", listOf(
                "Надежная переноска (жесткая, с хорошей вентиляцией)",
                "Впитывающая пеленка на дно переноски",
                "Питьевая вода и складная миска",
                "Порция корма",
                "Ветеринарный паспорт с прививками",
                "Любимая игрушка или одеяло для снижения стресса",
                "Поводок и шлейка",
                "Влажные салфетки и пакеты для мусора"
            )),
            Checklist("Ежемесячный контроль здоровья", listOf(
                "Вес: Проверен и записан",
                "Антипаразитарная обработка: Проведена по графику",
                "Когти: Проведена стрижка когтей",
                "Чистка: Проверены и очищены глаза и уши",
                "Обогащение: Проведена интерактивная игра (имитация охоты)",
                "Убежище: Есть ли у кошки место, где ее никто не беспокоит?"
            ))
        ),
        "Собаки" to listOf(
            Checklist("Стоит ли заводить собаку?", listOf(
                "Стиль жизни: Соответствует ли ваш уровень активности потребностям породы?",
                "Прогулки: Готовы к ежедневному выгулу в любую погоду?",
                "Дрессировка: Готовы тратить время и средства на базовый курс дрессировки?",
                "Пространство: Достаточно  места для комфортного проживания собаки?",
                "Социализация: Готовы знакомить щенка с людьми и животными?",
                "Одиночество: Сможет ли собака оставаться одна дома?",
                "Долгосрочное планирование: Согласны на 10-15 лет обязательств?",
                "Уход и груминг: Рассчитали расходы на уход/груминг?"
            )),
            Checklist("Аптечка первой помощи для собаки", listOf(
                "Перекись водорода 3% (для промывания ран)",
                "Хлоргексидин/Мирамистин (антисептик)",
                "Активированный уголь / Энтеросгель (до консультации с врачом!)",
                "Кровоостанавливающее средство (или крахмал/мука)",
                "Эластичный бинт и стерильные салфетки",
                "Термометр (ректальный)",
                "Пинцет / Крючок-выкручиватель для клещей",
                "Таблетки от укачивания",
                "Ветеринарный паспорт и телефоны круглосуточной клиники"
            )),
            Checklist("Сборы в дорогу (на машине)", listOf(
                "Ремни безопасности / Автогамак / Переноска",
                "Вода и складная миска",
                "Порция привычного корма",
                "Комплект амуниции: Ошейник, шлейка, поводок",
                "Пакеты для уборки за собакой, влажные салфетки",
                "Любимая игрушка или одеяло",
                "Средство защиты от клещей и блох"
            )),
            Checklist("Ежемесячный контроль здоровья", listOf(
                "Вакцинация: Проверена дата следующей прививки",
                "Зубы/Пасть: Осмотрена ротовая полость",
                "Лапы/Когти: Проведена стрижка когтей",
                "Обогащение: Использованы ли головоломки для умственной стимуляции?",
                "Температура: В жару обеспечен доступ к воде и тени"
            ))
        ),
        "Рыбки" to listOf(
            Checklist("Стоит ли заводить аквариум?", listOf(
                "Время: Готовы уделять 30-60 минут еженедельно на уход?",
                "Терпение: Готовы ждать 3-6 недель для запуска 'Азотного цикла'?",
                "Бюджет: Рассчитали стоимость оборудования и тестов?",
                "Размер: Можете обеспечить нужный объем аквариума?",
                "Стабильность: Можете обеспечить стабильную температуру?",
                "Знания: Изучили требования и совместимость рыб?",
                "Отпуск: Есть план, кто будет следить за аквариумом?"
            )),
            Checklist("Обслуживание аквариума (Еженедельно)", listOf(
                "Подмена: Проведена подмена воды (20–30%)?",
                "Тесты: Проверены параметры воды (NH3, NO2, NO3)?",
                "Чистка: Очищен грунт сифоном?",
                "Фильтр: Промыты губки фильтра в аквариумной воде?",
                "Осмотр: Осмотрены рыбки на предмет заболеваний?"
            )),
            Checklist("Аптечка для аквариума", listOf(
                "Набор капельных тестов (NH3, NO2, NO3)",
                "Кондиционер для воды (дехлоратор)",
                "Средство для снижения аммиака",
                "Запасная помпа/компрессор",
                "Отсадник/Карантинный аквариум",
                "Соль для аквариума",
                "Дополнительный термометр"
            ))
        ),
        "Мелкие млекопитающие" to listOf(
            Checklist("Диета (Кролики и Морские свинки)", listOf(
                "Сено: Доступно 24/7 и составляет 80-90% рациона",
                "Гранулы: Используются качественные пеллеты, без зерна и мюсли",
                "Запрет: Исключены сладости, хлеб, зерновые смеси",
                "Запрет: Исключены белокочанная капуста и бобовые",
                "Вода: Свежая, чистая вода всегда доступна",
                "Стачивание зубов: Есть безопасные деревянные игрушки или ветки"
            )),
            Checklist("Устройство клетки", listOf(
                "Клетка: Размер соответствует виду",
                "Пол: Пол сплошной (нет сетчатого пола)",
                "Наполнитель: Не используются кедровые или сосновые опилки",
                "Наполнитель: Используется бумага, осина, флис или кукуруза",
                "Температура: Клетка защищена от сквозняков и прямых лучей солнца",
                "Хомяки: Колесо имеет правильный диаметр (20+ см)"
            )),
            Checklist("Аптечка", listOf(
                "Ратолог: Есть контакты ветеринара-специалиста по грызунам",
                "Термометр (ректальный)",
                "Шприцы (без игл) для кормления/выпаивания",
                "Смесь для принудительного кормления (Critical Care)",
                "Антисептик: Хлоргексидин или Мирамистин(НЕ спиртовой)",
                "Грелка для согревания животного",
                "Наблюдение: Проверяете животное на наличие опухолей"
            )),
            Checklist("Социализация", listOf(
                "Пара: Животное (крыса/свинка/кролик) содержится в однополой паре",
                "Карантин: Новое животное прошло 2-3 недели карантина",
                "Нейтральная территория: Первое знакомство проводилось на нейтральной территории",
                "Хомяки: Сирийский хомяк содержится строго в одиночестве"
            ))
        ),
        "Птицы" to listOf(
            Checklist("Стоит ли заводить попугая?", listOf(
                "Долгосрочное планирование: Готовы к обязательствам на 15-80 лет?",
                "Шум: Готовы к ежедневному громкому шуму?",
                "Свободный полет: Готовы ежедневно выпускать птицу из клетки?",
                "Безопасность дома: Устранены все риски (тефлон, окна)?",
                "Одиночество: Готовы проводить достаточно времени с птицей?",
                "Питание: Готовы обеспечить правильный рацион (не только семена)?",
                "Орнитолог: Есть в вашем городе доступный ветеринар-орнитолог?"
            )),
            Checklist("Аптечка для птицы", listOf(
                "Телефон ветеринара-орнитолога",
                "Маленькая, безопасная переноска",
                "Настольная лампа и термометр (для обогрева)",
                "Кровоостанавливающее средство (крахмал/мука)",
                "Пинцет",
                "Шприцы (без игл)",
                "Кухонные весы (для контроля веса)"
            )),
            Checklist("Безопасность дома", listOf(
                "Тефлон: Исключена посуда с антипригарным покрытием",
                "Окна: Установлены сетки или закрыты во время полетов",
                "Вентиляторы: Выключены во время полетов",
                "Клетка: Стоит в светлом месте, не на сквозняке",
                "Жёрдочки: Используются натуральные ветки разного диаметра",
                "Сон: Обеспечен полный, темный сон (10-12 часов)"
            ))
        ),
        "Рептилии и амфибии" to listOf(
            Checklist("Стоит ли заводить рептилию?", listOf(
                "Специфика ухода: Готовы к уходу за животным без привязанности?",
                "Диета: Готовы кормить живыми кормовыми объектами?",
                "Освещение: Готовы обеспечить и регулярно менять УФ-лампы?",
                "Электроэнергия: Готовы к расходам на свет и обогрев?",
                "Долголетие: Готовы к обязательствам на десятки лет?",
                "Герпетолог: Есть в вашем регионе ветеринар-герпетолог?",
                "Гигиена: Готовы к строгой гигиене рук (риск сальмонеллеза)?"
            )),
            Checklist("Обустройство террариума", listOf(
                "Размер: Террариум соответствует взрослому размеру животного",
                "Контроль температуры: Установлены точка прогрева и прохладная зона",
                "Оборудование: Установлены термометры и гигрометры",
                "УФ-В лампа: Установлена и регулярно меняется",
                "Субстрат: Используется безопасный субстрат",
                "Свет: Обеспечен правильный режим дня и ночи",
                "Влажность: Поддерживается необходимая влажность"
            )),
            Checklist("Аптечка и уход", listOf(
                "Телефон ветеринара-герпетолога",
                "Кальциевые и витаминные добавки с D3",
                "Спрей-бутылка или туманогенератор",
                "Обеспечено наличие правильных кормовых объектов",
                "Есть отдельный карантинный контейнер",
                "Есть отдельное моющее средство только для террариума"
            ))
        )
    )
}

object ChecklistRoutes {
    const val MENU = "checklist_menu"
    const val MY_CHECKLISTS = "my_checklists"
    const val MY_CHECKLIST_DETAIL = "my_checklist_detail/{checklistId}"
    const val LIST = "checklist_list/{animalType}"

    const val EDIT_MY_CHECKLIST = "edit_my_checklist/{checklistId}"

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    onBack: () -> Unit,
    viewModel: PetViewModel
) {
    val application = LocalContext.current.applicationContext as Application
    val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(application))
    val currentAppTheme by themeViewModel.theme.collectAsState()
    val navController = rememberNavController()

    AppBackground(appTheme = currentAppTheme) {
        NavHost(
            navController = navController,
            startDestination = ChecklistRoutes.MENU,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(ChecklistRoutes.MENU) {
                ChecklistMenuScreen(navController = navController, onBack = onBack)
            }
            composable(
                route = ChecklistRoutes.LIST,
                arguments = listOf(navArgument("animalType") { type = NavType.StringType })
            ) { backStackEntry ->
                val animalType = backStackEntry.arguments?.getString("animalType") ?: ""
                ChecklistListScreen(
                    animalType = animalType,
                    navController = navController,
                    viewModel = viewModel
                )
            }
            composable(ChecklistRoutes.MY_CHECKLISTS) {
                MyChecklistsScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
            composable(
                route = ChecklistRoutes.MY_CHECKLIST_DETAIL,
                arguments = listOf(navArgument("checklistId") { type = NavType.IntType })
            ) { backStackEntry ->
                val checklistId = backStackEntry.arguments?.getInt("checklistId")
                UserChecklistDetailScreen(
                    checklistId = checklistId,
                    viewModel = viewModel,
                    navController = navController,
                    onNavigateToEdit = { id ->
                        navController.navigate("edit_my_checklist/$id")
                    }
                )
            }
            composable(
                route = ChecklistRoutes.EDIT_MY_CHECKLIST,
                arguments = listOf(navArgument("checklistId") {
                    type = NavType.IntType
                    defaultValue = -1
                })
            ) { backStackEntry ->
                val checklistId = backStackEntry.arguments?.getInt("checklistId")
                EditChecklistScreen(
                    checklistId = if (checklistId == -1) null else checklistId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistMenuScreen(navController: NavController, onBack: () -> Unit) {

    val animalTypes = getCurrentChecklistContent().keys.toList()
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.checklists_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(
                    R.string.back)) } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { navController.navigate(ChecklistRoutes.MY_CHECKLISTS) },
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.3f))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Folder, stringResource(R.string.checklists_my_lists), tint = Color.White)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(R.string.checklists_my_lists),
                            fontSize = 18.sp,

                        )
                    }
                }
            }
            items(animalTypes) { animalType ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { navController.navigate("checklist_list/$animalType") },
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = animalType,
                        modifier = Modifier.padding(16.dp),
                        color = Color.White,
                        fontSize = 18.sp,
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistListScreen(
    animalType: String,
    navController: NavController,
    viewModel: PetViewModel,
) {
    val localizedContent = getCurrentChecklistContent()
    val checklists = localizedContent[animalType] ?: emptyList()
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.checklists_title) + ": " + animalType) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(
                    R.string.back)) } },
                // ИЗМЕНЕНИЕ: Добавлены цвета для текста и иконок
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(checklists) { checklist ->
                val context = LocalContext.current
                TemplateChecklistCard(
                    checklist = checklist,
                    onCopyClick = {
                        viewModel.addChecklist(
                            checklist = UserChecklist(name = checklist.title),
                            items = checklist.items
                        )
                        Toast.makeText(context, context.getString(R.string.checklists_template_copied_toast, checklist.title), Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyChecklistsScreen(
    navController: NavController,
    viewModel: PetViewModel
) {
    val myChecklists by viewModel.userChecklists.collectAsState(initial = emptyList())

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.checklists_my_lists)) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(
                    R.string.back)) } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("edit_my_checklist/-1") },
                containerColor = MaterialTheme.colorScheme.secondary
            ) { Icon(Icons.Default.Add, stringResource(R.string.checklists_add_new)) }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (myChecklists.isEmpty()) { /* ... Текст "пусто" ... */ }
            else {
                items(myChecklists) { checklistWithItems ->
                    MyChecklistCard(
                        checklist = checklistWithItems.checklist,
                        onClick = {
                            navController.navigate("my_checklist_detail/${checklistWithItems.checklist.id}")
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserChecklistDetailScreen(
    checklistId: Int?,
    viewModel: PetViewModel,
    navController: NavController,
    onNavigateToEdit: (Int) -> Unit
) {
    val myChecklists by viewModel.userChecklists.collectAsState(initial = emptyList())
    val checklistWithItems = myChecklists.find { it.checklist.id == checklistId }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(checklistWithItems?.checklist?.name ?: stringResource(R.string.checklists_loading)) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(
                    R.string.back))
                } },
                actions = {
                    IconButton(onClick = { checklistId?.let { onNavigateToEdit(it) } }) {
                        Icon(Icons.Default.Edit, stringResource(R.string.checklists_edit))
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
        if (checklistWithItems == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(checklistWithItems.items) { item ->
                    ChecklistItemRow(
                        item = item,
                        onCheckedChange = { isChecked ->
                            viewModel.updateChecklistItem(item.copy(isChecked = isChecked))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ChecklistItemRow(
    item: UserChecklistItem,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!item.isChecked) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.secondary,
                uncheckedColor = Color.White
            )
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = item.text,
            color = if (item.isChecked) Color.Gray else Color.White,
            textDecoration = if (item.isChecked) TextDecoration.LineThrough else null
        )
    }
}

@Composable
fun TemplateChecklistCard(
    checklist: Checklist,
    onCopyClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val checkedItems = remember { mutableStateListOf<Boolean>().apply { addAll(List(checklist.items.size) { false }) } }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = checklist.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    fontSize = 18.sp
                )
                IconButton(onClick = onCopyClick) {
                    Icon(Icons.Default.ContentCopy, stringResource(R.string.checklists_copy_desc), tint = Color.White)
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.checklists_expand_desc),
                    tint = Color.White
                )
            }
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    checklist.items.forEachIndexed { index, item ->
                        ChecklistItem(
                            text = item,
                            isChecked = checkedItems[index],
                            onCheckedChange = { checkedItems[index] = it }
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun ChecklistItem(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isChecked) MaterialTheme.colorScheme.secondary else Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = if (isChecked) Color.Gray else Color.White,
            textDecoration = if (isChecked) TextDecoration.LineThrough else null
        )
    }
}

@Composable
fun MyChecklistCard(checklist: UserChecklist, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.3f))
    ) {
        Text(
            text = checklist.name,
            style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
            modifier = Modifier.padding(16.dp),
            fontSize = 18.sp
        )
    }
}

