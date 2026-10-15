import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// Settings come from Config.local (copy Config.example; the file is git-ignored). A missing file is fine: the app then
// shows a readable "configuration missing" screen instead of crashing.
val config = Properties().apply {
    val f = rootProject.file("Config.local")
    if (f.exists()) f.inputStream().use { load(it) }
}
fun cfg(key: String) = (config.getProperty(key) ?: "").trim().replace("\\", "\\\\").replace("\"", "\\\"")

android {
    namespace = "io.yesid.sample"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.yesid.sample"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "PUBLISHABLE_KEY", "\"${cfg("PUBLISHABLE_KEY")}\"")
        buildConfigField("String", "CONSUMER_USERNAME", "\"${cfg("CONSUMER_USERNAME")}\"")
        buildConfigField("String", "ENVIRONMENT", "\"${cfg("ENVIRONMENT").ifEmpty { "sandbox" }}\"")
        buildConfigField("String", "SESSION_URL", "\"${cfg("SESSION_URL")}\"")
        buildConfigField("String", "OCR_LICENSE", "\"${cfg("OCR_LICENSE")}\"")
        buildConfigField("String", "FACE_LICENSE", "\"${cfg("FACE_LICENSE")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    // card_detection (OpenCV) bundles libc++_shared.so; keep one copy.
    packaging { jniLibs { pickFirsts += "lib/**/libc++_shared.so" } }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions { jvmTarget = "11" }
    buildFeatures { compose = true; buildConfig = true }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)

    // Yesid SDK
    implementation(libs.yesid.licensing)
    implementation(libs.yesid.card.detection)
    implementation(libs.yesid.face)

    coreLibraryDesugaring(libs.desugar)
}
