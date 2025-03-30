plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.springs.common"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }

    viewBinding {
        enable = true
    }
}


dependencies {
    api("io.github.thanosfisherman.wifiutils:wifiutils:1.6.6")
    api("androidx.core:core-ktx:1.9.0")
    api("androidx.appcompat:appcompat:1.7.0")
    api("com.google.android.material:material:1.12.0")
    api("androidx.window:window:1.3.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    api ("androidx.recyclerview:recyclerview:1.1.0")
    api("androidx.databinding:viewbinding:8.6.1")

    //RxLifecycle
    api ("com.trello:rxlifecycle-components:1.0")
    // lifecycle-ViewModel
    api ("androidx.lifecycle:lifecycle-viewmodel-ktx:1.0")
    // lifecycle-LiveData
    api ("androidx.lifecycle:lifecycle-livedata-ktx:1.0")

    //Retrofit，rxjava ，okhttp3  logging
    api ("com.squareup.okhttp3:okhttp:4.9.0")
    api ("com.squareup.retrofit2:retrofit:2.9.0")
    api ("com.squareup.retrofit2:converter-gson:2.9.0")
    api ("com.squareup.retrofit2:adapter-rxjava:2.9.0")
    // RxAndroid
    api ("io.reactivex:rxandroid:1.2.1")
    api ("androidx.multidex:multidex:2.0.1")
    api ("com.google.code.gson:gson:2.8.2")
    api ("com.blankj:utilcode:1.30.7")
    api ("com.adjust.sdk:adjust-android:4.28.7")
    api ("com.android.installreferrer:installreferrer:2.2")
    api ("com.haibin:calendarview:3.7.1")
    api ("com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.30")
    api ("com.github.antonKozyriatskyi:CircularProgressIndicator:1.3.0")
}