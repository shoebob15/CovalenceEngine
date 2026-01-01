val lwjglVersion = "3.3.6"
val lwjglNatives = "natives-macos-arm64"

plugins {
    kotlin("jvm")
    `java-library`
    idea
}

repositories {
    mavenCentral()
}
// makes dev much easier
idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-assimp")
    implementation("org.lwjgl", "lwjgl-bgfx")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-remotery")
    implementation("org.lwjgl", "lwjgl-stb")
    implementation ("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-bgfx", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-remotery", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)

    implementation("ch.qos.logback:logback-classic:0.9.26")

    implementation("org.joml:joml:1.10.8") // TODO: remove dependency on joml
}

kotlin {
    jvmToolchain(21)
}