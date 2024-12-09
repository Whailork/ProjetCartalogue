import com.android.tools.r8.internal.pl

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "uqac.dim.projetcartalogue"
    compileSdk = 34

    defaultConfig {
        applicationId = "uqac.dim.projetcartalogue"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.text.recognition)
    implementation(libs.firebase.inappmessaging)
    implementation(libs.room.common)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.contentpager)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.material.v123alpha04)
    implementation (libs.recyclerview)
    implementation (libs.cardview)
    implementation (libs.room.runtime.v250 )
    annotationProcessor (libs.room.compiler)
    implementation (libs.gson)
}