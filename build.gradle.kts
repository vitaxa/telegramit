import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

plugins {
    kotlin("jvm") version "1.3.71"
    id("com.vanniktech.maven.publish") version "0.11.1"
}

allprojects {
    group = "org.botlaxy"
    version = "0.0.18"

    repositories {
        jcenter()
        mavenCentral()
    }
}

val kotlinVersion = plugins.getPlugin(KotlinPluginWrapper::class.java).kotlinPluginVersion
val jacksonVersion = "2.10.3"
val logbackVersion = "1.2.3"
val kotlinLogVersion = "1.7.7"
val mapDb = "3.0.8"
val jnaVersion = "4.2.2"
val emojiVersion = "5.1.1"
val okHttpVersion = "4.4.1"
val ktorVersion = "1.3.2"
val mockWebServerVersion = "4.4.0"

subprojects {

    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "com.vanniktech.maven.publish")

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation(kotlin("reflect", kotlinVersion))
        implementation(kotlin("script-util", kotlinVersion))
        implementation(kotlin("script-runtime", kotlinVersion))
        implementation(kotlin("compiler-embeddable", kotlinVersion))
        implementation(kotlin("scripting-compiler-embeddable", kotlinVersion))
        implementation("io.ktor:ktor-server-netty:$ktorVersion")
        implementation("com.vdurmont:emoji-java:$emojiVersion")
        implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
        implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
        implementation("net.java.dev.jna:jna:$jnaVersion")
        implementation("ch.qos.logback:logback-classic:$logbackVersion")
        implementation("io.github.microutils:kotlin-logging:$kotlinLogVersion")
        implementation("org.mapdb:mapdb:${mapDb}")
    }

}
