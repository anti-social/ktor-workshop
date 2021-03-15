import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.4.31"
}

group = "me.alexk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("me.alexk.MainKt")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}