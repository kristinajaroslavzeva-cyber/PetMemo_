package com.name.petmemo.ui.screens

import AppBackground
import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.name.petmemo.data.model.SettingsManager
import com.name.petmemo.ui.ThemeViewModel
import com.name.petmemo.ui.ThemeViewModelFactory
import com.name.petmemo.ui.theme.AppTheme
import androidx.compose.ui.res.stringResource
import com.name.petmemo.R
import com.name.petmemo.com.name.petmemo.ui.screens.SelectionDialog


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val currentTheme by themeViewModel.theme.collectAsState()
    val allThemes = AppTheme.values().toList()
    val settingsManager = remember { SettingsManager(themeViewModel.getApplication()) }
    var currentCurrency by remember { mutableStateOf(settingsManager.getCurrency()) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    val currencyOptions = listOf("тнг", "₽", "$", "€")

    if (showCurrencyDialog) {
        SelectionDialog(
            title = stringResource(R.string.dialog_select_currency),
            options = currencyOptions,
            onDismiss = { showCurrencyDialog = false },
            onSelect = { index ->
                val selectedCurrency = currencyOptions[index]
                settingsManager.saveCurrency(selectedCurrency)
                currentCurrency = selectedCurrency
                showCurrencyDialog = false
            }
        )
    }

    if (showThemeDialog) {
        SelectionDialog(
            title = stringResource(R.string.dialog_select_theme),
            options = allThemes.map { it.themeName },
            onDismiss = { showThemeDialog = false },
            onSelect = { index ->
                val selectedTheme = allThemes[index]
                themeViewModel.onThemeChange(selectedTheme)
                showThemeDialog = false
            }
        )
    }


    AppBackground(appTheme = currentTheme) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.settings_title)) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад") } },
                    // ИСПРАВЛЕНО: Цвет TopAppBar сделан сплошным для единообразия
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SettingsRow(
                    icon = painterResource(id = R.drawable.ic_theme_24),
                    label = stringResource(R.string.settings_label_theme),
                    value = currentTheme.themeName,
                    onClick = { showThemeDialog = true }
                )
                SettingsRow(
                    icon = painterResource(id = R.drawable.ic_money_24),
                    label = stringResource(R.string.settings_label_currency),
                    value = currentCurrency,
                    onClick = { showCurrencyDialog = true }
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(icon: Painter, label: String, value: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painter = icon, contentDescription = label, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
        }
    }
}