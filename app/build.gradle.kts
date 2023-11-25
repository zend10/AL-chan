import java.util.Properties

plugins {
    id("com.android.application")
    id("com.apollographql.apollo3")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.zen.alchan"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.zen.alchan"
        minSdk = 23
        targetSdk = 33
        versionCode = 200900
        versionName = "2.9.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val apikeyProperties = readProperties(file("../apikey.properties"))
        buildConfigField("String", "YOUTUBE_API_KEY", apikeyProperties["YOUTUBE_API_KEY"] as String)
        buildConfigField("String", "SPOTIFY_API_KEY", apikeyProperties["SPOTIFY_API_KEY"] as String)
    }
    signingConfigs {
        getByName("debug") {
            storeFile = file(findProperty("KEYSTORE_PATH_DEV")!!)
            storePassword = findProperty("KEYSTORE_PASSWORD_DEV") as String
            keyAlias = findProperty("KEY_ALIAS_DEV") as String
            keyPassword = findProperty("KEY_PASSWORD_DEV") as String
        }
    }
    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
            versionNameSuffix = "-debug"
            applicationIdSuffix = ".debug"
            isDebuggable = true
            proguardFiles("proguard-rules.pro")
        }
        getByName("release") {
            isDebuggable = false
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles("proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
//    implementation(fileTree(dir: "libs", include: ["*.jar"])))
    implementation(project(":shared"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.10")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.browser:browser:1.5.0")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.window:window:1.1.0")

    // Google Material
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // JUnit4 for Instrumentation Test
    androidTestImplementation("junit:junit:4.12")

    // AndroidX Test
    val androidx_test_version = "1.2.0"
    androidTestImplementation("androidx.test:core:$androidx_test_version")
    androidTestImplementation("androidx.test:runner:$androidx_test_version")

    // Architecture Components Test
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")

    // JUnit5 for Local Unit Test
    val junit_jupiter_version = "5.6.2"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_jupiter_version")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junit_jupiter_version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_jupiter_version")

    // Mockito
    testImplementation("org.mockito:mockito-core:2.19.0")

    // Rx
    implementation("io.reactivex.rxjava3:rxjava:3.1.6")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation("com.jakewharton.rxbinding4:rxbinding:4.0.0")

    // LiveData and ViewModel
    val lifecycle_version = "2.5.1"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    kapt("androidx.lifecycle:lifecycle-common-java8:$lifecycle_version")

    // Gson
    implementation("com.google.code.gson:gson:2.9.0")

    // Coil
    val coil_version = "1.4.0"
    implementation("io.coil-kt:coil:$coil_version")
    implementation("io.coil-kt:coil-gif:$coil_version")
    implementation("io.coil-kt:coil-svg:$coil_version")

    // Apollo
    val apollo_version = "3.8.1"
    implementation("com.apollographql.apollo3:apollo-runtime:$apollo_version")
    implementation("com.apollographql.apollo3:apollo-rx3-support:$apollo_version")


    // OkHttp
    val okhttp_version = "4.10.0"
    implementation("com.squareup.okhttp3:okhttp:$okhttp_version")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttp_version")

    // Retrofit
    val retrofit_version = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")
    implementation("com.squareup.retrofit2:adapter-rxjava3:$retrofit_version")

    // Koin DI
    val koin_version = "3.2.0"
    implementation("io.insert-koin:koin-android:$koin_version")

    // Others
    implementation("com.jaredrummler:colorpicker:1.1.0")
    implementation("com.github.stfalcon-studio:StfalconImageViewer:v1.0.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.27")
    implementation("com.github.zend10:OverlapImageListView:v1.0.1")

    val markwon_version = "4.6.2"
    implementation("io.noties.markwon:core:$markwon_version")
    implementation("io.noties.markwon:image:$markwon_version")
    implementation("io.noties.markwon:html:$markwon_version")
    implementation("io.noties.markwon:ext-strikethrough:$markwon_version")
    implementation("io.noties.markwon:inline-parser:$markwon_version")

    val work_version = "2.8.1"
    implementation("androidx.work:work-runtime-ktx:$work_version")
    implementation("androidx.work:work-rxjava3:$work_version")

    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.10")
}

apollo {
    packageName.set("com.zen.alchan")
    mapScalar("Json", "java.lang.Object")
    mapScalar("CountryCode", "kotlin.String")
}

fun readProperties(propertiesFile: File) = Properties().apply {
    propertiesFile.inputStream().use { fis ->
        load(fis)
    }
}
