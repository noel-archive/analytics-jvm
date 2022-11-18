/*
 * 🌂 analytics-jvm: Client and server implementation of Noelware Analytics in Java, supported for both Java and Kotlin
 * Copyright (c) 2022 Noelware
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.noelware.analytics.gradle.*
import dev.floofy.utils.gradle.*

plugins {
    id("com.diffplug.spotless")
    id("org.jetbrains.dokka")
    `java-library`
    kotlin("jvm")
}

group = "org.noelware.analytics"
version = "$VERSION"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation("org.slf4j:slf4j-simple:2.0.4")
    testImplementation(kotlin("test"))
    api("org.slf4j:slf4j-api:2.0.4")
}

spotless {
    kotlin {
        trimTrailingWhitespace()
        licenseHeaderFile("${rootProject.projectDir}/assets/HEADING")
        endWithNewline()

        // We can't use the .editorconfig file, so we'll have to specify it here
        // issue: https://github.com/diffplug/spotless/issues/142
        ktlint()
            .setUseExperimental(true)
            .userData(mapOf(
                "no-consecutive-blank-lines" to "true",
                "no-unit-return" to "true",
                "disabled_rules" to "no-wildcard-imports,colon-spacing,annotation-spacing",
            ))
            .editorConfigOverride(mapOf("indent_size" to "4"))
    }
}

java {
    sourceCompatibility = JAVA_VERSION
    targetCompatibility = JAVA_VERSION
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = JAVA_VERSION.toString()
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        kotlinOptions.javaParameters = true
    }

    test {
        useJUnitPlatform()
        testLogging {
            showStandardStreams = true
            showStackTraces = true
            exceptionFormat = TestExceptionFormat.FULL
            events.addAll(listOf(
                TestLogEvent.PASSED,
                TestLogEvent.FAILED,
                TestLogEvent.SKIPPED
            ))
        }
    }

    withType<Jar> {
        manifest {
            attributes(
                "Implementation-Version" to "$VERSION",
                "Implementation-Vendor" to "Noelware, LLC. [team@noelware.org]",
                "Implementation-Title" to "analytics-jvm"
            )
        }
    }
}
