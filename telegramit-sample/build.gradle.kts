import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.4.0"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "org.botlaxy"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    jcenter()
    maven {
        setUrl("https://dl.bintray.com/vitaxa/telegramit/")
    }
    mavenCentral()
}

val telegramitVersion = "0.1.14"
val ktorVersion = "1.4.0"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("script-runtime"))
    implementation("org.botlaxy:telegramit-core:${telegramitVersion}")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
}

tasks.withType<ShadowJar> {
    archiveBaseName.set(project.name)
    mergeServiceFiles()
    manifest {
        attributes("Main-Class" to "BotAppKt")
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}
