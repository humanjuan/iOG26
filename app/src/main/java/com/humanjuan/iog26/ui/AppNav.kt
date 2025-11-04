package com.humanjuan.iog26.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.humanjuan.iog26.R
import com.humanjuan.iog26.ui.screens.*
import com.humanjuan.iog26.ui.theme.LocalStrings

object Routes {
    const val HOME = "home"
    const val NUMBERS = "numbers"
    const val PREFIXES = "prefixes"
    const val SETTINGS = "settings"
    const val HISTORY = "history"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNav() {
    val nav = rememberNavController()
    val currentBackStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val effectiveRoute = currentRoute ?: Routes.HISTORY

    val strings = LocalStrings.current

    var actionHistory by remember { mutableStateOf<(() -> Unit)?>(null) }
    var actionNumbers by remember { mutableStateOf<(() -> Unit)?>(null) }
    var actionPrefixes by remember { mutableStateOf<(() -> Unit)?>(null) }

    var showDedicationDialog by remember { mutableStateOf(false) }

    if (showDedicationDialog) {
        Dialog(onDismissRequest = { }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_iog26),
                        contentDescription = "Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = strings.dedicationMessage,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

    val screenTitle = when (effectiveRoute) {
        Routes.HISTORY -> strings.historyTitle
        Routes.NUMBERS -> strings.numbersTitle
        Routes.PREFIXES -> strings.prefixesTitle
        Routes.SETTINGS -> strings.settingsTitle
        Routes.HOME -> strings.homeTitle
        else -> strings.appTitle
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = screenTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                },
                actions = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_iog26),
                        contentDescription = "Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        showDedicationDialog = true
                                    }
                                )
                            }
                            .padding(end = 10.dp)
                            .size(40.dp)
                    )
                },
                windowInsets = TopAppBarDefaults.windowInsets.exclude(WindowInsets.navigationBars)
            )
        },
        bottomBar = {
            val centerVisible = when (effectiveRoute) {
                Routes.SETTINGS -> false
                else -> true
            }
            val centerIcon = when (effectiveRoute) {
                Routes.HISTORY -> Icons.Filled.Refresh
                else -> Icons.Filled.Add
            }
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    nav.navigate(route) {
                        popUpTo(nav.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                centerVisible = centerVisible,
                centerIcon = centerIcon,
                onCenterClick = {
                    when (effectiveRoute) {
                        Routes.HISTORY -> actionHistory?.invoke()
                        Routes.NUMBERS -> actionNumbers?.invoke()
                        Routes.PREFIXES -> actionPrefixes?.invoke()
                        else -> {}
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Routes.HISTORY,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HISTORY) { BlockedHistoryScreen(onRegisterCentralAction = { action ->
                actionHistory = action }) }
            composable(Routes.NUMBERS) { NumberListScreen(onRegisterCentralAction = { action ->
                actionNumbers = action }) }
            composable(Routes.PREFIXES) { PrefixListScreen(onRegisterCentralAction = { action ->
                actionPrefixes = action }) }
            composable(Routes.SETTINGS) { SettingsScreen() }
            composable(Routes.HOME) { HomeScreen(nav) }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    centerVisible: Boolean,
    centerIcon: ImageVector,
    onCenterClick: () -> Unit
) {
    val items = listOf(
        NavItem("Historial", Icons.Filled.History, Routes.HISTORY),
        NavItem("Números", Icons.Filled.Shield, Routes.NUMBERS),
        NavItem("Prefijos", Icons.AutoMirrored.Filled.List, Routes.PREFIXES),
        NavItem("Ajustes", Icons.Filled.Settings, Routes.SETTINGS)
    )

    val navSurfaceColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)

    Box {
        Surface(
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            color = navSurfaceColor,
            modifier = Modifier
                .fillMaxWidth()
//                .height(64.dp)
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.take(2).forEach { item ->
                    val selected = currentRoute == item.route
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .clickable { onNavigate(item.route) }
                            .padding(vertical = 2.dp)
                    ) {
                        val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = color,
                            modifier = Modifier.size(26.dp)
                        )
                        Text(
                            text = item.label,
                            color = color,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(0.5f))

                items.drop(2).forEach { item ->
                    val selected = currentRoute == item.route
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .clickable { onNavigate(item.route) }
                            .padding(vertical = 2.dp)
                    ) {
                        val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = color,
                            modifier = Modifier.size(26.dp)
                        )
                        Text(
                            text = item.label,
                            color = color,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-24).dp)
                .alpha(if (centerVisible) 1f else 0f)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(navSurfaceColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clickable { if (centerVisible) onCenterClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Crossfade(targetState = centerIcon, label = "centerFabIcon") { icn ->
                            Icon(
                                icn,
                                contentDescription = "Center action",
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(nav: NavHostController) {
    val strings = LocalStrings.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Iog26Logo() },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
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
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = strings.homeTitle,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Text(
                text = strings.homeSubtitle,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            AnimatedVisibility(visible = true) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ModernButton(strings.homeCtaSettings, Icons.Filled.Settings) { nav.navigate(Routes.SETTINGS) }
                    ModernButton(strings.homeCtaNumbers, Icons.Filled.Shield) { nav.navigate(Routes.NUMBERS) }
                    ModernButton(strings.homeCtaPrefixes, Icons.AutoMirrored.Filled.List) { nav.navigate(Routes.PREFIXES) }
                    ModernButton(strings.homeCtaHistory, Icons.Filled.History) { nav.navigate(Routes.HISTORY) }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = strings.homeFooter,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun Iog26Logo(
    fontSize: Int = 22,
    neutralColor: Color = MaterialTheme.colorScheme.onSurface,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    Text(
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = neutralColor)) { append("i") }
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = accentColor)) { append("OG") }
            withStyle(SpanStyle(fontWeight = FontWeight.Medium, color = neutralColor)) { append("26") }
        },
        fontSize = fontSize.sp
    )
}

@Composable
private fun ModernButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 3.dp, pressedElevation = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(22.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}
