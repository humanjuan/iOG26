package com.humanjuan.iog26.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humanjuan.iog26.data.BlockedEvent
import com.humanjuan.iog26.ui.EventsViewModel
import com.humanjuan.iog26.ui.theme.LocalStrings
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockedHistoryScreen(
    vm: EventsViewModel = viewModel(),
    onOpenMenu: () -> Unit = {}
) {
    var daysInput by remember { mutableStateOf(TextFieldValue("7")) }
    val events by vm.rawItems.collectAsState()
    val groupedEvents by vm.groupedItems.collectAsState()

    LaunchedEffect(Unit) { vm.load(7) }

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
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = daysInput,
                        onValueChange = { v ->
                            val original = v.text
                            val cursor = v.selection.end.coerceIn(0, original.length)
                            val cleaned = original.filter { it.isDigit() }
                            var removedBefore = 0
                            for (i in 0 until cursor) {
                                if (i < original.length && !original[i].isDigit()) removedBefore++
                            }
                            val newCursor = (cursor - removedBefore).coerceIn(0, cleaned.length)
                            daysInput = TextFieldValue(cleaned, selection = androidx.compose.ui.text.TextRange(newCursor))
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(50)),
                        placeholder = { Text(strings.daysBack, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        shape = RoundedCornerShape(50),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val presets = listOf(7L, 14L, 30L)
                        presets.forEach { d ->
                            val selected = daysInput.text.toLongOrNull() == d
                            AssistChip(
                                onClick = {
                                    daysInput = TextFieldValue(d.toString())
                                    vm.load(d)
                                },
                                label = { Text(d.toString()) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    else MaterialTheme.colorScheme.surface,
                                    labelColor = if (selected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = {
                            val d = daysInput.text.toLongOrNull() ?: 0
                            vm.load(d)
                        }) { Text(strings.apply) }
                    }
                }
            }

            item {
                DashboardMetrics(events = events)
            }

            if (events.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        BlockedEventsChart(events.map { it.ts })
                        CallerTypeDonutChart(events)
                    }
                }
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

                items(groupedEvents) { g ->
                    val number = g.number ?: strings.unknownCaller
                    BlockedEventCard(number, g.mostRecentTimestamp, g.count)
                }
            }
        }
    }
}

@Composable
private fun DashboardMetrics(events: List<BlockedEvent>) {
    val total = events.size
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val grouped = events.groupBy { sdf.format(Date(it.ts)) }
    val daysWithActivity = grouped.size
    val avgPerDay = if (daysWithActivity > 0) total / daysWithActivity.toFloat() else 0f
    val lastEventDate = events.maxByOrNull { it.ts }?.ts?.let { dateFmt(it) } ?: "—"

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
            MetricCard(strings.metricsActiveDays, "$daysWithActivity", Modifier.weight(1f))
            MetricCard(strings.metricsLast, lastEventDate, Modifier.weight(1f))
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
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
    val sortedDays: List<String>
    val values: List<Float>
    if (existingDays.isEmpty()) {
        sortedDays = emptyList()
        values = emptyList()
    } else {
        val firstKey = existingDays.minOrNull()!!
        val lastKey = existingDays.maxOrNull()!!
        val startDate = dayKeyFormat.parse(firstKey)!!
        val endDate = dayKeyFormat.parse(lastKey)!!
        val cal = java.util.Calendar.getInstance().apply { time = startDate }
        val fullDays = mutableListOf<String>()
        while (!cal.time.after(endDate)) {
            fullDays += dayKeyFormat.format(cal.time)
            cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }
        sortedDays = fullDays
        values = sortedDays.map { (grouped[it]?.size ?: 0).toFloat() }
    }

    val maxValueRaw = values.maxOrNull() ?: 0f
    val maxValue = if (maxValueRaw <= 0f) 1f else maxValueRaw

    val strings = LocalStrings.current
    val primary = MaterialTheme.colorScheme.primary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val divider = MaterialTheme.colorScheme.outlineVariant ?: onSurfaceVariant.copy(alpha = 0.25f)

    val tickCount = 5
    val yTicks = (0 until tickCount).map { i ->
        (maxValue * i / (tickCount - 1)).toInt()
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp, max = 260.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = strings.chartBlocksPerDay,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))

            if (sortedDays.isEmpty()) {
                Text(
                    text = strings.noRecentBlocks,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            } else {
                val yGutter = 40.dp
                val chartHeight = 120.dp

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Column(
                        modifier = Modifier
                            .width(yGutter)
                            .height(chartHeight),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        yTicks.reversed().forEach { v ->
                            Text(
                                text = v.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(color = onSurfaceVariant)
                            )
                        }
                    }

                    Box(modifier = Modifier
                        .weight(1f)
                        .height(chartHeight)
                        .padding(start = 4.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val h = size.height
                            val w = size.width
                            val strokePx = 1.dp.toPx()
                            yTicks.forEach { v ->
                                val y = h - (v / maxValue) * h
                                drawLine(
                                    color = divider,
                                    start = Offset(0f, y),
                                    end = Offset(w, y),
                                    strokeWidth = strokePx
                                )
                            }
                            drawLine(color = onSurfaceVariant.copy(alpha = 0.6f), start = Offset(0f, 0f), end = Offset(0f, h), strokeWidth = strokePx)
                            drawLine(color = onSurfaceVariant.copy(alpha = 0.6f), start = Offset(0f, h), end = Offset(w, h), strokeWidth = strokePx)

                            val widthStep = if (values.size <= 1) w else w / (values.size - 1)
                            val path = Path()
                            values.forEachIndexed { index, value ->
                                val x = index * widthStep
                                val y = h - (value / maxValue) * h
                                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            }
                            val fillPath = Path().apply {
                                addPath(path)
                                lineTo(w, h)
                                lineTo(0f, h)
                                close()
                            }
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(primary.copy(alpha = 0.22f), Color.Transparent)
                                )
                            )
                            drawPath(
                                path = path,
                                color = primary,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                    }
                }

                val maxLabels = 6
                val stride = ((sortedDays.size + maxLabels - 1) / maxLabels).coerceAtLeast(1)
                Spacer(Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    sortedDays.indices.filter { it % stride == 0 || it == sortedDays.lastIndex }.forEach { i ->
                        val dayStr = sortedDays[i]
                        val date = try { dayKeyFormat.parse(dayStr) } catch (_: Throwable) { null }
                        val label = date?.let { xLabelFormat.format(it) } ?: dayStr.substring(5)
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(color = onSurfaceVariant)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CallerTypeDonutChart(events: List<BlockedEvent>) {
    val strings = LocalStrings.current
    val known = events.count { it.e164 != null }
    val unknown = events.size - known
    val total = kotlin.math.max(known + unknown, 1)
    val knownPct = (known.toFloat() / total.toFloat())

    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val surface = MaterialTheme.colorScheme.surface

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp, max = 200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = strings.chartByCallerType,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                val donutSize = 80.dp
                Box(modifier = Modifier.size(donutSize)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val sweepKnown = 360f * knownPct
                        drawArc(
                            color = primary,
                            startAngle = -90f,
                            sweepAngle = sweepKnown,
                            useCenter = true
                        )
                        drawArc(
                            color = tertiary,
                            startAngle = -90f + sweepKnown,
                            sweepAngle = 360f - sweepKnown,
                            useCenter = true
                        )
                        val holeRadius = size.minDimension * 0.35f
                        drawCircle(color = surface, radius = holeRadius, center = center)
                    }
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "${(knownPct * 100).toInt()}%",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(12.dp)
                                .background(primary, RoundedCornerShape(2.dp))
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("${strings.knownCaller}: $known")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(12.dp)
                                .background(tertiary, RoundedCornerShape(2.dp))
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("${strings.unknownCaller}: $unknown")
                    }
                }
            }
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

private fun dateFmt(ts: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(ts))
}
