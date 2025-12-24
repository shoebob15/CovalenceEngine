plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("com.shoebob.MainKt")
    applicationDefaultJvmArgs = mutableListOf("-XstartOnFirstThread")
}

dependencies {
    implementation(project(":engine"))
}

kotlin {
    jvmToolchain(21)
}