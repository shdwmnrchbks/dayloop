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
}
