plugins {
    kotlin("jvm") version "2.2.20"
}

val group = "com.shoebob"
val version = "0.1.0"

allprojects {
    group = this.group
    version = this.version

    repositories {
        mavenCentral()
    }
}