package com.name.petmemo.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext // для доступа к Assets
import java.io.InputStream
import androidx.compose.runtime.* // Для remember и state
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.runtime.remember // Для remember(key)
import androidx.compose.runtime.mutableStateOf // Для mutableStateOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.annotations.SerializedName

object UsefulRoutes {
    const val MENU = "useful_menu"
    const val ARTICLE_LIST = "article_list/{animalType}"
    const val ARTICLE_DETAIL = "article_detail/{animalType}/{articleIndex}"
}
data class Article(
    @SerializedName("title")    val title: String? = null,
    @SerializedName("content")  val content: String? = null
)


// 2. ENUM ДЛЯ КЛЮЧЕЙ
enum class AnimalKey(val assetFileName: String) {
    CATS("cats.json"),
    DOGS("dogs.json"),
    FISH("fish.json"),
    BIRDS("birds.json"),
    SMALL_MAMMALS("small_mammals.json"),
    REPTILES_AMPHIBIANS("reptiles_amphibians.json");

    companion object {
        fun fromStringKey(key: String): AnimalKey? {
            return when (key) {
                "Кошки" -> CATS
                "Собаки" -> DOGS
                "Рыбки" -> FISH
                "Птицы" -> BIRDS
                "Мелкие млекопитающие" -> SMALL_MAMMALS
                "Рептилии и амфибии" -> REPTILES_AMPHIBIANS
                else -> null
            }
        }
    }
}

fun loadArticlesFromAssets(context: Context, languageCode: String, key: AnimalKey): List<Article> {
    val assetPath = "articles/$languageCode/${key.assetFileName}"

    return try {
        val inputStream: InputStream = context.assets.open(assetPath)
        val size: Int = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val jsonString = String(buffer, Charsets.UTF_8)
        val gson = Gson()
        val type = object : com.google.gson.reflect.TypeToken<List<Article>>() {}.type
        return gson.fromJson(jsonString, type)
        // ------------------------------------

    } catch (e: Exception) {
        e.printStackTrace()
        listOf(Article(context.getString(_root_ide_package_.com.name.petmemo.R.string.error_content_not_found_articles), "Ошибка при загрузке контента: ${e.message}"))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsefulScreen(onBack: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val topAppBarBackgroundColor = MaterialTheme.colorScheme.primary
    val iconAndTextColor = MaterialTheme.colorScheme.onPrimary

    val title = when {
        currentRoute?.startsWith("article_list") == true -> {
            val animalType = navBackStackEntry?.arguments?.getString("animalType") ?: stringResource(
                _root_ide_package_.com.name.petmemo.R.string.content_articles)
            animalType
        }
        currentRoute?.startsWith("article_detail") == true -> stringResource(_root_ide_package_.com.name.petmemo.R.string.title_article_detail) // "Статья"
        else -> stringResource(_root_ide_package_.com.name.petmemo.R.string.title_main_menu) // "Полезное"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, color = iconAndTextColor) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (navController.previousBackStackEntry != null) navController.popBackStack() else onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(_root_ide_package_.com.name.petmemo.R.string.back), tint = iconAndTextColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = topAppBarBackgroundColor)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = UsefulRoutes.MENU,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(UsefulRoutes.MENU) {
                UsefulMenuScreen(navController = navController, innerPadding = paddingValues)
            }
            composable(
                route = UsefulRoutes.ARTICLE_LIST,
                arguments = listOf(navArgument("animalType") { type = NavType.StringType })
            ) { backStackEntry ->
                val animalType = backStackEntry.arguments?.getString("animalType")
                if (animalType != null) {
                    ArticleListScreen(navController = navController, animalType = animalType, innerPadding = paddingValues)
                }
            }
            composable(
                route = UsefulRoutes.ARTICLE_DETAIL,
                arguments = listOf(
                    navArgument("animalType") { type = NavType.StringType },
                    navArgument("articleIndex") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val animalType = backStackEntry.arguments?.getString("animalType")
                val articleIndex = backStackEntry.arguments?.getInt("articleIndex")
                if (animalType != null && articleIndex != null) {
                    ArticleDetailScreen(animalType = animalType, articleIndex = articleIndex, innerPadding = paddingValues)
                }
            }
        }
    }
}
@Composable
fun UsefulMenuScreen(navController: NavController, innerPadding: PaddingValues) {
    val animalTypesTranslated = listOf(
        stringResource(_root_ide_package_.com.name.petmemo.R.string.animal_type_cats),
        stringResource(_root_ide_package_.com.name.petmemo.R.string.animal_type_dogs),
        stringResource(_root_ide_package_.com.name.petmemo.R.string.animal_type_fish),
        stringResource(_root_ide_package_.com.name.petmemo.R.string.animal_type_birds),
        stringResource(_root_ide_package_.com.name.petmemo.R.string.animal_type_small_mammals),
        stringResource(_root_ide_package_.com.name.petmemo.R.string.animal_type_reptiles_amphibians)
    )
    val animalTypeMap: Map<String, String> = mapOf(
        stringResource(_root_ide_package_.com.name.petmemo.R.string.animal_type_cats) to AnimalKey.CATS.name,
        stringResource(_root_ide_package_.com.name.petmemo.R.string.animal_type_dogs) to AnimalKey.DOGS.name,
        stringResource(_root_ide_package_.com.name.petmemo.R.string.animal_type_fish) to AnimalKey.FISH.name,
        stringResource(_root_ide_package_.com.name.petmemo.R.string.animal_type_birds) to AnimalKey.BIRDS.name,
        stringResource(_root_ide_package_.com.name.petmemo.R.string.animal_type_small_mammals) to AnimalKey.SMALL_MAMMALS.name,
        stringResource(_root_ide_package_.com.name.petmemo.R.string.animal_type_reptiles_amphibians) to AnimalKey.REPTILES_AMPHIBIANS.name
    )


    val gradientStartColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
    val gradientEndColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
    val cardBackgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
    val cardTextColor = MaterialTheme.colorScheme.onSurface

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(listOf(gradientStartColor, gradientEndColor)))
            .padding(innerPadding),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(animalTypesTranslated) { translatedName ->
            val animalKeyName = animalTypeMap[translatedName] ?: ""
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("article_list/$animalKeyName") },
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
            ) {
                Text(
                    text = translatedName,
                    modifier = Modifier.padding(16.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 18.sp
                )
            }
        }
    }
}
@Composable
fun ArticleListScreen(navController: NavController, animalType: String, innerPadding: PaddingValues) {
    val context = LocalContext.current
    val languageCode = LocalConfiguration.current.locales.get(0).language
    val animalKey = remember(animalType) { AnimalKey.valueOf(animalType) }
    val articles by remember(animalKey, languageCode) {
        mutableStateOf(loadArticlesFromAssets(context, languageCode, animalKey))
    }

    val gradientStartColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
    val gradientEndColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
    val cardBackgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
    val cardTextColor = MaterialTheme.colorScheme.onSurface

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(listOf(gradientStartColor, gradientEndColor)))
            .padding(innerPadding),
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(articles.size) { index ->
            val article = articles[index]
            Card(
                modifier = Modifier.fillMaxWidth().clickable { navController.navigate("article_detail/$animalType/$index") },
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
            ) {
                Text(
                    text = article.title ?: "",
                    modifier = Modifier.padding(16.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ArticleDetailScreen(animalType: String, articleIndex: Int, innerPadding: PaddingValues) {
    val context = LocalContext.current
    val languageCode = LocalConfiguration.current.locales.get(0).language
    val animalKey = remember(animalType) { AnimalKey.valueOf(animalType) }
    val articles by remember(animalKey, languageCode) {
        mutableStateOf(loadArticlesFromAssets(context, languageCode, animalKey))
    }

    val article = articles.getOrNull(articleIndex)

    val gradientStartColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
    val gradientEndColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
    val textColor = MaterialTheme.colorScheme.onBackground

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(listOf(gradientStartColor, gradientEndColor)))
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp)
    ) {
        if (article != null) {
            item {
                Text(
                    text = article.title ?: "",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                ArticleText(text = article.content ?: "")
            }
        } else {
            item {
                Text(
                    text = stringResource(_root_ide_package_.com.name.petmemo.R.string.error_content_not_found_articles),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
@Composable
fun ArticleText(text: String) {
    val lines = text.split("\n")

    Column {
        lines.forEach { line ->
            if (line.startsWith("## ")) {
                Text(
                    text = line.removePrefix("## "),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            } else {
                val annotatedString = buildAnnotatedString {
                    val regex = Regex("""(\*\*.*?\*\*)|(\*.*?\*)""")
                    var lastIndex = 0
                    regex.findAll(line).forEach { matchResult ->
                        val match = matchResult.value
                        val startIndex = matchResult.range.first
                        if (startIndex > lastIndex) {
                            append(line.substring(lastIndex, startIndex))
                        }

                        when {
                            match.startsWith("**") -> {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(match.removeSurrounding("**"))
                                }
                            }
                            match.startsWith("*") -> {
                                withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                                    append(match.removeSurrounding("*"))
                                }
                            }
                        }
                        lastIndex = matchResult.range.last + 1
                    }
                    if (lastIndex < line.length) {
                        append(line.substring(lastIndex))
                    }
                }
                Text(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}