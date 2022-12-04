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

import io.grpc.ServerBuilder;
import java.util.function.Consumer;
import org.noelware.analytics.jvm.server.extensions.Extension;
import org.noelware.analytics.jvm.server.extensions.internal.DefaultExtensionRegistry;
import org.noelware.analytics.jvm.server.internal.DefaultAnalyticsServer;
import org.noelware.analytics.jvm.server.internal.metadata.DefaultServerMetadata;

/**
 * Represents the builder class for creating the {@link AnalyticsServer}.
 */
public class AnalyticsServerBuilder {
    private final DefaultExtensionRegistry extensionRegistry = new DefaultExtensionRegistry();
    private final ServerMetadata serverMetadata = new DefaultServerMetadata();
    private final ServerBuilder<?> serverBuilder;
    private String serviceToken;

    /**
     * Creates a new instance of {@link AnalyticsServerBuilder} with the default port
     * being <code>10234</code>
     */
    public AnalyticsServerBuilder() {
        this(10234);
    }

    /**
     * Creates a new instance of the {@link AnalyticsServerBuilder}.
     * @param port The port to bind the server to
     */
    public AnalyticsServerBuilder(int port) {
        this.serverBuilder = ServerBuilder.forPort(port);
    }

    public AnalyticsServerBuilder withServerMetadata(Consumer<ServerMetadata> metadata) {
        metadata.accept(serverMetadata);
        return this;
    }

    public AnalyticsServerBuilder withServiceToken(String token) {
        this.serviceToken = token;
        return this;
    }

    /**
     * Sets the service token that the server gives out when you
     * create your instance.
     *
     * @param serviceToken The service token that is encoded in Base64
     */
    public void setServiceToken(String serviceToken) {
        this.serviceToken = serviceToken;
    }

    /**
     * Registers an extension at the builder level
     * @param extension The extension to register
     * @return {@link AnalyticsServerBuilder} for chaining methods
     */
    public <T> AnalyticsServerBuilder withExtension(Extension<T> extension) {
        extensionRegistry.register(extension);
        return this;
    }

    /**
     * Modifies the gRPC server to your liking
     * @param serverBuilderConsumer The {@link Consumer} function to modify the {@link ServerBuilder server builder}.
     * @return {@link AnalyticsServerBuilder} for chaining methods
     */
    public AnalyticsServerBuilder withServerBuilder(Consumer<ServerBuilder<?>> serverBuilderConsumer) {
        serverBuilderConsumer.accept(serverBuilder);
        return this;
    }

    /**
     * Builds a new {@link AnalyticsServer}.
     */
    public AnalyticsServer build() {
        if (serviceToken == null) throw new IllegalStateException("Missing service token to use when connecting!");
        return new DefaultAnalyticsServer(extensionRegistry, serviceToken, serverMetadata, serverBuilder);
    }
}
