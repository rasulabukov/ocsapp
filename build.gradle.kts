// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    kotlin("kapt") version "2.0.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0"
}