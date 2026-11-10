plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.access_tracker"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.access_tracker"
        minSdk = 30
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        // URL de la API. Emulador: 10.0.2.2 es tu PC. Telefono fisico en la misma red:
        // agrega  apiBaseUrl=http://IP-DE-TU-PC:8000  en gradle.properties (o ~/.gradle/gradle.properties).
        debug {
            val url = providers.gradleProperty("apiBaseUrl").getOrElse("http://10.0.2.2:8000")
            buildConfigField("String", "API_BASE_URL", "\"$url\"")
        }
        release {
            // Debe ser HTTPS (URL del tunel de Cloudflare). Se pasa con -PapiBaseUrl=...
            val url = providers.gradleProperty("apiBaseUrl").getOrElse("https://CAMBIAR-url-del-tunel.example.com")
            buildConfigField("String", "API_BASE_URL", "\"$url\"")
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}