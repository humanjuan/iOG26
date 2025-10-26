package com.humanjuan.iog26.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

data class Privacy(
    val title: String,
    val generalInfoTitle: String,
    val generalInfoText: String,
    val dataCollectedTitle: String,
    val dataCollectedText: String,
    val dataCollectedItemA: String,
    val dataCollectedItemB: String,
    val dataCollectedEndText: String,

    val useDataTitle: String,
    val useDataText: String,
    val useDataItemA: String,
    val useDataItemB: String,

    val devicePermissionsTitle: String,
    val devicePermissionsText: String,
    val devicePermissionsItemA: String,
    val devicePermissionsItemB: String,
    val devicePermissionsItemC: String,
    val devicePermissionsEndText: String,

    val securityTitle: String,
    val securityText: String,
    val contactTitle: String,
    val contactText: String,

    val close: String

)

val PrivacyEs = Privacy(
    title = "Política de Privacidad: iOG26 Call Filter",
    generalInfoTitle = "1. Información General",
    generalInfoText = "La aplicación iOG26, desarrollada por HumanJuan, tiene como propósito ofrecer funciones de filtrado y bloqueo de llamadas no deseadas en dispositivos Android. Nos comprometemos a proteger la privacidad de los usuarios y a cumplir con las políticas de Google Play y las leyes de protección de datos aplicables.",
    dataCollectedTitle = "2. Datos recopilados",
    dataCollectedText = "iOG26 no recopila, almacena ni comparte información personal. La aplicación opera completamente en el dispositivo del usuario. Solo accede temporalmente a la información necesaria para cumplir su función principal:",
    dataCollectedItemA = "Estado de llamadas (para detectar llamadas entrantes o salientes).",
    dataCollectedItemB = "Número de teléfono de llamadas desconocidas (para aplicar las reglas de bloqueo).",
    dataCollectedEndText = "Esta información no se envía ni se guarda en servidores externos.",
    useDataTitle = "3. Uso de los datos",
    useDataText = "Los datos se usan únicamente de forma local para:",
    useDataItemA = "Determinar si una llamada debe ser permitida o bloqueada.",
    useDataItemB = "Mostrar notificaciones locales sobre el estado del filtro.",
    devicePermissionsTitle = "4. Permisos del dispositivo",
    devicePermissionsText = "La aplicación puede solicitar permisos como:",
    devicePermissionsItemA = "READ_PHONE_STATE",
    devicePermissionsItemB = "CALL_SCREENING_SERVICE",
    devicePermissionsItemC = "POST_NOTIFICATIONS",
    devicePermissionsEndText = "Estos permisos se utilizan exclusivamente para ejecutar las funciones descritas. No se utilizan para recopilar datos personales ni realizar seguimiento del usuario.",
    securityTitle = "5. Seguridad",
    securityText = "Todos los procesos ocurren en el dispositivo. No se transmiten datos fuera del teléfono ni a terceros.",
    contactTitle = "6. Contacto",
    contactText = "Si tienes preguntas o inquietudes sobre esta política, puedes contactarme en: juan.alejandro@humanjuan.com",
    close = "Cerrar",
)

val PrivacyEn = Privacy(
    title = "Privacy Policy: iOG26 Call Filter",
    generalInfoTitle = "1. General Information",
    generalInfoText = "The iOG26 application, developed by HumanJuan, is designed to provide call filtering and blocking features on Android devices. We are committed to protecting user privacy and complying with Google Play policies and applicable data protection laws.",
    dataCollectedTitle = "2. Data Collected",
    dataCollectedText = "iOG26 does not collect, store, or share personal information. The app operates entirely on the user's device. It only accesses, temporarily and locally, the information needed to perform its core function:",
    dataCollectedItemA = "Call state (to detect incoming or outgoing calls).",
    dataCollectedItemB = "Phone number of unknown calls (to apply blocking rules).",
    dataCollectedEndText = "This information is not sent to nor stored on external servers.",
    useDataTitle = "3. Use of Data",
    useDataText = "Data is used only locally to:",
    useDataItemA = "Determine whether a call should be allowed or blocked.",
    useDataItemB = "Display local notifications about the filter status.",
    devicePermissionsTitle = "4. Device Permissions",
    devicePermissionsText = "The app may request permissions such as:",
    devicePermissionsItemA = "READ_PHONE_STATE",
    devicePermissionsItemB = "CALL_SCREENING_SERVICE",
    devicePermissionsItemC = "POST_NOTIFICATIONS",
    devicePermissionsEndText = "These permissions are used exclusively to perform the described functions. They are not used to collect personal data or track the user.",
    securityTitle = "5. Security",
    securityText = "All processes occur on the device. Data is not transmitted outside the phone nor to third parties.",
    contactTitle = "6. Contact",
    contactText = "If you have questions or concerns about this policy, you can contact me at: juan.alejandro@humanjuan.com",
    close = "Close",
)

val PrivacyIt = Privacy(
    title = "Informativa sulla Privacy: iOG26 Call Filter",
    generalInfoTitle = "1. Informazioni Generali",
    generalInfoText = "L’applicazione iOG26, sviluppata da HumanJuan, ha lo scopo di offrire funzioni di filtro e blocco delle chiamate indesiderate sui dispositivi Android. Ci impegniamo a proteggere la privacy degli utenti e a rispettare le politiche di Google Play e le leggi applicabili sulla protezione dei dati.",
    dataCollectedTitle = "2. Dati Raccolti",
    dataCollectedText = "iOG26 non raccoglie, non archivia e non condivide informazioni personali. L’applicazione funziona interamente sul dispositivo dell’utente. Accede solo temporaneamente alle informazioni necessarie per eseguire la sua funzione principale:",
    dataCollectedItemA = "Stato delle chiamate (per rilevare chiamate in entrata o in uscita).",
    dataCollectedItemB = "Numero di telefono delle chiamate sconosciute (per applicare le regole di blocco).",
    dataCollectedEndText = "Queste informazioni non vengono inviate né memorizzate su server esterni.",
    useDataTitle = "3. Uso dei Dati",
    useDataText = "I dati vengono utilizzati esclusivamente in locale per:",
    useDataItemA = "Determinare se una chiamata deve essere consentita o bloccata.",
    useDataItemB = "Mostrare notifiche locali sullo stato del filtro.",
    devicePermissionsTitle = "4. Permessi del Dispositivo",
    devicePermissionsText = "L’app può richiedere permessi come:",
    devicePermissionsItemA = "READ_PHONE_STATE",
    devicePermissionsItemB = "CALL_SCREENING_SERVICE",
    devicePermissionsItemC = "POST_NOTIFICATIONS",
    devicePermissionsEndText = "Questi permessi vengono utilizzati esclusivamente per eseguire le funzioni descritte. Non vengono utilizzati per raccogliere dati personali né per tracciare l’utente.",
    securityTitle = "5. Sicurezza",
    securityText = "Tutti i processi avvengono sul dispositivo. I dati non vengono trasmessi al di fuori del telefono né a terze parti.",
    contactTitle = "6. Contatto",
    contactText = "Se hai domande o dubbi riguardo a questa informativa, puoi contattarmi all’indirizzo: juan.alejandro@humanjuan.com",
    close = "Chiudi",
)


val LocalPrivacy = staticCompositionLocalOf { PrivacyEs }

@Composable
fun ProvidePrivacy(language: String, content: @Composable () -> Unit) {
    val privacy = when (language.uppercase()) {
        "EN" -> PrivacyEn
        "IT" -> PrivacyIt
        else -> PrivacyEs
    }
    CompositionLocalProvider(LocalPrivacy provides privacy) {
        content()
    }
}
