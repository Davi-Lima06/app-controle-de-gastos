import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.controlecontas"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.controlecontas"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Room
    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")

    // LiveData e ViewModel (opcional, para observar dados)
    implementation ("androidx.lifecycle:lifecycle-livedata:2.8.4")
    implementation ("androidx.lifecycle:lifecycle-viewmodel:2.8.4")
    implementation ("com.github.GrenderG:Toasty:1.5.2")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("org.projectlombok:lombok:1.18.28")

}