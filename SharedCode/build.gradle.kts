import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("com.squareup.sqldelight")
    id("kotlinx-serialization")
    id("com.android.library")
    id("kotlin-android-extensions")
}

kotlin {
    android()

    //select iOS target platform depending on the Xcode environment variables
    val iOSTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
            ::iosArm64
        else
            ::iosX64

    iOSTarget("ios") {
        binaries {
            framework {
                baseName = "SharedCode"
            }
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    //select jvm target platform
//    jvm("android") {
//        val main by compilations.getting {
//            kotlinOptions {
//                jvmTarget = JavaVersion.VERSION_1_8.toString()
//            }
//        }
//        val test by compilations.getting {
//            kotlinOptions {
//                jvmTarget = JavaVersion.VERSION_1_8.toString()
//            }
//        }
//    }

    sourceSets["commonMain"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:1.0-M1-1.4.0-rc")
        implementation("org.kodein.di:kodein-di:7.0.0")
        implementation("io.ktor:ktor-client-core:1.3.2")
        api("dev.icerock.moko:mvvm:0.7.0")
        implementation("com.squareup.sqldelight:runtime:1.4.3")
        implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    }

    sourceSets["androidMain"].dependencies {
//        implementation("android.arch.lifecycle:extensions:1.1.1")
//        implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
//        implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
//        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")

        implementation("org.jetbrains.kotlin:kotlin-stdlib")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
        implementation("com.squareup.sqldelight:android-driver:1.4.3")
        implementation("io.ktor:ktor-client-android:1.3.2")
    }

    sourceSets["iosMain"].dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:1.3.7")
        implementation("com.squareup.sqldelight:native-driver:1.4.3")
        implementation("io.ktor:ktor-client-ios:1.3.2")
        implementation("io.ktor:ktor-client-core-native:1.3.2")
    }
}

android {
    compileSdkVersion(29)
    sourceSets["main"].manifest.srcFile("src/androidMain/kotlin/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

sqldelight {
    database("Servers") {
        packageName = "net.compoza.deactivator.databes"
    }
}

val packForXcode by tasks.creating(Sync::class) {
    group = "build"

    //selecting the right configuration for the iOS framework depending on the Xcode environment variables
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val framework = kotlin.targets.getByName<KotlinNativeTarget>("ios").binaries.getFramework(mode)

    inputs.property("mode", mode)
    dependsOn(framework.linkTask)

    val targetDir = File(buildDir, "xcode-frameworks")
    from({ framework.outputDirectory })
    into(targetDir)

    doLast {
        val gradlew = File(targetDir, "gradlew")
        gradlew.writeText("#!/bin/bash\nexport 'JAVA_HOME=${System.getProperty("java.home")}'\ncd '${rootProject.rootDir}'\n./gradlew \$@\n")
        gradlew.setExecutable(true)
    }
}

tasks.getByName("build").dependsOn(packForXcode)