package com.humanjuan.iog26.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humanjuan.iog26.ui.PrefixRulesViewModel
import com.humanjuan.iog26.ui.UiPrefixRule
import com.humanjuan.iog26.ui.theme.LocalStrings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrefixListScreen(
    vm: PrefixRulesViewModel = viewModel(),
    onRegisterCentralAction: (((() -> Unit)) -> Unit)? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf(TextFieldValue("")) }
    val strings = LocalStrings.current
    val prefsVm: com.humanjuan.iog26.ui.AppPrefsViewModel = viewModel()
    val devRegexMode by prefsVm.prefs.collectAsState()
    val showRegex = devRegexMode.devRegexMode

    LaunchedEffect(Unit) {
        onRegisterCentralAction?.invoke { showDialog = true }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        val prefsVm: com.humanjuan.iog26.ui.AppPrefsViewModel = viewModel()
        val devRegexMode by prefsVm.prefs.collectAsState()
        val showRegex = devRegexMode.devRegexMode
        val items by vm.items.collectAsState()
        val regexItems by vm.regexItems.collectAsState()
        val filtered = remember(items, query) {
            val q = query.text.trim().lowercase()
            if (q.isEmpty()) items else items.filter { it.label.lowercase().contains(q) }
        }

        val gradient = Brush.verticalGradient(
            listOf(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.background
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 0.dp)
        ) {
            SearchBar(
                value = query,
                onValueChange = { query = it },
                placeholder = LocalStrings.current.searchPrefixPlaceholder
            )
            Text(
                text = strings.totalTemplate.format(filtered.size),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(top = 5.dp)
            )

            Spacer(Modifier.height(16.dp))

            var editingRegex by remember { mutableStateOf<PrefixRulesViewModel.UiRegexRule?>(null) }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 10.dp)
            ) {
                items(filtered, key = { rule -> rule.id }) { rule ->
                    SwipeToDeleteItem(
                        rule = rule,
                        onDelete = {
                            vm.remove(rule.id)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "Prefijo eliminado: ${rule.label}"
                                )
                            }
                        }
                    )
                }

                if (regexItems.isNotEmpty()) {
                    item {
                        Text(
                            text = LocalStrings.current.regexRulesTitle,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(regexItems, key = { it.id }) { rule ->
                        RegexRuleSwipeItem(
                            pattern = rule.pattern,
                            createdAt = rule.createdAt,
                            onClick = { editingRegex = rule },
                            onDelete = { vm.removeRegex(rule.id) }
                        )
                    }
                }
            }

            if (editingRegex != null) {
                EditRegexDialog(
                    initial = editingRegex!!.pattern,
                    title = LocalStrings.current.editRegexTitle,
                    onDismiss = { editingRegex = null },
                    onSave = { newPattern ->
                        val error = vm.updateRegex(editingRegex!!.id, newPattern)
                        if (error == null) editingRegex = null else scope.launch { snackbarHostState.showSnackbar(error) }
                    }
                )
            }
        }
    }

    if (showDialog) {
        AddPrefixDialog(
            showRegex = showRegex,
            onDismiss = { showDialog = false },
            onSave = { prefixDigits, cc, regex ->
                val err1 = vm.add(prefixDigits, cc)
                val err2 = if (!regex.isNullOrBlank()) vm.addRegex(regex) else null
                val error = err1 ?: err2
                if (error == null) showDialog = false
                else {
                    scope.launch { snackbarHostState.showSnackbar(error) }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteItem(
    rule: UiPrefixRule,
    onDelete: () -> Unit
) {
    var dismissed by remember { mutableStateOf(false) }

    if (!dismissed) {
        val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = {
                if (it == SwipeToDismissBoxValue.StartToEnd || it == SwipeToDismissBoxValue.EndToStart) {
                    dismissed = true
                    onDelete()
                    true
                } else false
            },
            positionalThreshold = { it * 0.3f }
        )

        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = true,
            enableDismissFromEndToStart = true,
            backgroundContent = {
                val color = when (dismissState.dismissDirection) {
                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFFEF5350)
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFEF5350)
                    else -> Color.Transparent
                }
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color, RoundedCornerShape(20.dp))
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = LocalStrings.current.delete,
                        tint = Color.White
                    )
                }
            },
            content = {
                PrefixRuleCard(rule = rule)
            }
        )
    }
}

@Composable
private fun PrefixRuleCard(rule: UiPrefixRule) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Text(
                rule.label,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            )
            val strings = LocalStrings.current
            val dateStr = dateFmt(rule.createdAt)
            val subtitle = strings.blockedOnTemplate.format(dateStr)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = { v ->
            val original = v.text
            val cursor = v.selection.end.coerceIn(0, original.length)
            val cleaned = original.filter { it.isDigit() || it == '+' || it == '*' }
            var removedBefore = 0
            for (i in 0 until cursor) {
                if (i < original.length && !(original[i].isDigit() || original[i] == '+' || original[i] == '*')) removedBefore++
            }
            val newCursor = (cursor - removedBefore).coerceIn(0, cleaned.length)
            onValueChange(TextFieldValue(cleaned, selection = androidx.compose.ui.text.TextRange(newCursor)))
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(50)),
        singleLine = true,
        placeholder = {
            Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddPrefixDialog(
    showRegex: Boolean,
    onDismiss: () -> Unit,
    onSave: (prefixDigits: String, countryCode: String?, regex: String?) -> Unit
) {
    var prefix by remember { mutableStateOf(TextFieldValue("")) }
    var cc by remember { mutableStateOf(TextFieldValue("")) }
    var regexText by remember { mutableStateOf(TextFieldValue("")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                LocalStrings.current.addPrefixTitle,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = prefix,
                    onValueChange = {
                        val cleaned = it.text.filter(Char::isDigit)
                        prefix = TextFieldValue(
                            cleaned,
                            selection = androidx.compose.ui.text.TextRange(cleaned.length)
                        )
                    },
                    label = {
                        Text(
                            LocalStrings.current.prefixLabel,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                if (showRegex) {
                    OutlinedTextField(
                        value = regexText,
                        onValueChange = { regexText = it },
                        label = { Text(LocalStrings.current.regexLabel) },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
                OutlinedTextField(
                    value = cc,
                    onValueChange = {
                        val cleaned = it.text.removePrefix("+").filter(Char::isDigit)
                        cc = TextFieldValue(
                            cleaned,
                            selection = androidx.compose.ui.text.TextRange(cleaned.length)
                        )
                    },
                    label = {
                        Text(
                            LocalStrings.current.countryCodeLabel,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val digits = prefix.text
                    val ccValue = cc.text.ifBlank { null }
                    onSave(digits, ccValue, regexText.text.ifBlank { null })
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) { Text(LocalStrings.current.save) }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) { Text(LocalStrings.current.cancel) }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegexRuleSwipeItem(
    pattern: String,
    createdAt: Long,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var dismissed by remember { mutableStateOf(false) }
    if (!dismissed) {
        val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = {
                if (it == SwipeToDismissBoxValue.StartToEnd || it == SwipeToDismissBoxValue.EndToStart) {
                    dismissed = true
                    onDelete()
                    true
                } else false
            },
            positionalThreshold = { it * 0.3f }
        )
        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = true,
            enableDismissFromEndToStart = true,
            backgroundContent = {
                val color = when (dismissState.dismissDirection) {
                    SwipeToDismissBoxValue.StartToEnd, SwipeToDismissBoxValue.EndToStart -> Color(0xFFEF5350)
                    else -> Color.Transparent
                }
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color, RoundedCornerShape(20.dp))
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = LocalStrings.current.delete,
                        tint = Color.White
                    )
                }
            },
            content = {
                RegexRuleCard(pattern = pattern, createdAt = createdAt, onClick = onClick)
            }
        )
    }
}

@Composable
private fun RegexRuleCard(pattern: String, createdAt: Long, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Text(
                text = "/$pattern/",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            )
            val subtitle = LocalStrings.current.blockedOnTemplate.format(dateFmt(createdAt))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun EditRegexDialog(
    initial: String,
    title: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(LocalStrings.current.regexLabel) },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(text.trim()) }) { Text(LocalStrings.current.save) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(LocalStrings.current.cancel) }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp)
    )
}

private fun dateFmt(ts: Long): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(ts))
}
