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

package org.noelware.analytics.jvm.client.async;

import io.grpc.ManagedChannelBuilder;
import java.util.function.Consumer;
import org.noelware.analytics.jvm.client.internal.async.DefaultAsyncAnalyticsClient;

public class AsyncAnalyticsClientBuilder {
    private final ManagedChannelBuilder<?> channelBuilder;
    private String serviceToken;

    AsyncAnalyticsClientBuilder(ManagedChannelBuilder<?> builder) {
        this.channelBuilder = builder;
    }

    public static AsyncAnalyticsClientBuilder create(String host) {
        return new AsyncAnalyticsClientBuilder(ManagedChannelBuilder.forTarget(host));
    }

    public static AsyncAnalyticsClientBuilder create(String host, int port) {
        return new AsyncAnalyticsClientBuilder(ManagedChannelBuilder.forAddress(host, port));
    }

    public AsyncAnalyticsClientBuilder withManagedChannel(Consumer<ManagedChannelBuilder<?>> consumer) {
        consumer.accept(channelBuilder);
        return this;
    }

    public AsyncAnalyticsClientBuilder withServiceToken(String token) {
        this.serviceToken = token;
        return this;
    }

    public AsyncAnalyticsClient build() {
        if (serviceToken == null) throw new IllegalStateException("Missing service token to use");
        return new DefaultAsyncAnalyticsClient(serviceToken, channelBuilder);
    }
}
