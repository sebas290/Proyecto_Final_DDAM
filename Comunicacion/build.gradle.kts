plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.comunicacion"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core Android libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Use the Compose Bill of Materials (BoM) to manage all Compose versions
    implementation(platform(libs.androidx.compose.bom))

    // Core Jetpack Compose dependencies
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)

    // Add the missing Compose UI dependencies
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation(libs.androidx.compose.material3)

    debugImplementation("androidx.compose.ui:ui-tooling")

    // Use the Firebase BoM to manage Firebase versions
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))

    // Specific Firebase dependency (version is handled by the BoM)
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}