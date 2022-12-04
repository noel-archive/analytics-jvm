/*
 * 🐻‍❄️🌂 analytics-jvm: Client and server implementation of Noelware Analytics in Java, supported for both Java and Kotlin
 * Copyright (c) 2022 Noelware <team@noelware.org>
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

package org.noelware.analytics.gradle

import dev.floofy.utils.gradle.by
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar
import java.util.*

val Project.prettyPath: String
    get() = path.substring(1).replace(':', '-')

fun MavenPublication.createPublicationMeta(
    project: Project,
    componentKey: String,
    javadocJar: TaskProvider<Jar>,
    sourcesJar: TaskProvider<Jar>
) {
    from(project.components.getByName(componentKey))

    artifactId = "analytics-${project.prettyPath}"
    groupId = "org.noelware.analytics"
    version = "$VERSION"

    artifact(javadocJar.get())
    artifact(sourcesJar.get())

    pom {
        description by "\uD83D\uDC3B\u200D❄️\uD83C\uDF02 Client and server implementation of Noelware Analytics in Java, supported for both Java and Kotlin"
        name by "analytics-${project.prettyPath}"
        url by "https://analytics.noelware.org/docs/libraries/analytics-jvm/$VERSION/api/$componentKey/analytics-${project.prettyPath}"

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
