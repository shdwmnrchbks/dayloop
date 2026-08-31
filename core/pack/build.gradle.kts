// Shared pack contract: schema + loader + calendar helpers.
// Consumed by :tools:pack (lint) and :app (rendering) — one source of truth
// for the content-pack format (docs/PLAN.md §3).
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(libs.kotlinx.serialization.json)

    // Seed -> tonal scheme mapping (docs/ROADMAP-v2.md Phase 10 / ROADMAP-v3.md
    // Phase 12): shared by the app renderer and packlint's contrast rule so
    // both see the exact same colors for a pack's declared palette.
    implementation(libs.material.color.utilities)

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
