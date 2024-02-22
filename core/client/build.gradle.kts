import com.codeskraps.binance.ConfigData
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.codeskraps.core.client"
    compileSdk = ConfigData.compileSdk

    defaultConfig {
        minSdk = ConfigData.minSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        project.rootProject.file("binance.properties").run {
            val properties = Properties().apply { load(inputStream()) }
            listOf("API_KEY", "SECRET_KEY", "BASE_URL", "TESTNET_URL").forEach { key ->
                buildConfigField(
                    type = "String",
                    name = key,
                    value = properties.getProperty(key) ?: ""
                )
            }
        }
    }

    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        release {
            isMinifyEnabled = ConfigData.isMinifyRelease
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = ConfigData.isMinifyDebug
        }
    }
    compileOptions {
        sourceCompatibility = ConfigData.javaVersion
        targetCompatibility = ConfigData.javaVersion
    }
    kotlinOptions {
        jvmTarget = ConfigData.javaTarget
    }
}

dependencies {
    implementation(project(mapOf("path" to ":core:realm")))

    implementation(libs.androidx.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.androidx.work.runtime)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.binance.connector.java)

    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}