import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.4.30"

    application
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
}

group = "me.alexk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "logevo"
        url = uri("https://gitlab.evo.dev/api/v4/projects/2442/packages/maven")
    }
    maven {
        name = "metrics"
        url = uri("https://dl.bintray.com/evo/maven")
    }
}

val ktorVersion = "1.5.2"
val slf4jVersion = "1.7.30"
val kotlinLoggingVersion = "2.0.4"
val log4jVersion = "2.14.0"
val logevoVersion = "0.2.0"
val prometheusKtVersion = "0.1.0"

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    // Logging
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("io.github.microutils:kotlin-logging:${kotlinLoggingVersion}")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
    runtimeOnly("dev.evo.logging:logevo-log4j:$logevoVersion")

    implementation("dev.evo:prometheus-kt-ktor:$prometheusKtVersion")

    implementation("dev.evo.elasticmagic:elasticmagic")
    implementation("dev.evo.elasticmagic:elasticmagic-serde-json")
    implementation("dev.evo.elasticmagic:elasticmagic-transport-ktor")
}

application {
    mainClass.set("me.alexk.MainKt")
    applicationDefaultJvmArgs = listOf(
        "-Dlogevo.more.warn=ktor.application"
    ) + (System.getProperty("jvm.args")?.toString()?.split(" ") ?: emptyList())
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}