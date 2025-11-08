package com.humanjuan.iog26.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humanjuan.iog26.ui.NumbersViewModel
import com.humanjuan.iog26.ui.theme.LocalStrings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberListScreen(
    vm: NumbersViewModel = viewModel(),
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


    // Register central bottom bar action to open the Add Number dialog
    LaunchedEffect(Unit) {
        onRegisterCentralAction?.invoke { showDialog = true }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        val prefsVm: com.humanjuan.iog26.ui.AppPrefsViewModel = viewModel()
        val devRegexMode = prefsVm.prefs.collectAsState().value.devRegexMode
        val items by vm.items.collectAsState()
        val regexItems by vm.regexItems.collectAsState()
        val filtered = remember(items, query) {
            val q = query.text.trim().lowercase()
            if (q.isEmpty()) items else items.filter { it.e164.lowercase().contains(q) }
        }

        val gradient = Brush.verticalGradient(
            listOf(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.background
            )
        )

        var editingRegex by remember { mutableStateOf<NumbersViewModel.UiRegexRule?>(null) }

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
                placeholder = strings.searchNumberPlaceholder
            )

            Text(
                text = strings.totalTemplate.format(filtered.size),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(top = 5.dp)
            )
            Spacer(Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 10.dp)
            ) {
                items(filtered, key = { item -> "n:${item.e164}" }) { item ->
                    SwipeToDeleteItem(
                        item = item,
                        onDelete = {
                            vm.remove(item.e164)
                            scope.launch {
                                snackbarHostState.showSnackbar(strings.deletedNumberTemplate.format(item.e164))
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
                    items(regexItems, key = { "rn:${it.id}" }) { rule ->
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
        AddNumberDialog(
            showRegex = showRegex,
            onDismiss = { showDialog = false },
            onSave = { raw, regex ->
                val wantNumber = raw.isNotBlank()
                val wantRegex = !regex.isNullOrBlank()

                var earlyError: String? = null
                if (wantRegex) {
                    try { Regex(regex) } catch (t: Throwable) {
                        earlyError = t.message ?: strings.regexInvalidMessage
                    }
                }

                if (earlyError != null) {
                    scope.launch { snackbarHostState.showSnackbar(earlyError) }
                } else {
                    val err1 = if (wantNumber) vm.add(raw) else null
                    if (err1 != null) {
                        scope.launch { snackbarHostState.showSnackbar(err1) }
                    } else {
                        val err2 = if (wantRegex) vm.addRegex(regex) else null
                        if (err2 == null) showDialog = false else scope.launch { snackbarHostState.showSnackbar(err2) }
                    }
                }
            }
        )
    }
}

// --- Swipe-to-delete moderno ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteItem(
    item: com.humanjuan.iog26.ui.UiBlockedNumber,
    onDelete: () -> Unit
) {
    var dismissed by remember { mutableStateOf(false) }

    if (!dismissed) {
        val dismissState = rememberSwipeToDismissBoxState(
            positionalThreshold = { it * 0.3f }
        )

        LaunchedEffect(dismissState.currentValue) {
            if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd ||
                dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                dismissed = true
                onDelete()
            }
        }

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
                    val strings = LocalStrings.current
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = strings.delete,
                        tint = Color.White
                    )
                }
            },
            content = {
                BlockedNumberCard(item = item)
            }
        )
    }
}

@Composable
private fun BlockedNumberCard(item: com.humanjuan.iog26.ui.UiBlockedNumber) {
    val ctx = LocalContext.current
    var displayName by remember(item.e164) { mutableStateOf<String?>(null) }

    LaunchedEffect(item.e164) {
        try {
            val uri = android.net.Uri.withAppendedPath(
                android.provider.ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                android.net.Uri.encode(item.e164)
            )
            val cursor = ctx.contentResolver.query(
                uri,
                arrayOf(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME),
                null, null, null
            )
            cursor?.use { c ->
                if (c.moveToFirst()) displayName = c.getString(0)
            }
        } catch (_: Throwable) { }
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Text(
                text = displayName ?: item.e164,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            )
            if (displayName != null) {
                Text(
                    text = item.e164,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            val strings = LocalStrings.current
            val dateStr = dateFmt(item.createdAt)
            Text(
                text = strings.blockedOnTemplate.format(dateStr),
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
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
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
private fun AddNumberDialog(
    showRegex: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String?) -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var regexText by remember { mutableStateOf(TextFieldValue("")) }
    val ctx = LocalContext.current

    val contactPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri ->
        if (uri != null) {
            try {
                val id = android.content.ContentUris.parseId(uri)
                val cursor = ctx.contentResolver.query(
                    android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER),
                    android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                    arrayOf(id.toString()),
                    null
                )
                cursor?.use { c ->
                    if (c.moveToFirst()) {
                        val number = c.getString(0)
                        if (!number.isNullOrBlank()) onSave(number, null)
                    }
                }
            } catch (_: Throwable) { }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                LocalStrings.current.addNumberTitle,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { v ->
                        val original = v.text
                        val cursor = v.selection.end.coerceIn(0, original.length)
                        val cleaned = original.filter { it.isDigit() || it == '+' || it == '*' }
                        var removedBefore = 0
                        for (i in 0 until cursor) {
                            if (i < original.length && !(original[i].isDigit() || original[i] == '+' || original[i] == '*')) removedBefore++
                        }
                        val newCursor = (cursor - removedBefore).coerceIn(0, cleaned.length)
                        text = TextFieldValue(cleaned, selection = androidx.compose.ui.text.TextRange(newCursor))
                    },
                    label = { Text(LocalStrings.current.addNumberLabel) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
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
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                Text(
                    LocalStrings.current.addNumberHint,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(
                        onClick = { contactPicker.launch(null) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(LocalStrings.current.fromContacts)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(text.text.trim(), regexText.text.trim().ifBlank { null }) },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
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
            positionalThreshold = { it * 0.3f }
        )
        LaunchedEffect(dismissState.currentValue) {
            if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd ||
                dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                dismissed = true
                onDelete()
            }
        }
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
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp)
                .clickable { onClick() }
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
