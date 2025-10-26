package com.humanjuan.iog26.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humanjuan.iog26.data.BlockedEvent
import com.humanjuan.iog26.ui.EventsViewModel
import com.humanjuan.iog26.ui.theme.LocalStrings
import java.text.SimpleDateFormat
import java.util.*

private val indicatorPalette = listOf(
    Color(0xFF3B82F6),
    Color(0xFF22C55E),
    Color(0xFFF59E0B),
    Color(0xFFEF4444),
    Color(0xFFA855F7)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockedHistoryScreen(
    vm: EventsViewModel = viewModel(),
    onRegisterCentralAction: (((() -> Unit)) -> Unit)? = null
) {
    var recentDays by remember { mutableLongStateOf(7L) }
    val events by vm.rawItems.collectAsState()
    val groupedEvents by vm.groupedItems.collectAsState()
    val inventory by vm.inventory.collectAsState()
    val breakdown by vm.callerBreakdown.collectAsState()
    val countryStats by vm.countryStats.collectAsState()

    LaunchedEffect(Unit) {
        vm.load(7)
        vm.loadRecentFor(recentDays)
    }

    LaunchedEffect(recentDays) {
        onRegisterCentralAction?.invoke {
            vm.load(7)
            vm.loadRecentFor(recentDays)
        }
    }

    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.background
        )
    )

    val strings = LocalStrings.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(padding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = strings.historySummarySubtitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }


            item {
                DashboardMetrics(
                    events = events,
                    blockedNumbers = inventory.blockedNumbers,
                    blockedPrefixes = inventory.blockedPrefixes,
                    countriesCount = countryStats.size
                )
            }

            if (events.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        BlockedEventsChart(events.map { it.ts })
                        CallerTypeDonutChart(breakdown)
                    }
                }
            }

            if (countryStats.isNotEmpty()) {
                val top5 = countryStats.take(5)
                item { CountriesCard(top5) }
            }

            if (groupedEvents.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(vertical = 64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.noRecentBlocks,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            } else {
                item {
                    Text(
                        text = strings.recentEvents,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                // Quick filter buttons centered between Top countries and Recent events
                item {
                    QuickFilterRow(
                        selectedDays = recentDays,
                        onSelect = { days ->
                            recentDays = days
                            vm.loadRecentFor(days)
                        }
                    )
                }

                items(groupedEvents) { g ->
                    val number = g.number.ifBlank { strings.unknownCaller }
                    BlockedEventCard(number, g.mostRecentTimestamp, g.count)
                }
            }
        }
    }
}

@Composable
private fun DashboardMetrics(
    events: List<BlockedEvent>,
    blockedNumbers: Int,
    blockedPrefixes: Int,
    countriesCount: Int
) {
    val total = events.size
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val grouped = events.groupBy { sdf.format(Date(it.ts)) }
    val daysWithActivity = grouped.size
    val avgPerDay = if (daysWithActivity > 0) total / daysWithActivity.toFloat() else 0f
    val lastTs = events.maxByOrNull { it.ts }?.ts
    val lastEventDate = if (lastTs != null) {
        val d = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(lastTs))
        val t = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(lastTs))
        "$d\n$t"
    } else "—"
    val strings = LocalStrings.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(strings.metricsTotal, "$total", Modifier.weight(1f))
            MetricCard(strings.metricsAvgPerDay, "%.1f".format(avgPerDay), Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(strings.metricsTotalCountries, "$countriesCount", Modifier.weight(1f))
            MetricCard(strings.metricsLast, lastEventDate, Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(strings.metricBlockNumbers, "$blockedNumbers", Modifier.weight(1f))
            MetricCard(strings.metricBlockPrefixes, "$blockedPrefixes", Modifier.weight(1f))
        }
    }
}

@Composable
private fun BlockedEventCard(number: String, timestamp: Long, count: Int) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    number,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                )
                val strings = LocalStrings.current
                val dateStr = dateFmt(timestamp)
                Text(
                    strings.blockedOnTemplate.format(dateStr),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (count > 1) {
                Badge(containerColor = MaterialTheme.colorScheme.primary) {
                    Text(
                        text = "x$count",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    val numeric = value.toFloatOrNull()

    ElevatedCard(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (numeric != null) {
                val animatedValue by animateFloatAsState(
                    targetValue = numeric,
                    animationSpec = tween(durationMillis = 900)
                )
                Text(
                    if (numeric % 1 == 0f) animatedValue.toInt().toString() else "%.1f".format(animatedValue),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            } else {
                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary,
                        lineHeight = 22.sp
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            Text(
                label,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
                )
            )
        }
    }
}

@Composable
private fun BlockedEventsChart(timestamps: List<Long>) {
    val dayKeyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val xLabelFormat = SimpleDateFormat("MM-dd", Locale.getDefault())

    val grouped = timestamps.groupBy { dayKeyFormat.format(Date(it)) }

    val existingDays = grouped.keys
    if (existingDays.isEmpty()) return

    val firstKey = existingDays.minOrNull()!!
    val lastKey = existingDays.maxOrNull()!!
    val startDate = dayKeyFormat.parse(firstKey)!!
    val endDate = dayKeyFormat.parse(lastKey)!!

    val cal = Calendar.getInstance().apply { time = startDate }
    val sortedDays = mutableListOf<String>()
    while (!cal.time.after(endDate)) {
        sortedDays += dayKeyFormat.format(cal.time)
        cal.add(Calendar.DAY_OF_YEAR, 1)
    }

    val values = sortedDays.map { (grouped[it]?.size ?: 0).toFloat() }
    val maxValue = (values.maxOrNull() ?: 1f).coerceAtLeast(1f)

    val strings = LocalStrings.current
    val accent = MaterialTheme.colorScheme.primary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val divider = onSurfaceVariant.copy(alpha = 0.15f)
    val animationProgress by animateFloatAsState(targetValue = 1f, animationSpec = tween(1000))

    val tickCount = 5
    val yTicks = (0 until tickCount).map { i -> (maxValue * i / (tickCount - 1)).toInt() }


    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp, max = 260.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = strings.chartBlocksPerDay,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Eje Y
                Column(
                    modifier = Modifier
                        .width(36.dp)
                        .height(140.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    yTicks.reversed().forEach { v ->
                        Text(
                            text = v.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(color = onSurfaceVariant)
                        )
                    }
                }

                // Área del gráfico
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(140.dp)
                        .padding(start = 4.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val h = size.height
                        val w = size.width
                        val step = if (values.size <= 1) w else w / (values.size - 1)
                        val strokePx = 1.dp.toPx()

                        // Líneas horizontales
                        yTicks.forEach { v ->
                            val y = h - (v / maxValue) * h
                            drawLine(
                                color = divider,
                                start = Offset(0f, y),
                                end = Offset(w, y),
                                strokeWidth = strokePx
                            )
                        }

                        // Path de datos
                        val path = Path()
                        values.forEachIndexed { i, v ->
                            val x = i * step
                            val y = h - (v / maxValue) * h
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }

                        val fill = Path().apply {
                            addPath(path)
                            lineTo(w, h)
                            lineTo(0f, h)
                            close()
                        }

                        drawPath(
                            path = fill,
                            brush = Brush.verticalGradient(
                                listOf(accent.copy(alpha = 0.25f), Color.Transparent)
                            ),
                            alpha = animationProgress
                        )

                        drawPath(
                            path = path,
                            color = accent,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
                            alpha = animationProgress
                        )

                        // Puntos en la línea
                        values.take((values.size * animationProgress).toInt())
                            .forEachIndexed { i, v ->
                                val x = i * step
                                val y = h - (v / maxValue) * h
                                drawCircle(
                                    color = accent,
                                    radius = 4.dp.toPx(),
                                    center = Offset(x, y)
                                )
                            }
                    }
                }
            }

            // Eje X (fechas)
            Spacer(Modifier.height(8.dp))
            val maxLabels = 6
            val stride = ((sortedDays.size + maxLabels - 1) / maxLabels).coerceAtLeast(1)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                sortedDays.indices
                    .filter { it % stride == 0 || it == sortedDays.lastIndex }
                    .forEach { i ->
                        val labelDate = dayKeyFormat.parse(sortedDays[i])
                        val label =
                            labelDate?.let { xLabelFormat.format(it) } ?: sortedDays[i].substring(5)
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(color = onSurfaceVariant)
                        )
                    }
            }
        }
    }
}

@Composable
private fun CallerTypeDonutChart(breakdown: com.humanjuan.iog26.ui.CallerBreakdown) {
    val strings = LocalStrings.current
    val anonymous = breakdown.anonymous
    val unknown = breakdown.unknownContacts
    val known = breakdown.known
    val total = (anonymous + unknown + known).coerceAtLeast(1)

    val surface = MaterialTheme.colorScheme.surface
    val colors = listOf(indicatorPalette[2], indicatorPalette[0], indicatorPalette[1])

    val animatedAnon by animateFloatAsState(
        targetValue = anonymous.toFloat(),
        animationSpec = tween(1000)
    )
    val animatedUnknown by animateFloatAsState(
        targetValue = unknown.toFloat(),
        animationSpec = tween(1000)
    )
    val animatedKnown by animateFloatAsState(
        targetValue = known.toFloat(),
        animationSpec = tween(1000)
    )
    val animatedTotal by animateFloatAsState(
        targetValue = total.toFloat(),
        animationSpec = tween(800)
    )

    val sum = (anonymous + unknown + known).toFloat().coerceAtLeast(1f)
    val sweeps = listOf(
        360f * (animatedAnon / sum),
        360f * (animatedUnknown / sum),
        360f * (animatedKnown / sum)
    )

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(
                min = 180.dp,
                max = 200.dp
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Text(
                strings.chartByCallerType,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(modifier = Modifier.size(90.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        var start = -90f
                        sweeps.forEachIndexed { i, angle ->
                            drawArc(
                                color = colors[i],
                                startAngle = start,
                                sweepAngle = angle,
                                useCenter = true
                            )
                            start += angle
                        }
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(
                                    surface,
                                    surface.copy(alpha = 0.8f)
                                )
                            ),
                            radius = size.minDimension * 0.35f,
                            center = center
                        )
                    }
                    Text(
                        text = animatedTotal.toInt().toString(),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        strings.anonymousCaller to anonymous,
                        strings.unknownContactsLabel to unknown,
                        strings.knownCaller to known
                    ).forEachIndexed { i, (label, count) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(12.dp)
                                    .background(colors[i], RoundedCornerShape(2.dp))
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("$label: $count", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CountriesCard(items: List<com.humanjuan.iog26.ui.CountryStat>) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = LocalStrings.current.topCountries,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(8.dp))
            val max = (items.maxOfOrNull { it.count } ?: 1).toFloat()
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items.forEach { c ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(c.countryName, modifier = Modifier
                            .widthIn(min = 80.dp)
                            .weight(1f))
                        val ratio = (c.count / max).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .weight(2f)
                                .height(10.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                    RoundedCornerShape(50)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(ratio)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        RoundedCornerShape(50)
                                    )
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("${c.count}", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickFilterRow(selectedDays: Long, onSelect: (Long) -> Unit) {
    val strings = LocalStrings.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val options = listOf(
            0L to strings.filterToday,
            7L to strings.filter7d,
            15L to strings.filter15d,
            30L to strings.filter30d
        )
        options.forEachIndexed { index, (days, label) ->
            val selected = selectedDays == days
            AssistChip(
                onClick = { onSelect(days) },
                label = { Text(label) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
                    labelColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            )
            if (index < options.lastIndex) Spacer(Modifier.width(8.dp))
        }
    }
}

private fun dateFmt(ts: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(ts))
}