// Engine-neutral progress semantics: tri-state step marks, the End-Day clock,
// and orphan detection. Pure Kotlin (no Android, no serialization) so the
// semantics are cheap to unit test in CI (docs/PLAN.md Phase 3).
plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:${libs.versions.kotlin.get()}")
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("failed", "skipped")
        showStackTraces = true
    }
}
