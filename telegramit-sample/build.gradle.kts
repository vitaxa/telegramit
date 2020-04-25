import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.3.71"
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
    mavenLocal()
    mavenCentral()
}

val telegramitVersion = "0.0.18"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("script-runtime"))
    implementation("org.botlaxy:telegramit:$telegramitVersion")
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
