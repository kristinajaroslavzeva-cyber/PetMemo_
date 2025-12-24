import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    alias(libs.plugins.google.maps.secrets)
    alias(libs.plugins.hilt.android)
}

val properties = Properties()
val propertiesFile = File(rootProject.rootDir, "local.properties")
if (propertiesFile.exists()) {
    propertiesFile.inputStream().use { properties.load(it) }
}

android {    namespace = "com.name.petmemo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.name.petmemo"
        minSdk = 24
        targetSdk = 35
        versionCode = 23
        versionName = "3.3"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        multiDexEnabled = true
        resourceConfigurations.addAll(listOf("en", "ru"))
    }

    signingConfigs {
        create("release") {
            storeFile = file(properties.getProperty("KEYSTORE_FILE")!!)
            storePassword = properties.getProperty("KEYSTORE_PASSWORD")!!
            keyAlias = properties.getProperty("KEY_ALIAS")!!
            keyPassword = properties.getProperty("KEY_PASSWORD")!!
        }
    }


    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {
    // === ЯДРО ===
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))

    // === COMPOSE ===
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.5")

    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.coil.compose)
    implementation(libs.androidx.core.splashscreen)
    coreLibraryDesugaring(libs.desugar)

    // === НАВИГАЦИЯ И VIEWMODEL ===
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // === ROOM ===
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation("com.google.code.gson:gson:2.10.1")
    ksp(libs.androidx.room.compiler)

    // === ТЕСТЫ ===
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // === FIREBASE ===
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-crashlytics")

    // === GOOGLE SERVICES ===
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    // === UI UTILS ===
    implementation("com.kizitonwose.calendar:compose:2.6.0")
    implementation("androidx.compose.ui:ui-viewbinding")
    implementation("com.jakewharton.timber:timber:5.0.1")

    // === HILT ===
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // === BILLING & ADS ===
    implementation("com.android.billingclient:billing-ktx:7.1.1")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("com.google.android.gms:play-services-ads:23.5.0")
    implementation("com.google.android.ump:user-messaging-platform:3.0.0")
    implementation("androidx.emoji2:emoji2-bundled:1.5.0")
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

