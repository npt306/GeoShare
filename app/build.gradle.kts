plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.geoshare"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.geoshare"
        minSdk = 28
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

    dataBinding {
        enable = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation("com.android.support:cardview-v7:27.+")
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.circleimageview)
    implementation(libs.material.v120)
    implementation(libs.glide)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.zxing.android.embedded)
    implementation(libs.billing)
    implementation(libs.play.services.ads)
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-auth-ktx")
    // Import the BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))
    // Declare the dependency for the Cloud Firestore library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    // Stripe Android SDK
    implementation("com.stripe:stripe-android:20.41.0")
    implementation("com.google.firebase:firebase-functions-ktx:20.4.0")
    implementation(libs.firebase.firestore)
    implementation(libs.flexbox)
    implementation(libs.places)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.glide.v4160);
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-storage")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
}