plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "ma.emsi.foodallergyapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "ma.emsi.foodallergyapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "GEMINI_API_KEY", "\"AIzaSyBUunzWBjsfZU0BmbP9s-kOLNRl4t-VMMU\"")
        }
        release {
            buildConfigField("String", "GEMINI_API_KEY", "\"AIzaSyBUunzWBjsfZU0BmbP9s-kOLNRl4t-VMMU\"")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        // Enable desugaring for LocalDateTime support on older Android versions
        // Enable desugaring for LocalDateTime support on older Android versions
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "META-INF/*.kotlin_module"
            )
        }
    }
}

dependencies {

    implementation (libs.google.guava)
    implementation (libs.generativeai)
    // Core Android dependencies
    implementation(libs.core)
    implementation(libs.appcompat.v170)
    implementation(libs.constraintlayout.v221)
    implementation(libs.coordinatorlayout)
    implementation("androidx.fragment:fragment:1.6.2")
    implementation("androidx.activity:activity:1.8.2")

    // Material Design
    implementation("com.google.android.material:material:1.11.0")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    // Navigation components
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:2.7.6")

    // RecyclerView and CardView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Networking - Retrofit & OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")

    // Supabase SDK - Using correct versions and repositories
    implementation("io.github.jan-tennert.supabase:supabase-kt:1.4.7")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:1.4.7")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:1.4.7") // Changed from auth-kt
    implementation("io.github.jan-tennert.supabase:realtime-kt:1.4.7")
    implementation("io.github.jan-tennert.supabase:storage-kt:1.4.7")

    // Coroutines for async operations
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Image loading - Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Camera and Barcode scanning
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")
    implementation("androidx.camera:camera-extensions:1.3.1")

    // Barcode scanning
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.0")

    // Permissions handling
    implementation("com.karumi:dexter:6.2.3")

    // SharedPreferences encryption
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Date and time handling (for older Android versions)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Progress indicators - Using alternative to SpinKit
    implementation("com.github.ybq:Android-SpinKit:1.4.0") {
        exclude(group = "com.android.support")
    }
    // Alternative progress indicators
    implementation("com.airbnb.android:lottie:6.2.0")

    // Text processing and validation
    implementation("commons-validator:commons-validator:1.7")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // EventBus for communication between components
    implementation("org.greenrobot:eventbus:3.3.1")

    // Flexible UI components
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    // Biometric authentication (optional)
    implementation("androidx.biometric:biometric:1.1.0")

    // Work Manager for background tasks
    implementation("androidx.work:work-runtime:2.9.0")

    // Room database (if you need local caching)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // Android testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.6")

    // UI testing
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")

    // Fragment testing
    debugImplementation("androidx.fragment:fragment-testing:1.6.2")

    // Add Volley for networking (missing dependency)
    implementation("com.android.volley:volley:1.2.1")

    // Google Maps
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.1.0")
    implementation("com.google.android.libraries.places:places:3.3.0")

    // MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}

