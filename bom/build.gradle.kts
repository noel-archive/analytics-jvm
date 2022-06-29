// code modified from kord
// https://github.com/kordlib/kord/blob/0.8.x/bom/build.gradle.kts

import org.noelware.analytics.gradle.*
import dev.floofy.utils.gradle.*

plugins {
    `maven-publish`
    `java-platform`
}

val me = project
rootProject.subprojects {
    if (name != me.name) {
        me.evaluationDependsOn(path)
    }
}

// Get the `publishing.properties` file from the `gradle/` directory
// in the root project.
val publishingPropsFile = file("${rootProject.projectDir}/gradle/publishing.properties")
val publishingProps = java.util.Properties()

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

        publishingProps.load(java.io.StringReader(data))
    }
}

// Check if we have the `NOELWARE_PUBLISHING_ACCESS_KEY` and `NOELWARE_PUBLISHING_SECRET_KEY` environment
// variables, and if we do, set it in the publishing.properties loader.
val snapshotRelease: Boolean = run {
    val env = System.getenv("NOELWARE_PUBLISHING_IS_SNAPSHOT") ?: "false"
    env == "true"
}

dependencies {
    constraints {
        rootProject.subprojects.forEach { subproj ->
            if (subproj.plugins.hasPlugin("maven-publish") && subproj.name != name) {
                subproj.publishing.publications.withType<MavenPublication> {
                    if (!artifactId.endsWith("-metadata") && !artifactId.endsWith("-kotlinMultiplatform")) {
                        api("$groupId:$artifactId:$version")
                    }
                }
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("analyticsProtobufs") {
            from(components["javaPlatform"])

            artifactId = "analytics-bom"
            groupId = "org.noelware.analytics"
            version = "$VERSION"

            pom {
                description by "Bill of Materials for analytics-jvm"
                name by "analytics-bom"
                url by "https://docs.noelware.org/analytics/jvm"

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
