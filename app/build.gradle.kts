plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.shadowmonarchbooks.dayloop"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.shadowmonarchbooks.dayloop"
        minSdk = 26          // per docs/PLAN.md architecture table
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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

    // Bundled pack content (docs/PLAN.md §2): every directory under
    // /content/packs becomes an asset root, so lint-validated JSON ships as-is.
    sourceSets["main"].assets.srcDir(rootDir.resolve("content/packs"))
}

dependencies {
    implementation(project(":core:pack"))
    implementation(project(":core:progress"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)

    // Progress layer (docs/PLAN.md Phase 3): Room for mutable progress,
    // DataStore for settings (active profile per pack).
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)

    // Home-screen widget (docs/PLAN.md Phase 5).
    implementation(libs.androidx.glance.appwidget)

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:${libs.versions.kotlin.get()}")
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events("failed", "skipped")
        showStackTraces = true
    }
}

// Export Room schemas so future progress migrations are reviewable in-repo.
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
