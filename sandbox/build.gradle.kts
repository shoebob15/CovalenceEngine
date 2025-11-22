plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("com.shoebob.sandbox.MainKt")
}

dependencies {
    implementation(project(":engine"))
}

kotlin {
    jvmToolchain(21)
}