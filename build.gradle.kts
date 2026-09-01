// Root build configuration. Module-specific logic lives in app/build.gradle.kts
// and tools/pack/build.gradle.kts.

// javapoet 1.13.0 (pulled by AGP) removed ClassName.canonicalName(), which the
// Hilt Gradle plugin's aggregation worker still calls. Pinning the last version
// that has it first on the build classpath fixes :app:hiltAggregateDeps*.
buildscript {
    dependencies {
        classpath("com.squareup:javapoet:1.12.1")
    }
}

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
    description = "Validate content packs (schema, calendar, cross-refs, launcher art, ID immutability)."
    classpath = project(":tools:pack").extensions
        .getByType(org.gradle.api.plugins.JavaPluginExtension::class.java)
        .sourceSets["main"].runtimeClasspath
    mainClass = "com.shadowmonarchbooks.dayloop.tools.pack.PackLintAllKt"
    args(
        listOf("validate", "--pack", (findProperty("pack") ?: "content/packs/p5r").toString()) +
            (findProperty("packlintExtra")?.toString()?.split(" ")?.filter { it.isNotBlank() } ?: emptyList()),
    )
}

// Extract schedule facts from a local guide archive into candidate JSON
// (build/packgen — never committed; docs/sources.md).
tasks.register<JavaExec>("packgen") {
    group = "content"
    description = "Extract day-schedule facts from a local guide archive (not in git) into candidates."
    classpath = project(":tools:packgen").extensions
        .getByType(org.gradle.api.plugins.JavaPluginExtension::class.java)
        .sourceSets["main"].runtimeClasspath
    mainClass = "com.shadowmonarchbooks.dayloop.tools.packgen.PackGenKt"
    args(
        "--archive",
        (findProperty("archive") ?: "P5R_100p_Guide_AI_Package").toString(),
        "--out",
        (findProperty("out") ?: "build/packgen").toString(),
    )
}
