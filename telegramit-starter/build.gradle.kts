plugins {
    id("io.spring.dependency-management")
}

val springVersion: String by project

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:$springVersion")
    }
}

dependencies {
    api(project(":telegramit-core"))
    api(project(":telegramit-spring"))
    api(project(":telegramit-autoconfigure"))
}
