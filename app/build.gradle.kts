plugins {
    alias(libs.plugins.android.application)
}
android {
    namespace = "com.example.challenge3"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.challenge3"
        minSdk = 26
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    /* MQTT DEPENDENCIES */
    //implementation(libs.paho.android.service)
    //implementation(libs.org.eclipse.paho.client.mqttv3)
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    implementation("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1")
    implementation(libs.paho.mqttv3)
    implementation(libs.localbroadcastmanager)
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.0.0")
    implementation("com.github.hannesa2:paho.mqtt.android:3.6.6")
    implementation ("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    //implementation ("info.mqtt:android-service:1.1.1")
    implementation("com.github.hannesa2:paho.mqtt.android:3.6.6")
}
