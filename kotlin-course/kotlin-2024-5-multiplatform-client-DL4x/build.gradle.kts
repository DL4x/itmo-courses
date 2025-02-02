import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget

val `I wanna bonus` = false

plugins {
    kotlin("multiplatform") version "2.0.20"
}

group = "ru.itmo"

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm()

    js(IR) {
        nodejs()
        browser {
            testTask {
                useKarma {
                    useChromeHeadlessNoSandbox()
                }
            }
        }

        generateTypeScriptDefinitions()
        binaries.executable()
    }

    if (`I wanna bonus`) {
        nativeX64 {
            binaries.executable { entryPoint = "ru.itmo.main" }

            val main by compilations
            main.cinterops.create("libcurl") {
                definitionFile = project.file(curlDef)
                includeDirs.headerFilterOnly(curlPaths)
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
            }
        }

        jsMain {
            dependencies {
                implementation(npm("node-fetch", "2.6.7")) // 3.3.2
            }
        }
    }
}

fun KotlinMultiplatformExtension.nativeX64(configuration: KotlinNativeTarget.() -> Unit = {}) {
    val nativeTarget = when (HostManager.host) {
        is KonanTarget.LINUX_X64 -> linuxX64()
        is KonanTarget.LINUX_ARM64 -> linuxArm64()
        is KonanTarget.MACOS_X64 -> macosX64()
        is KonanTarget.MACOS_ARM64 -> macosArm64()
        is KonanTarget.MINGW_X64 -> mingwX64()
        else -> error("Unsupported target ${HostManager.host}")
    }
    nativeTarget.configuration()
}

val curlPaths: List<String>
    get() = listOf(
        "/opt/homebrew/opt/curl/include/",
        "/opt/local/include/",
        "/usr/local/include/",
        "/usr/include/",
        "/usr/local/opt/curl/include/",
        "/usr/include/x86_64-linux-gnu/",
        "/usr/local/Cellar/curl/7.62.0/include/",
        "/usr/local/Cellar/curl/7.63.0/include/",
        "/usr/local/Cellar/curl/7.65.3/include/",
        "/usr/local/Cellar/curl/7.66.0/include/",
        "/usr/local/Cellar/curl/7.80.0/include/",
        "/usr/local/Cellar/curl/7.80.0_1/include/",
        "/usr/local/Cellar/curl/7.81.0/include/",
        "native/interop/mingwX64/include/",
    )

val KotlinNativeTarget.curlDef: String
    get() = if (konanTarget == KonanTarget.MACOS_ARM64) {
        "native/interop/libcurl_arm64.def"
    } else {
        "native/interop/libcurl.def"
    }
