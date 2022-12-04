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

package org.noelware.analytics.jvm.server.internal;

import io.grpc.Server;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.NotNull;
import org.noelware.analytics.jvm.server.AnalyticsServer;
import org.noelware.analytics.jvm.server.extensions.ExtensionRegistry;
import org.noelware.analytics.jvm.server.extensions.internal.DefaultExtensionRegistry;

public class DefaultAnalyticsServer implements AnalyticsServer {
    private final ExtensionRegistry extensionRegistry;
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final Server server;

    public DefaultAnalyticsServer(Server server) {
        this.extensionRegistry = new DefaultExtensionRegistry();
        this.server = server;
    }

    public DefaultAnalyticsServer(ExtensionRegistry registry, Server server) {
        this.extensionRegistry = registry;
        this.server = server;
    }

    /**
     * Returns the {@link ExtensionRegistry extension registry} to register or fetch extensions
     * provided by you.
     */
    @Override
    public @NotNull ExtensionRegistry extensions() {
        return extensionRegistry;
    }

    /**
     * Returns if this {@link AnalyticsServer analytics server} is closed or not.
     */
    @Override
    public boolean isClosed() {
        return closed.get();
    }

    /**
     * Returns if this {@link AnalyticsServer analytics server} has started. Returns <code>false</code>
     * if {@link #isClosed()} is true.
     */
    @Override
    public boolean hasStarted() {
        return started.get();
    }

    /**
     * Returns the underlying {@link Server gRPC server}.
     */
    @Override
    public @NotNull Server server() {
        return server;
    }

    /**
     * Starts the server.
     */
    @Override
    public void start() throws IOException {
        if (started.compareAndSet(false, true)) {
            server.start();
        }
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     */
    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            server.shutdownNow();
            try {
                server.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // do nothing :)
            }
        }
    }
}
