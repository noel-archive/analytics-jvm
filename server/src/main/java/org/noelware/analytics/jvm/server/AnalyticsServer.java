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

package org.noelware.analytics.jvm.server;

import io.grpc.Server;
import java.io.Closeable;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.noelware.analytics.jvm.server.extensions.ExtensionRegistry;

/**
 * Represents the server implementation of the Noelware Analytics protocol. This is probably your main entrypoint
 * if you wish to wish the Noelware Analytics protocol.
 */
public interface AnalyticsServer extends Closeable {
    /**
     * Returns the {@link ExtensionRegistry extension registry} to register or fetch extensions
     * provided by you.
     */
    @NotNull
    ExtensionRegistry extensions();

    /**
     * Returns if this {@link AnalyticsServer analytics server} is closed or not.
     */
    boolean isClosed();

    /**
     * Returns if this {@link AnalyticsServer analytics server} has started. Returns <code>false</code>
     * if {@link #isClosed()} is true.
     */
    boolean hasStarted();

    /**
     * Returns the underlying {@link Server gRPC server}.
     */
    @NotNull
    Server server();

    /**
     * Returns the instance UUID that this server is using
     */
    @NotNull
    String instanceUUID();

    /**
     * Returns the {@link ServerMetadata} collection.
     */
    @NotNull
    ServerMetadata metadata();

    /**
     * Starts the server.
     */
    void start() throws IOException;
}
