// Root build configuration. Module-specific logic lives in app/build.gradle.kts
// and tools/pack/build.gradle.kts.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

// Validate content packs: ./gradlew packlint [-Ppack=content/packs/p5r]
tasks.register<JavaExec>("packlint") {
    group = "verification"
    description = "Validate content packs (schema, calendar, cross-refs, ID immutability)."
    classpath = project(":tools:pack").extensions
        .getByType(org.gradle.api.plugins.JavaPluginExtension::class.java)
        .sourceSets["main"].runtimeClasspath
    mainClass = "com.shadowmonarchbooks.dayloop.tools.pack.PackLintKt"
    args(
        listOf("validate", "--pack", (findProperty("pack") ?: "content/packs/p5r").toString()) +
            (findProperty("packlintExtra")?.toString()?.split(" ")?.filter { it.isNotBlank() } ?: emptyList()),
    )
}
