plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.pm1e2grupo6"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pm1e2grupo6"
        minSdk = 24
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

<<<<<<< HEAD
=======
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    val versionRetrofit = "2.9.0"
    val versionOkHttp = "4.9.0"

    //Implementaciones para Retrofit y OkHttp
    implementation("com.squareup.retrofit2:retrofit:$versionRetrofit")
    implementation("com.squareup.retrofit2:converter-gson:$versionRetrofit")
    implementation("com.squareup.okhttp3:logging-interceptor:$versionOkHttp")
    implementation("com.squareup.okhttp3:okhttp:$versionOkHttp")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.+")

>>>>>>> origin/master
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}