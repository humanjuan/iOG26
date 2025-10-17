package com.humanjuan.iog26.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider

// Minimal i18n without Android resources: CompositionLocal-based strings.

data class Strings(
    val appTitle: String,
    val homeTitle: String,
    val homeSubtitle: String,
    val homeCtaSettings: String,
    val homeCtaNumbers: String,
    val homeCtaPrefixes: String,
    val homeCtaHistory: String,
    val homeFooter: String,

    val settingsTitle: String,
    val groupBlocking: String,
    val blockUnknown: String,
    val blockUnknownSub: String,
    val skipCallLog: String,
    val skipCallLogSub: String,
    val skipNotif: String,
    val skipNotifSub: String,

    val groupDigest: String,
    val digestEnable: String,
    val digestHint: String,

    val numbersTitle: String,
    val searchNumberPlaceholder: String,
    val addNumberTitle: String,
    val addNumberLabel: String,
    val addNumberHint: String,
    val fromContacts: String,
    val save: String,
    val cancel: String,
    val delete: String,

    val prefixesTitle: String,
    val searchPrefixPlaceholder: String,
    val addPrefixTitle: String,
    val prefixLabel: String,
    val countryCodeLabel: String,

    val historyTitle: String,
    val daysBack: String,
    val apply: String,
    val noRecentBlocks: String,

    val languageLabel: String,
    val themeLabel: String,
    val languageEs: String,
    val languageEn: String,
    val themeGreen: String,
    val themeNavy: String,
    val themeSunset: String,
    val themeViolet: String,

    val blockedOnTemplate: String, // e.g., "Bloqueado el %s" / "Blocked on %s"
    val totalTemplate: String,     // e.g., "%d Total" / "Total: %d"
    val deletedNumberTemplate: String, // e.g., "Número eliminado: %s" / "Number deleted: %s"

    // History screen labels
    val historySummaryTitle: String,
    val historySummarySubtitle: String,
    val recentEvents: String,
    val chartBlocksPerDay: String,
    val unknownCaller: String,
    val metricsTotal: String,
    val metricsAvgPerDay: String,
    val metricsActiveDays: String,
    val metricsLast: String,

    // History extras (charts)
    val chartByCallerType: String,
    val knownCaller: String,

    // Settings: System info
    val systemInfoTitle: String,
    val systemAppVersion: String,
    val systemKotlinVersion: String,
    val systemAndroidVersion: String,
    val systemDevice: String,
    val systemLibraries: String,

    // App dedication message (about logo tap)
    val dedicationMessage: String
)

val StringsEs = Strings(
    appTitle = "iOG26",
    homeTitle = "Filtro de llamadas",
    homeSubtitle = "Administra tus reglas de bloqueo y revisa el historial de llamadas filtradas.",
    homeCtaSettings = "Ajustes",
    homeCtaNumbers = "Números bloqueados",
    homeCtaPrefixes = "Prefijos bloqueados",
    homeCtaHistory = "Historial de bloqueos",
    homeFooter = "Protege tu tranquilidad filtrando llamadas no deseadas.",

    settingsTitle = "Ajustes",
    groupBlocking = "Bloqueo de llamadas",
    blockUnknown = "Bloquear números desconocidos",
    blockUnknownSub = "Silencia o filtra llamadas sin ID u ocultas",
    skipCallLog = "No registrar llamadas bloqueadas",
    skipCallLogSub = "Evita entradas en el historial del teléfono",
    skipNotif = "Sin notificación al bloquear",
    skipNotifSub = "No mostrar aviso al bloquear llamadas",

    groupDigest = "Resumen diario",
    digestEnable = "Activar resumen de bloqueos",
    digestHint = "El resumen se reprograma automáticamente al cambiar la hora o activar la opción.",

    numbersTitle = "Números bloqueados",
    searchNumberPlaceholder = "Buscar número (+56…, 600…, etc.)",
    addNumberTitle = "Agregar número",
    addNumberLabel = "Número (con o sin +CC)",
    addNumberHint = "Se normaliza automáticamente a formato E.164 (ej: +56 9 12345678).",
    fromContacts = "Desde contactos",
    save = "Guardar",
    cancel = "Cancelar",
    delete = "Eliminar",

    prefixesTitle = "Prefijos bloqueados",
    searchPrefixPlaceholder = "Buscar (+56 800*, 800* NSN, etc.)",
    addPrefixTitle = "Agregar prefijo",
    prefixLabel = "Prefijo (solo dígitos)",
    countryCodeLabel = "Código de país (opcional)",

    historyTitle = "Historial de bloqueos",
    daysBack = "Días hacia atrás",
    apply = "Aplicar",
    noRecentBlocks = "No hay registros de bloqueos recientes",

    languageLabel = "Idioma",
    themeLabel = "Tema",
    languageEs = "Español",
    languageEn = "Inglés",
    themeGreen = "Verde",
    themeNavy = "Navy",
    themeSunset = "Amanecer",
    themeViolet = "Violeta",

    blockedOnTemplate = "Bloqueado el %s",
    totalTemplate = "%d Total",
    deletedNumberTemplate = "Número eliminado: %s",

    // History screen labels
    historySummaryTitle = "Resumen de bloqueos",
    historySummarySubtitle = "Estadísticas y actividad reciente",
    recentEvents = "Eventos recientes",
    chartBlocksPerDay = "Bloqueos por día",
    unknownCaller = "Desconocido",
    metricsTotal = "Total",
    metricsAvgPerDay = "Promedio/día",
    metricsActiveDays = "Días activos",
    metricsLast = "Último",

    // History extras (charts)
    chartByCallerType = "Por tipo de llamante",
    knownCaller = "Conocido",

    // Settings: System info
    systemInfoTitle = "Información del sistema",
    systemAppVersion = "Versión de la app",
    systemKotlinVersion = "Versión de Kotlin",
    systemAndroidVersion = "Versión de Android",
    systemDevice = "Dispositivo",
    systemLibraries = "Librerías",
    dedicationMessage = "Aplicación dedicada a mi amigo OG, para que también pueda disfrutar en Android esas funciones mágicas que Apple ya perfeccionó hace tiempo."
)

val StringsEn = Strings(
    appTitle = "iOG26",
    homeTitle = "Call filter",
    homeSubtitle = "Manage your blocking rules and review filtered call history.",
    homeCtaSettings = "Settings",
    homeCtaNumbers = "Blocked numbers",
    homeCtaPrefixes = "Blocked prefixes",
    homeCtaHistory = "Blocked history",
    homeFooter = "Protect your peace by filtering unwanted calls.",

    settingsTitle = "Settings",
    groupBlocking = "Call blocking",
    blockUnknown = "Block unknown/private",
    blockUnknownSub = "Silence or filter calls without caller ID",
    skipCallLog = "Skip call log on block",
    skipCallLogSub = "Avoid entries in phone history",
    skipNotif = "Skip notification on block",
    skipNotifSub = "Do not show a notification when blocking",

    groupDigest = "Daily digest",
    digestEnable = "Enable daily summary",
    digestHint = "The summary is rescheduled when changing time or toggling the option.",

    numbersTitle = "Blocked numbers",
    searchNumberPlaceholder = "Search number (+1…, 800…, etc.)",
    addNumberTitle = "Add number",
    addNumberLabel = "Number (with or without +CC)",
    addNumberHint = "Automatically normalized to E.164 format (e.g., +1 234 567 8901).",
    fromContacts = "From contacts",
    save = "Save",
    cancel = "Cancel",
    delete = "Delete",

    prefixesTitle = "Blocked prefixes",
    searchPrefixPlaceholder = "Search (+1 800*, 800* NSN, etc.)",
    addPrefixTitle = "Add prefix",
    prefixLabel = "Prefix (digits only)",
    countryCodeLabel = "Country code (optional)",

    historyTitle = "Blocked history",
    daysBack = "Days back",
    apply = "Apply",
    noRecentBlocks = "No recent block records",

    languageLabel = "Language",
    themeLabel = "Theme",
    languageEs = "Spanish",
    languageEn = "English",
    themeGreen = "Green",
    themeNavy = "Navy",
    themeSunset = "Sunset",
    themeViolet = "Violet",

    blockedOnTemplate = "Blocked on %s",
    totalTemplate = "Total: %d",
    deletedNumberTemplate = "Number deleted: %s",

    // History screen labels
    historySummaryTitle = "Block summary",
    historySummarySubtitle = "Statistics and recent activity",
    recentEvents = "Recent events",
    chartBlocksPerDay = "Blocks per day",
    unknownCaller = "Unknown",
    metricsTotal = "Total",
    metricsAvgPerDay = "Avg/day",
    metricsActiveDays = "Active days",
    metricsLast = "Last",

    // History extras (charts)
    chartByCallerType = "By caller type",
    knownCaller = "Known",

    // Settings: System info
    systemInfoTitle = "System info",
    systemAppVersion = "App version",
    systemKotlinVersion = "Kotlin version",
    systemAndroidVersion = "Android version",
    systemDevice = "Device",
    systemLibraries = "Libraries",
    dedicationMessage = "Application dedicated to my friend OG, so he can also enjoy on Android those magical features that Apple perfected long ago."
)

val LocalStrings = staticCompositionLocalOf { StringsEs }

@Composable
fun ProvideStrings(language: String, content: @Composable () -> Unit) {
    val strings = if (language.uppercase() == "EN") StringsEn else StringsEs
    CompositionLocalProvider(LocalStrings provides strings) {
        content()
    }
}
