import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    application
    id("org.springframework.boot") version "2.4.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.4.30"
    kotlin("plugin.spring") version "1.4.30"
}

group = "com.vitaxa"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_15

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    maven {
        setUrl("https://maven.pkg.github.com/vitaxa/telegramit")
        credentials {
            username = project.findProperty("githubUser") as String?
            password = project.findProperty("githubToken") as String?
        }
    }
    mavenCentral()
}

val telegramitVersion = "0.1.25"

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Third party
    implementation("org.botlaxy:telegramit-starter:$telegramitVersion")

    implementation("com.squareup.okhttp3:okhttp:4.6.0") // Important to ignore spring dependency management. Can we do it better?

    // Dev tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

application {
    mainClass.set("com.vitaxa.psfivetelegrambot.SpringTelegramitSampleApplicationKt")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "15"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    enabled = true
    manifest {
        attributes("Implementation-Version" to archiveVersion)
    }
}

// Extract the files for the correct work of the telegramit handlers
tasks.withType<BootJar> {
    archiveClassifier.set("boot")
    requiresUnpack("**/**kotlin**.jar")
    requiresUnpack("**/**telegramit**.jar")
}
