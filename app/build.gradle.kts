plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
}

android {
    namespace = "com.sprint.lock.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sprint.lock.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        externalNativeBuild {
            cmake {
                cppFlags("-std=c++11")
                abiFilters("arm64-v8a")
            }
        }
    }


    externalNativeBuild {
        cmake {
            file ("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }



    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildFeatures {
        viewBinding = true
    }

    viewBinding {
        enable = true
    }

}

dependencies {
    implementation(project(mapOf("path" to ":commons")))
    implementation(project(mapOf("path" to ":photoselector")))
    implementation(project(mapOf("path" to ":room_database")))
    implementation(project(mapOf("path" to ":libuvccamera")))
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.haibin:calendarview:3.7.1")
    implementation("com.lovedise:permissiongen:0.0.6")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("androidx.camera:camera-view:1.4.0")
    implementation("androidx.camera:camera-video:1.4.0")
    implementation("androidx.camera:camera-core:1.1.0-beta01")
    implementation("androidx.camera:camera-camera2:1.1.0-beta01")
    implementation("androidx.camera:camera-lifecycle:1.1.0-beta01")
    implementation("androidx.camera:camera-view:1.1.0-beta01")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.6.2")
    implementation("com.github.onlyloveyd:SlideToggleView:1.0")
    implementation(files("libs\\QWeather_Public_Android_V4.17.jar"))
    //Android权限请求框架
    implementation("com.github.respost:OmgPermission:1.0")
    implementation ("com.herohan:UVCAndroid:1.0.5")
    implementation ("com.github.getActivity:XXPermissions:13.5")
    implementation(project(mapOf("path" to ":seriallibrary")))
    implementation(files("libs\\AMap_Location_V5.3.1_20210331.jar"))
}