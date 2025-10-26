package com.humanjuan.iog26.ui.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humanjuan.iog26.ui.AppPrefsViewModel
import com.humanjuan.iog26.ui.SettingsViewModel
import com.humanjuan.iog26.ui.theme.AppThemeOption
import com.humanjuan.iog26.ui.theme.LocalPrivacy
import com.humanjuan.iog26.ui.theme.LocalStrings
import com.humanjuan.iog26.ui.theme.PrivacyEn
import com.humanjuan.iog26.ui.theme.PrivacyEs
import com.humanjuan.iog26.ui.theme.PrivacyIt
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    vm: SettingsViewModel = viewModel(),
    onBack: () -> Unit = {},
    onOpenMenu: () -> Unit = {}
) {
    val state by vm.ui.collectAsState()
    val host = remember { SnackbarHostState() }
    val strings = LocalStrings.current
    val privacy = LocalPrivacy.current

    LaunchedEffect(Unit) {
        vm.events.collectLatest { msg -> host.showSnackbar(msg) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = host) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val gradient = Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.background
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = strings.settingsIntro,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            // Grupo 1: Apariencia y lenguaje
            SettingsCard(title = "${strings.themeLabel} / ${strings.languageLabel}") {
                val prefsVm: AppPrefsViewModel = viewModel()
                val appPrefs by prefsVm.prefs.collectAsState()

                ThemeRow(
                    current = appPrefs.theme,
                    onSelect = { value -> prefsVm.setTheme(value) }
                )
                Spacer(Modifier.height(12.dp))
                LanguageRow(
                    current = appPrefs.language,
                    onSelect = { value -> prefsVm.setLanguage(value) }
                )
            }

            // Grupo 2: Bloqueo
            SettingsCard(title = strings.groupBlocking) {
                RowSwitch(
                    title = strings.blockAnonymous,
                    subtitle = strings.blockAnonymousSub,
                    checked = state.blockAnonymousEnabled,
                    onCheckedChange = vm::setBlockAnonymous
                )

                RowSwitch(
                    title = strings.blockUnknownContacts,
                    subtitle = strings.blockUnknownContactsSub,
                    checked = state.blockUnknownContactsEnabled,
                    onCheckedChange = vm::setBlockUnknownContacts
                )

                RowSwitch(
                    title = strings.skipCallLog,
                    subtitle = strings.skipCallLogSub,
                    checked = state.skipCallLogOnBlock,
                    onCheckedChange = vm::setSkipCallLog
                )

                RowSwitch(
                    title = strings.skipNotif,
                    subtitle = strings.skipNotifSub,
                    checked = state.skipNotificationOnBlock,
                    onCheckedChange = vm::setSkipNotif
                )
            }

            // Grupo 3: Resumen diario
            SettingsCard(title = strings.groupDigest) {
                RowSwitch(
                    title = strings.digestEnable,
                    subtitle = strings.digestHint,
                    checked = state.digestEnabled,
                    onCheckedChange = vm::setDigestEnabled
                )

                TimeRow(
                    title = strings.change,
                    hour = state.digestHour,
                    minute = state.digestMinute,
                    enabled = state.digestEnabled,
                    onPick = { h, m -> vm.setDigestTime(h, m) }
                )
            }

            // Grupo 4: Información del sistema
            SettingsCard(title = strings.systemInfoTitle) {
                val appVersion = com.humanjuan.iog26.BuildConfig.APP_VERSION
                val kotlinVer = KotlinVersion.CURRENT.toString()
                val androidVer = "${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})"
                val device = "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"

                InfoRow(label = strings.systemAppVersion, value = appVersion)
                InfoRow(label = strings.systemKotlinVersion, value = kotlinVer)
                InfoRow(label = strings.systemAndroidVersion, value = androidVer)
                InfoRow(label = strings.systemDevice, value = device)

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                Text(
                    text = strings.systemLibraries,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                InfoRow(label = "Material3", value = com.humanjuan.iog26.BuildConfig.MATERIAL3_VERSION)
                InfoRow(label = "Room", value = com.humanjuan.iog26.BuildConfig.ROOM_VERSION)
                InfoRow(label = "WorkManager", value = com.humanjuan.iog26.BuildConfig.WORK_VERSION)
                InfoRow(label = "DataStore", value = com.humanjuan.iog26.BuildConfig.DATASTORE_VERSION)
                InfoRow(label = "libphonenumber", value = com.humanjuan.iog26.BuildConfig.LIBPHONENUMBER_VERSION)
            }

            // Grupo 5: Política de Privacidad
            var showPrivacy by remember { mutableStateOf(false) }
            if (showPrivacy) {
                Dialog(onDismissRequest = { showPrivacy = false }) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
//                        tonalElevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = privacy.title,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )

                            listOf(
                                privacy.generalInfoTitle to privacy.generalInfoText,
                                privacy.dataCollectedTitle to """
                                    ${privacy.dataCollectedText}

                                    * ${privacy.dataCollectedItemA}
                                    * ${privacy.dataCollectedItemB}

                                    ${privacy.dataCollectedEndText}
                                """.trimIndent(),
                                privacy.useDataTitle to """
                                    ${privacy.useDataText}

                                    * ${privacy.useDataItemA}
                                    * ${privacy.useDataItemB}
                                """.trimIndent(),
                                privacy.devicePermissionsTitle to """
                                    ${privacy.devicePermissionsText}

                                    * ${privacy.devicePermissionsItemA}
                                    * ${privacy.devicePermissionsItemB}
                                    * ${privacy.devicePermissionsItemC}

                                    ${privacy.devicePermissionsEndText}
                                """.trimIndent(),
                                privacy.securityTitle to privacy.securityText,
                                privacy.contactTitle to privacy.contactText
                            ).forEach { (title, content) ->
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = content,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 20.sp
                                    )
                                )
                                Spacer(Modifier.height(4.dp))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { showPrivacy = false }) {
                                    Text(privacy.close)
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPrivacy = true }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = LocalStrings.current.privacyTitle,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open privacy policy",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
private fun SettingsCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            content()
        }
    }
}

@Composable
private fun RowSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun TimeRow(
    title: String,
    hour: Int,
    minute: Int,
    enabled: Boolean,
    onPick: (Int, Int) -> Unit
) {
    val ctx = LocalContext.current
    val label = "%02d:%02d".format(hour, minute)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        )
        TextButton(
            onClick = {
                TimePickerDialog(ctx, { _, h, m -> onPick(h, m) }, hour, minute, true).show()
            },
            enabled = enabled
        ) {
            Text(title)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeRow(current: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf(
        "GREEN" to LocalStrings.current.themeGreen,
        "NAVY" to LocalStrings.current.themeNavy,
        "SUNSET" to LocalStrings.current.themeSunset,
        "VIOLET" to LocalStrings.current.themeViolet
    )
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = items.firstOrNull { it.first == current }?.second ?: current,
            onValueChange = {},
            readOnly = true,
            label = { Text(LocalStrings.current.themeLabel) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { (key, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = { onSelect(key); expanded = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageRow(current: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf(
        "ES" to LocalStrings.current.languageEs,
        "EN" to LocalStrings.current.languageEn,
        "IT" to LocalStrings.current.languageIt
    )
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = items.firstOrNull { it.first == current.uppercase() }?.second ?: current,
            onValueChange = {},
            readOnly = true,
            label = { Text(LocalStrings.current.languageLabel) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { (key, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = { onSelect(key); expanded = false }
                )
            }
        }
    }
}
