import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

group = "net.ruben"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" && isArm64 -> macosArm64("native")
        hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
        hostOs == "Linux" && isArm64 -> linuxArm64("native")
        hostOs == "Linux" && !isArm64 -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        targets.withType<KotlinNativeTarget> {
            named("${name}Main") {
                kotlin.srcDir("src/nativeMain/kotlin")
                resources.srcDir("resources")
            }
        }
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
            languageSettings.optIn("kotlin.experimental.ExperimentalNativeApi")
        }
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
        compilations.getByName("main") {
            cinterops {
                val sdl by creating {
                    definitionFile.set(project.file("src/nativeInterop/cinterop/sdl.def"))
                    includeDirs("libs/include")
                }
            }
        }

    }
}

tasks.withType<Copy>() {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.register<Copy>("copyResources") {
   val spec = copySpec{
       from("libs/") {
           include("**/*.dll")
           exclude("include")
       }

       from("$buildDir/processedResources/native/main") {
           include("**/*")
       }
       includeEmptyDirs = false
   }

    copy {
        with(spec)
        into("$buildDir/bin/native/debugExecutable")
    }
    copy {
        with(spec)
        into("$buildDir/bin/native/releaseExecutable")
    }

    copy {
        from("$buildDir/processedResources/native/main") {
            include("**/*")
        }
        into("$projectDir")
    }
    dependsOn("nativeProcessResources")
}

tasks.named("compileKotlinNative") {
    mustRunAfter("copyResources")
}

tasks.named("runDebugExecutableNative") {
    dependsOn("copyResources")
}

tasks.named("runReleaseExecutableNative") {
    dependsOn("copyResources")
}
