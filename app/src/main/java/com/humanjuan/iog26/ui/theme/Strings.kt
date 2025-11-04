package com.humanjuan.iog26.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider
//import androidx.compose.ui.input.pointer.HistoricalChange

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
    // New split toggles
    val blockAnonymous: String,
    val blockAnonymousSub: String,
    val blockUnknownContacts: String,
    val blockUnknownContactsSub: String,
    val skipCallLog: String,
    val skipCallLogSub: String,
    val skipNotif: String,
    val skipNotifSub: String,

    // Settings intro/UX
    val settingsIntro: String,

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
    val change: String,

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
    val languageIt: String,
    val themeGreen: String,
    val themeNavy: String,
    val themeSunset: String,
    val themeViolet: String,
    val themeIOG26: String,
    val themeRose: String,

    // Theme mode (System/Light/Dark)
    val themeModeLabel: String = "Mode",
    val themeModeSystem: String = "System default",
    val themeModeLight: String = "Light",
    val themeModeDark: String = "Dark",

    // Regex dev mode and labels
    val regexLabel: String = "Regex (optional)",
    val regexInvalidMessage: String = "Invalid regex",
    val devRegexOn: String = "Regex mode enabled",
    val devRegexOff: String = "Regex mode disabled",
    val regexRulesTitle: String = "Regex rules",
    val editRegexTitle: String = "Edit regex",

    val blockedOnTemplate: String,
    val totalTemplate: String,
    val deletedNumberTemplate: String,

    // History screen labels
    val historySummaryTitle: String,
    val historySummarySubtitle: String,
    val recentEvents: String,
    val chartBlocksPerDay: String,
    val unknownCaller: String,
    val metricsTotal: String,
    val metricsAvgPerDay: String,
    val metricsTotalCountries: String,
    val metricsLast: String,
    val metricBlockNumbers: String,
    val metricBlockPrefixes: String,

    // History extras (charts)
    val chartByCallerType: String,
    val knownCaller: String,
    // caller breakdown labels
    val anonymousCaller: String,
    val unknownContactsLabel: String,
    // Top countries card title
    val topCountries: String,

    // Quick filter buttons labels
    val filterToday: String,
    val filter7d: String,
    val filter15d: String,
    val filter30d: String,

    // Settings snackbars/messages
    val snackAnonymousOn: String,
    val snackAnonymousOff: String,
    val snackUnknownContactsOn: String,
    val snackUnknownContactsOff: String,
    val snackLogOn: String,
    val snackLogOff: String,
    val snackNotifyOn: String,
    val snackNotifyOff: String,
    val snackDigestOn: String,
    val snackDigestOff: String,
    val snackDigestRequiresO: String,
    val snackDigestTimeSet: String,

    // Settings: System info
    val systemInfoTitle: String,
    val systemAppVersion: String,
    val systemKotlinVersion: String,
    val systemAndroidVersion: String,
    val systemDevice: String,
    val systemLibraries: String,

    // Privacy policy
    val privacyTitle: String,
    val privacyOpenLabel: String,

    // AppBar / Navigation
    val navHistory: String = "History",
    val navNumbers: String = "Numbers",
    val navPrefixes: String = "Prefixes",
    val navSettings: String = "Settings",
    val cdOpenPrivacy: String = "Open privacy policy",
    val cdCenterAdd: String = "Add",
    val cdCenterRefresh: String = "Refresh",

    // App dedication message
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
    blockAnonymous = "Bloquear llamadas anónimas",
    blockAnonymousSub = "Bloquea llamadas sin identificador de llamante (privadas/ocultas)",
    blockUnknownContacts = "Bloquear números desconocidos",
    blockUnknownContactsSub = "Bloquea números que no estén en tus contactos",
    skipCallLog = "Registrar llamadas bloqueadas",
    skipCallLogSub = "Crear una entrada en el historial del teléfono cuando se bloquee",
    skipNotif = "Notificación de llamadas bloqueadas",
    skipNotifSub = "Mostrar una notificación cuando se bloquee una llamada",

    // Settings intro/UX
    settingsIntro = "Personaliza tu experiencia y preferencias.",

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
    change = "Cambiar",


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
    languageIt = "Italiano",
    themeGreen = "Verde",
    themeNavy = "Navy",
    themeSunset = "Amanecer",
    themeViolet = "Violeta",
    themeIOG26 = "IOG26",
    themeRose = "Rosa",

    // Theme Mode
    themeModeLabel = "Modo",
    themeModeSystem = "Igual que el sistema",
    themeModeLight = "Claro",
    themeModeDark = "Oscuro",

    // Regex
    regexLabel = "Regex (opcional)",
    regexInvalidMessage = "Regex inválida",
    devRegexOn = "Modo Regex ACTIVADO",
    devRegexOff = "Modo Regex DESACTIVADO",
    regexRulesTitle = "Reglas Regex",
    editRegexTitle = "Editar regex",

    blockedOnTemplate = "Bloqueado el %s",
    totalTemplate = "%d Total",
    deletedNumberTemplate = "Número eliminado: %s",

    // History screen labels
    historySummaryTitle = "Resumen de bloqueos",
    historySummarySubtitle = "Estadísticas y actividad reciente (7d)",
    recentEvents = "Eventos recientes",
    chartBlocksPerDay = "Bloqueos por día",
    unknownCaller = "Desconocido",
    metricsTotal = "Total",
    metricsAvgPerDay = "Promedio/día",
    metricsTotalCountries = "Número de países",
    metricsLast = "Último",
    metricBlockNumbers = "Números bloqueados",
    metricBlockPrefixes = "Sufijos Bloqueados",

    // History extras (charts)
    chartByCallerType = "Por tipo de llamante",
    knownCaller = "Conocido",
    // labels
    anonymousCaller = "Anónimas",
    unknownContactsLabel = "Desconocidos",
    // Top countries card title
    topCountries = "Top países",

    // Quick filter buttons labels
    filterToday = "Hoy",
    filter7d = "7d",
    filter15d = "15d",
    filter30d = "30d",

    // Settings snackbars/messages
    snackAnonymousOn = "Bloqueo de llamadas anónimas ACTIVADO",
    snackAnonymousOff = "Bloqueo de llamadas anónimas DESACTIVADO",
    snackUnknownContactsOn = "Bloquear números que no estén en tus contactos: ACTIVADO",
    snackUnknownContactsOff = "Bloquear números que no estén en tus contactos: DESACTIVADO",
    snackLogOn = "Registrar en historial: ACTIVADO",
    snackLogOff = "Registrar en historial: DESACTIVADO",
    snackNotifyOn = "Notificación de bloqueos: ACTIVADA",
    snackNotifyOff = "Notificación de bloqueos: DESACTIVADA",
    snackDigestOn = "Resumen diario ACTIVADO",
    snackDigestOff = "Resumen diario DESACTIVADO",
    snackDigestRequiresO = "El resumen diario requiere Android 8.0+ (no se puede programar en este dispositivo)",
    snackDigestTimeSet = "Hora del resumen: %02d:%02d",

    // Settings: System info
    systemInfoTitle = "Información del sistema",
    systemAppVersion = "Versión de la app",
    systemKotlinVersion = "Versión de Kotlin",
    systemAndroidVersion = "Versión de Android",
    systemDevice = "Dispositivo",
    systemLibraries = "Librerías",

    // Privacy policy
    privacyTitle = "Políticas de privacidad",
    privacyOpenLabel = "Ver políticas de privacidad",

    // Bottom nav and content descriptions
    navHistory = "Historial",
    navNumbers = "Números",
    navPrefixes = "Prefijos",
    navSettings = "Ajustes",
    cdOpenPrivacy = "Abrir políticas de privacidad",
    cdCenterAdd = "Agregar",
    cdCenterRefresh = "Actualizar",

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
    blockAnonymous = "Block anonymous/private",
    blockAnonymousSub = "Block calls without caller ID (private/hidden)",
    blockUnknownContacts = "Block unknown numbers",
    blockUnknownContactsSub = "Block numbers not in your contacts",
    skipCallLog = "Log blocked calls",
    skipCallLogSub = "Create a phone call log entry for blocked calls",
    skipNotif = "Notification for blocked calls",
    skipNotifSub = "Show a notification when a call is blocked",

    // Settings intro/UX
    settingsIntro = "Customize your experience and preferences.",

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
    change = "Change",

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
    languageIt = "Italian",
    themeGreen = "Green",
    themeNavy = "Navy",
    themeSunset = "Sunset",
    themeViolet = "Violet",
    themeIOG26 = "IOG26",
    themeRose = "Rose",

    // Theme Mode
    themeModeLabel = "Mode",
    themeModeSystem = "System default",
    themeModeLight = "Light",
    themeModeDark = "Dark",

    // Regex
    regexLabel = "Regex (optional)",
    regexInvalidMessage = "Invalid regex",
    devRegexOn = "Regex mode ENABLED",
    devRegexOff = "Regex mode DISABLED",
    regexRulesTitle = "Regex rules",
    editRegexTitle = "Edit regex",

    blockedOnTemplate = "Blocked on %s",
    totalTemplate = "Total: %d",
    deletedNumberTemplate = "Number deleted: %s",

    // History screen labels
    historySummaryTitle = "Block summary",
    historySummarySubtitle = "Statistics and recent activity (7d)",
    recentEvents = "Recent events",
    chartBlocksPerDay = "Blocks per day",
    unknownCaller = "Unknown",
    metricsTotal = "Total",
    metricsAvgPerDay = "Avg/day",
    metricsTotalCountries = "Number of countries",
    metricsLast = "Last",
    metricBlockNumbers = "Blocked numbers",
    metricBlockPrefixes = "Blocked prefixes",

    // History extras (charts)
    chartByCallerType = "By caller type",
    knownCaller = "Known",
    // labels
    anonymousCaller = "Anonymous",
    unknownContactsLabel = "Unknown",
    // Top countries card title
    topCountries = "Top countries",

    // Quick filter buttons labels
    filterToday = "Today",
    filter7d = "7d",
    filter15d = "15d",
    filter30d = "30d",

    // Settings snackbars/messages
    snackAnonymousOn = "Anonymous call blocking ENABLED",
    snackAnonymousOff = "Anonymous call blocking DISABLED",
    snackUnknownContactsOn = "Block numbers not in contacts: ENABLED",
    snackUnknownContactsOff = "Block numbers not in contacts: DISABLED",
    snackLogOn = "Log to call history: ENABLED",
    snackLogOff = "Log to call history: DISABLED",
    snackNotifyOn = "Block notifications: ENABLED",
    snackNotifyOff = "Block notifications: DISABLED",
    snackDigestOn = "Daily summary ENABLED",
    snackDigestOff = "Daily summary DISABLED",
    snackDigestRequiresO = "Daily summary requires Android 8.0+ (cannot schedule on this device)",
    snackDigestTimeSet = "Summary time: %02d:%02d",

    // Settings: System info
    systemInfoTitle = "System info",
    systemAppVersion = "App version",
    systemKotlinVersion = "Kotlin version",
    systemAndroidVersion = "Android version",
    systemDevice = "Device",
    systemLibraries = "Libraries",

    // Privacy policy
    privacyTitle = "Privacy policy",
    privacyOpenLabel = "View privacy policy",

    // Bottom nav and content descriptions
    navHistory = "History",
    navNumbers = "Numbers",
    navPrefixes = "Prefixes",
    navSettings = "Settings",
    cdOpenPrivacy = "Open privacy policy",
    cdCenterAdd = "Add",
    cdCenterRefresh = "Refresh",

    dedicationMessage = "Application dedicated to my friend OG, so he can also enjoy on Android those magical features that Apple perfected long ago."
)

val StringsIt = Strings(
    appTitle = "iOG26",
    homeTitle = "Filtro chiamate",
    homeSubtitle = "Gestisci le regole di blocco e rivedi la cronologia delle chiamate filtrate.",
    homeCtaSettings = "Impostazioni",
    homeCtaNumbers = "Numeri bloccati",
    homeCtaPrefixes = "Prefissi bloccati",
    homeCtaHistory = "Storico blocchi",
    homeFooter = "Proteggi la tua tranquillità filtrando le chiamate indesiderate.",

    settingsTitle = "Impostazioni",
    groupBlocking = "Blocco chiamate",
    blockUnknown = "Blocca sconosciuti/privati",
    blockUnknownSub = "Silenzia o filtra le chiamate senza ID chiamante",
    blockAnonymous = "Blocca chiamate anonime",
    blockAnonymousSub = "Blocca chiamate senza ID chiamante (private/nascoste)",
    blockUnknownContacts = "Blocca numeri sconosciuti",
    blockUnknownContactsSub = "Blocca numeri che non sono nei tuoi contatti",
    skipCallLog = "Registrare chiamate bloccate",
    skipCallLogSub = "Crea una voce nel registro chiamate quando si blocca",
    skipNotif = "Notifica per chiamate bloccate",
    skipNotifSub = "Mostra una notifica quando una chiamata viene bloccata",

    // Settings intro/UX
    settingsIntro = "Personalizza la tua esperienza e preferenze.",

    groupDigest = "Riepilogo giornaliero",
    digestEnable = "Abilita riepilogo giornaliero",
    digestHint = "Il riepilogo viene riprogrammato cambiando orario o attivando l'opzione.",

    numbersTitle = "Numeri bloccati",
    searchNumberPlaceholder = "Cerca numero (+39…, 800…, ecc.)",
    addNumberTitle = "Aggiungi numero",
    addNumberLabel = "Numero (con o senza +CC)",
    addNumberHint = "Normalizzato automaticamente in formato E.164 (es: +39 3 12345678).",
    fromContacts = "Dai contatti",
    save = "Salva",
    cancel = "Annulla",
    delete = "Elimina",
    change = "Cambia",

    prefixesTitle = "Prefissi bloccati",
    searchPrefixPlaceholder = "Cerca (+39 800*, 800* NSN, ecc.)",
    addPrefixTitle = "Aggiungi prefisso",
    prefixLabel = "Prefisso (solo cifre)",
    countryCodeLabel = "Prefisso internazionale (opzionale)",

    historyTitle = "Storico blocchi",
    daysBack = "Giorni indietro",
    apply = "Applica",
    noRecentBlocks = "Nessun blocco recente",

    languageLabel = "Lingua",
    themeLabel = "Tema",
    languageEs = "Spagnolo",
    languageEn = "Inglese",
    languageIt = "Italiano",
    themeGreen = "Verde",
    themeNavy = "Blu Navy",
    themeSunset = "Tramonto",
    themeViolet = "Viola",
    themeIOG26 = "IOG26",
    themeRose = "Rosa",

    // Theme Mode
    themeModeLabel = "Modalità",
    themeModeSystem = "Come il sistema",
    themeModeLight = "Chiaro",
    themeModeDark = "Scuro",

    // Regex
    regexLabel = "Regex (opzionale)",
    regexInvalidMessage = "Regex non valida",
    devRegexOn = "Modalità Regex ATTIVATA",
    devRegexOff = "Modalità Regex DISATTIVATA",
    regexRulesTitle = "Regole Regex",
    editRegexTitle = "Modifica regex",

    blockedOnTemplate = "Bloccato il %s",
    totalTemplate = "Totale: %d",
    deletedNumberTemplate = "Numero eliminato: %s",

    // History screen labels
    historySummaryTitle = "Riepilogo blocchi",
    historySummarySubtitle = "Statistiche e attività recente (7d)",
    recentEvents = "Eventi recenti",
    chartBlocksPerDay = "Blocchi per giorno",
    unknownCaller = "Sconosciuto",
    metricsTotal = "Totale",
    metricsAvgPerDay = "Media/giorno",
    metricsTotalCountries = "Numero di paesi",
    metricsLast = "Ultimo",
    metricBlockNumbers = "Numeri bloccati",
    metricBlockPrefixes = "Prefissi bloccati",

    // History extras (charts)
    chartByCallerType = "Per tipo chiamante",
    knownCaller = "Conosciuto",
    // labels
    anonymousCaller = "Anonime",
    unknownContactsLabel = "Sconosciuto",
    // Top countries card title
    topCountries = "Paesi principali",

    // Quick filter buttons labels
    filterToday = "Oggi",
    filter7d = "7g",
    filter15d = "15g",
    filter30d = "30g",

    // Settings snackbars/messages
    snackAnonymousOn = "Blocco chiamate anonime ATTIVATO",
    snackAnonymousOff = "Blocco chiamate anonime DISATTIVATO",
    snackUnknownContactsOn = "Blocca numeri non nei contatti: ATTIVATO",
    snackUnknownContactsOff = "Blocca numeri non nei contatti: DISATTIVATO",
    snackLogOn = "Registrazione nel registro chiamate: ATTIVATA",
    snackLogOff = "Registrazione nel registro chiamate: DISATTIVATA",
    snackNotifyOn = "Notifiche di blocco: ATTIVATE",
    snackNotifyOff = "Notifiche di blocco: DISATTIVATE",
    snackDigestOn = "Riepilogo giornaliero ATTIVATO",
    snackDigestOff = "Riepilogo giornaliero DISATTIVATO",
    snackDigestRequiresO = "Il riepilogo giornaliero richiede Android 8.0+ (non programmabile su questo dispositivo)",
    snackDigestTimeSet = "Ora del riepilogo: %02d:%02d",

    // Settings: System info
    systemInfoTitle = "Info di sistema",
    systemAppVersion = "Versione app",
    systemKotlinVersion = "Versione Kotlin",
    systemAndroidVersion = "Versione Android",
    systemDevice = "Dispositivo",
    systemLibraries = "Librerie",

    // Privacy policy
    privacyTitle = "Informativa sulla privacy",
    privacyOpenLabel = "Vedi informativa sulla privacy",

    dedicationMessage = "Applicazione dedicata al mio amico OG, così può godersi anche su Android quelle funzioni magiche che Apple ha perfezionato da tempo."
)

val LocalStrings = staticCompositionLocalOf { StringsEs }

@Composable
fun ProvideStrings(language: String, content: @Composable () -> Unit) {
    val strings = when (language.uppercase()) {
        "EN" -> StringsEn
        "IT" -> StringsIt
        else -> StringsEs
    }
    CompositionLocalProvider(LocalStrings provides strings) {
        content()
    }
}
