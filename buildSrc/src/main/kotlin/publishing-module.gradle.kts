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

import org.noelware.analytics.gradle.*
import dev.floofy.utils.gradle.*
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import java.io.StringReader
import java.util.Properties

plugins {
    id("org.jetbrains.dokka")
    `maven-publish`
    kotlin("jvm")
}

// Get the `publishing.properties` file from the `gradle/` directory
// in the root project.
val publishingPropsFile = file("${rootProject.projectDir}/gradle/publishing.properties")
val publishingProps = Properties()

// If the file exists, let's get the input stream
// and load it.
if (publishingPropsFile.exists()) {
    publishingProps.load(publishingPropsFile.inputStream())
} else {
    // Check if we do in environment variables
    val accessKey = System.getenv("NOELWARE_PUBLISHING_ACCESS_KEY") ?: ""
    val secretKey = System.getenv("NOELWARE_PUBLISHING_SECRET_KEY") ?: ""

    if (accessKey.isNotEmpty() && secretKey.isNotEmpty()) {
        val data = """
        |s3.accessKey=$accessKey
        |s3.secretKey=$secretKey
        """.trimMargin()

        publishingProps.load(StringReader(data))
    }
}

// Check if we have the `NOELWARE_PUBLISHING_ACCESS_KEY` and `NOELWARE_PUBLISHING_SECRET_KEY` environment
// variables, and if we do, set it in the publishing.properties loader.
val snapshotRelease: Boolean = run {
    val env = System.getenv("NOELWARE_PUBLISHING_IS_SNAPSHOT") ?: "false"
    env == "true"
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val documentationJar by if (plugins.hasPlugin(JavaPlugin::class)) {
    tasks.registering(Jar::class) {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        description = "Assemble Java documentation with Javadoc"

        archiveClassifier.set("javadoc")
        from(tasks.javadoc)
        dependsOn(tasks.javadoc)
    }
} else {
    tasks.registering(Jar::class) {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        description = "Assemble Kotlin documentation with Dokka"

        archiveClassifier.set("javadoc")
        from(tasks.dokkaHtml)
        dependsOn(tasks.dokkaHtml)
    }
}

publishing {
    publications {
        create<MavenPublication>("analyticsProtobuf") {
            val key = if (plugins.hasPlugin(JavaPlugin::class)) "java" else "kotlin"
            from(components[key])

            artifactId = "analytics-${project.name}"
            groupId = "org.noelware.analytics"
            version = "$VERSION"

            artifact(documentationJar.get())
            artifact(sourcesJar.get())

            pom {
                description by "Protocol Buffers typings, client, and server implementation of Noelware Analytics for the JVM"
                name by "analytics-${project.name}"
                url by when (key) {
                    "kotlin" -> "https://docs.noelware.org/analytics/jvm/${project.name}/api"
                    "java" -> "https://docs.noelware.org/analytics/jvm/api"
                    else -> error("This should never happen")
                }

                organization {
                    name by "Noelware"
                    url by "https://noelware.org"
                }

                developers {
                    developer {
                        email by "cutie@floofy.dev"
                        name by "Noel"
                        url by "https://floofy.dev"
                    }

                    developer {
                        name by "Noelware Team"
                        email by "team@noelware.org"
                        url by "https://noelware.org"
                    }
                }

                issueManagement {
                    system by "GitHub"
                    url by "https://github.com/Noelware/analytics-jvm/issues"
                }

                licenses {
                    license {
                        name by "MIT"
                        url by "https://mit-license.org/"
                    }
                }

                scm {
                    connection by "scm:git:ssh://github.com/Noelware/analytics-jvm.git"
                    developerConnection by "scm:git:ssh://git@github.com:Noelware/analytics-jvm.git"
                    url by "https://github.com/Noelware/analytics-jvm"
                }
            }
        }
    }

    repositories {
        val url = if (snapshotRelease) "s3://maven.noelware.org/snapshots" else "s3://maven.noelware.org"
        maven(url) {
            credentials(AwsCredentials::class.java) {
                this.accessKey = publishingProps.getProperty("s3.accessKey") ?: ""
                this.secretKey = publishingProps.getProperty("s3.secretKey") ?: ""
            }
        }
    }
}
