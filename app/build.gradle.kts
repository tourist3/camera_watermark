plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
//    id("com.google.gms.google-services")
//    id("com.google.firebase.crashlytics")
    id("kotlinx-serialization")
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("./../upload-keystore.jks")
            storePassword = "123456"
            keyAlias = "upload"
            keyPassword = "123456"
        }
    }
    namespace = "com.mobileheros.camera"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mobileheros.camera"
        minSdk = 27
        targetSdk = 34
        versionCode = 8
        versionName = "1.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("release")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isDebuggable = true
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.config.ktx)
    implementation (libs.imagepicker)
    implementation (libs.x.permissions)
    implementation(libs.billing.ktx)
    implementation (libs.gson)
    implementation (libs.eventbus)
    implementation(libs.androidx.datastore.preferences)
    implementation (libs.net)
    implementation (libs.glide)
//    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
//    implementation("com.google.firebase:firebase-analytics")
//    implementation("com.google.firebase:firebase-config")
//    implementation ("com.google.firebase:firebase-crashlytics-ktx")
//    implementation("com.google.firebase:firebase-storage")
    implementation (libs.play.services.ads)
    implementation ("androidx.lifecycle:lifecycle-runtime:2.3.1")
    implementation(libs.androidx.lifecycle.process)
    annotationProcessor ("androidx.lifecycle:lifecycle-compiler:2.3.1")

    // define a BOM and its version
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    // define any required OkHttp artifacts without version
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    implementation ("com.otaliastudios:cameraview:2.7.2")


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.ultimate.bar.x)
}