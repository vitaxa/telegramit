val mockkVersion = "1.9.+"
val ktorVersion = "1.5.1"

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.ktor:ktor-client-mock-jvm:$ktorVersion")
    testImplementation("com.squareup.okhttp3:okhttp-tls:4.6.0")
}
