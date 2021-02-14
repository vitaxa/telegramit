import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

plugins {
    id("io.spring.dependency-management")
    kotlin("kapt")
    kotlin("plugin.spring")
}

val springVersion: String by project

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:$springVersion")
    }
}

dependencies {
    implementation(project(":telegramit-core"))

    implementation(project(":telegramit-spring"))

    implementation("org.springframework.boot:spring-boot-autoconfigure")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}
