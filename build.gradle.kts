plugins {
    kotlin("jvm") version "2.4.10"
    id("dev.detekt") version "2.0.0-alpha.6"
}

repositories {
    mavenCentral()
}

dependencies {
    detektPlugins("dev.detekt:detekt-rules-ktlint-wrapper:2.0.0-alpha.6")
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("detekt.yml")
    // Report findings without failing the build, so that only the crash fails it.
    ignoreFailures = true
}
