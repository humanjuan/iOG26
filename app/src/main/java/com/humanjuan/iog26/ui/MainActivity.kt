package com.humanjuan.iog26.ui

import android.Manifest
import android.app.role.RoleManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.humanjuan.iog26.ui.theme.AppThemeOption
import com.humanjuan.iog26.ui.theme.IOG26Theme
import com.humanjuan.iog26.ui.theme.ProvidePrivacy
import com.humanjuan.iog26.ui.theme.ProvideStrings

class MainActivity : ComponentActivity() {

    private val requestContacts = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    private val requestNotifications = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    private val requestRole = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            if (roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) &&
                !roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
                requestRole.launch(roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING))
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) requestContacts.launch(Manifest.permission.READ_CONTACTS)

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) requestNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)

        setContent {
            val prefsVm: AppPrefsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            val prefs = prefsVm.prefs.collectAsState()
            val theme = when (prefs.value.theme.uppercase()) {
                "NAVY" -> AppThemeOption.NAVY
                "SUNSET" -> AppThemeOption.SUNSET
                "VIOLET" -> AppThemeOption.VIOLET
                "IOG26" -> AppThemeOption.IOG26
                "ROSE" -> AppThemeOption.ROSE
                else -> AppThemeOption.IOG26
            }
            val isDark = when (prefs.value.themeMode.uppercase()) {
                "DARK" -> true
                "LIGHT" -> false
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            IOG26Theme(appTheme = theme, darkTheme = isDark) {
                ProvideStrings(language = prefs.value.language) {
                    ProvidePrivacy(language = prefs.value.language) {
                        AppNav(
                        )
                    }
                }
            }
        }
    }
}
