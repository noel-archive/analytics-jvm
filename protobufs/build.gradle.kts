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

import com.google.protobuf.gradle.*

plugins {
    id("com.google.protobuf")
    `analytics-module`
    idea
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.google.protobuf:protobuf-java:3.22.2")
    compileOnly("org.apache.tomcat:annotations-api:6.0.53")
    runtimeOnly("io.grpc:grpc-netty:1.54.0")
    api("io.grpc:grpc-protobuf:1.54.0")
    api("io.grpc:grpc-stub:1.54.0")
}

sourceSets {
    create("proto") {
        proto {
            srcDir("src/main/proto")
        }
    }

    main {
        java {
            srcDirs(
                "build/generated/source/proto/main/java"
            )
        }
    }

    test {
        java {
            srcDirs(
                "build/generated/source/proto/main/java"
            )
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.22.2"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.53.0"
        }
    }

    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
}
