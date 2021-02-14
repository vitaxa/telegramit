import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import com.jfrog.bintray.gradle.BintrayExtension

plugins {
    kotlin("jvm") version "1.4.30"
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.5"

    // Spring
    id("org.springframework.boot") version "2.4.1" apply false
    id("io.spring.dependency-management") version "1.0.10.RELEASE" apply false
    kotlin("kapt") version "1.4.30" apply false
    kotlin("plugin.spring") version "1.4.30" apply false
}

allprojects {
    group = "org.botlaxy"
    version = "0.1.21"

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
val ktorVersion = "1.5.1"
val mockWebServerVersion = "4.4.0"

subprojects {

    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")
    apply(plugin = "com.jfrog.bintray")

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation(kotlin("reflect", kotlinVersion))
        implementation(kotlin("script-util", kotlinVersion))
        implementation(kotlin("script-runtime", kotlinVersion))
        implementation(kotlin("compiler-embeddable", kotlinVersion))
        implementation(kotlin("scripting-compiler-embeddable", kotlinVersion))
        implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

        implementation("io.ktor:ktor-server-netty:$ktorVersion")
        api("io.ktor:ktor-client-okhttp:$ktorVersion")
        implementation("io.ktor:ktor-client-logging-jvm:$ktorVersion")
        implementation("io.ktor:ktor-client-json-jvm:$ktorVersion")
        implementation("io.ktor:ktor-client-jackson:$ktorVersion")
        implementation("io.ktor:ktor-jackson:$ktorVersion")

        implementation("com.vdurmont:emoji-java:$emojiVersion")
        implementation("net.java.dev.jna:jna:$jnaVersion")
        implementation("ch.qos.logback:logback-classic:$logbackVersion")
        implementation("io.github.microutils:kotlin-logging:$kotlinLogVersion")
        implementation("org.mapdb:mapdb:${mapDb}")
    }

    val sourcesJar by tasks.creating(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.getByName("main").allSource)
    }

    publishing {
        publications {
            create<MavenPublication>("telegramit") {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
                from(components["java"])

                artifact(sourcesJar)

                pom {
                    name.set("Telegramit")
                    description.set("Telegram chat bot framework")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("vitaxa")
                            name.set("Vitaliy Banin")
                            email.set("vitaxa93gamebox@gmail.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:https://github.com/vitaxa/telegramit")
                        developerConnection.set("scm:git:ssh://github.com/vitaxa/telegramit")
                    }
                }
            }
        }
    }

    bintray {
        user = project.findProperty("bintrayUser") as String?
        key = project.findProperty("bintrayApiKey") as String?
        publish = true

        setPublications("telegramit")

        pkg.apply {
            repo = "telegramit"
            name = project.name
            githubRepo = "https://github.com/vitaxa/telegramit"
            vcsUrl = "https://github.com/vitaxa/telegramit"
            description = "Telegram chat bot framework"
            setLabels("kotlin", "telegram", "chat", "bot", "dsl")
            setLicenses("Apache-2.0")
            desc = description
            version.apply {
                name = project.version.toString()
            }
        }
    }

}
