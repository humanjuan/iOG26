import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.humanjuan.iog26"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.humanjuan.iog26"
        minSdk = 30
        targetSdk = 36
        versionCode = 15
        versionName = "1.3.5"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // BuildConfig fields for System Info section
        buildConfigField("String", "APP_VERSION", "\"" + versionName + "\"")
        // Leer versiones desde el Version Catalog para evitar desincronizaciones
        buildConfigField("String", "ROOM_VERSION", "\"${libs.versions.room.get()}\"")
        buildConfigField("String", "WORK_VERSION", "\"${libs.versions.work.get()}\"")
        buildConfigField("String", "DATASTORE_VERSION", "\"${libs.versions.datastore.get()}\"")
        buildConfigField("String", "LIBPHONENUMBER_VERSION", "\"${libs.versions.libphonenumber.get()}\"")
        buildConfigField("String", "MATERIAL3_VERSION", "\"${libs.versions.material3.get()}\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Exponer los esquemas para pruebas de migración (androidTest)
    sourceSets["androidTest"].assets.srcDir(file("schemas"))
}

// Exportar esquemas de Room a app/schemas usando KSP args
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.generateKotlin", "true")
}

dependencies {
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Compose
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.runtime)

    // Core / Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Room + KSP
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Otros
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.libphonenumber)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.coil.compose)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
