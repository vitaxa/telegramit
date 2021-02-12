plugins {
    id("io.spring.dependency-management")
    kotlin("kapt")
}

val springVersion: String by project

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:$springVersion")
    }
}

dependencies {
    implementation(project(":telegramit-core"))

    implementation("org.springframework.boot:spring-boot-starter")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}
