package com.codeskraps.binance

import org.gradle.api.JavaVersion

object ConfigData {
    const val minSdk = 33
    const val targetSdk = 34
    const val compileSdk = 34

    val javaVersion = JavaVersion.VERSION_19
    val javaTarget = javaVersion.toString()

    const val kotlinCompiler = "1.5.14"

    const val versionCode = 1
    const val versionName = "1.0"

    const val isMinifyRelease = true
    const val isMinifyDebug = false
}