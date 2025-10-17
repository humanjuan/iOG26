package com.humanjuan.iog26.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humanjuan.iog26.data.PrefixScope
import com.humanjuan.iog26.ui.PrefixRulesViewModel
import com.humanjuan.iog26.ui.UiPrefixRule
import com.humanjuan.iog26.ui.theme.LocalStrings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrefixListScreen(
    vm: PrefixRulesViewModel = viewModel(),
    onBack: () -> Unit = {},
    onOpenMenu: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf(TextFieldValue("")) }
    val strings = LocalStrings.current

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = LocalStrings.current.addPrefixTitle)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val items by vm.items.collectAsState()
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
                .padding(horizontal = 20.dp, vertical = 16.dp)
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 88.dp)
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
            }
        }
    }

    if (showDialog) {
        AddPrefixDialog(
            onDismiss = { showDialog = false },
            onSave = { prefixDigits, cc ->
                val error = vm.add(prefixDigits, cc)
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
                        contentDescription = "Eliminar",
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
        onValueChange = onValueChange,
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
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddPrefixDialog(
    onDismiss: () -> Unit,
    onSave: (prefixDigits: String, countryCode: String?) -> Unit
) {
    var prefix by remember { mutableStateOf(TextFieldValue("")) }
    var cc by remember { mutableStateOf(TextFieldValue("")) }

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
                    )
                )
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
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val digits = prefix.text
                    val ccValue = cc.text.ifBlank { null }
                    onSave(digits, ccValue)
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

private fun dateFmt(ts: Long): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(ts))
}
