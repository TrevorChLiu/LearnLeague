plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "edu.cuhk.csci3310.learnleague"
    compileSdk = 35

    defaultConfig {
        applicationId = "edu.cuhk.csci3310.learnleague"
        minSdk = 33
        targetSdk = 35
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    implementation(libs.androidx.appcompat)
    // for fragment
    val fragment_version = "1.8.6"
    implementation("androidx.fragment:fragment:$fragment_version")
    implementation("androidx.fragment:fragment-ktx:$fragment_version")

    // for connection
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // JSON processing
    implementation("org.json:json:20240303")

    // AndroidX core libraries
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // UI components
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // newest updated swiperefreshlayout
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
    // recyclerview
    implementation ("androidx.recyclerview:recyclerview:1.3.1")

    // create round profile
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    // adapter
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    // for retrieve youtube data
    implementation("com.squareup.picasso:picasso:2.71828")

    // for thumbnail
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    // YouTube player
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.1")

    //save data
    implementation("com.google.code.gson:gson:2.10.1")
}