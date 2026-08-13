plugins {
    alias(libs.plugins.android.application)

    //alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    //alias(libs.plugins.ksp)
    id("com.google.devtools.ksp")

}

android {
    namespace = "com.cityair"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.cityair"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", "\"https://api.waqi.info/feed/\"")
        buildConfigField("String", "API_KEY", "\"88503d06b3f0d48fab82af922227d1d1741ff694\"")
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
            buildConfigField("String", "BASE_URL", "\"https://api.waqi.info/feed/\"")
            buildConfigField("String", "API_KEY", "\"88503d06b3f0d48fab82af922227d1d1741ff694\"")
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
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)



    // Standard Android dependencies...
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")

    // ADD RETROFIT HERE:


    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    // Add this line to enable viewModel() inside Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2")
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.5.2")
    implementation("androidx.room:room-compiler:2.8.4")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.ui:ui:1.11.4")
    debugImplementation("androidx.compose.ui:ui-tooling:1.11.4")
    implementation("androidx.navigation:navigation-compose:2.9.8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")
    //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
    ksp("androidx.room:room-compiler:2.8.4")
}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}