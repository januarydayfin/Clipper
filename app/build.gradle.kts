plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "2.3.2"
    id("org.jetbrains.kotlin.plugin.compose")
    alias(libs.plugins.kotlinx.serialization)

}

android {
    namespace = "com.krayapp.buffercompanion.bargen"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.krayapp.buffercompanion.bargen"
        minSdk = 30
        targetSdk = 36
        versionCode = 322
        versionName = "3.2.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    buildFeatures {
        viewBinding = true
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // --- Основные зависимости (implementation) ---
    implementation(libs.android.splashscreen)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.serialization.json)

    // Core/UI
    implementation(libs.bundles.core) // Включает core-ktx, appcompat, recyclerview, material

    implementation(libs.bundles.koin)
    // Navigation
    implementation(libs.bundles.navigation)

    implementation(libs.bundles.orbit)

    // Room
    implementation(libs.bundles.room) // Включает room-runtime и room-ktx

    // Compose (Material3, UI, Activity, ViewModel)
    implementation(libs.bundles.compose)

    implementation(libs.androidx.datastore.preferences)

    // Сторонние
    implementation(libs.reorderable)
    implementation(libs.google.play.review)
    implementation(libs.zxing.android.embedded)
    implementation(libs.tedpermission.normal)
    implementation(libs.skydoves.colorpickerview)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.paging.common.android)

    // --- Процессоры аннотаций/KSP ---
    // Room compiler используется для обоих
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)

    // --- Debug ---
    debugImplementation(libs.androidx.compose.ui.tooling)

    // --- Тестирование ---
    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}