// packgen: extracts day-schedule FACTS from a local guide archive into
// candidate JSON for human curation. The archive itself is all-rights-reserved
// and never enters version control (docs/sources.md); candidates are written
// to build/ and are likewise never committed.
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.kotlinx.serialization.json)

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:${libs.versions.kotlin.get()}")
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("failed")
        showStackTraces = true
    }
}
