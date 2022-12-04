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

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.io.Closeable;
import java.util.concurrent.CompletableFuture;
import org.noelware.analytics.jvm.client.AnalyticsClient;
import org.noelware.analytics.jvm.client.handlers.ResponseHandler;
import org.noelware.analytics.jvm.client.internal.async.DefaultAsyncAnalyticsClient;
import org.noelware.analytics.protobufs.v1.ConnectionAckResponse;
import org.noelware.analytics.protobufs.v1.ReceiveStatsResponse;

/**
 * Represents a {@link org.noelware.analytics.jvm.client.AnalyticsClient client} but that sends requests asynchronously.
 */
public interface AsyncAnalyticsClient extends Closeable {
    /**
     * Creates a new {@link AsyncAnalyticsClient analytics client} with the given target string.
     * @param target The target string to connect to the Analytics server.
     */
    static AsyncAnalyticsClient create(String target) {
        return create(ManagedChannelBuilder.forTarget(target).build());
    }

    static AsyncAnalyticsClient create(String host, int port) {
        return create(ManagedChannelBuilder.forAddress(host, port).build());
    }

    static AsyncAnalyticsClient create(ManagedChannel channel) {
        return new DefaultAsyncAnalyticsClient(channel);
    }

    /**
     * Determines whether this {@link AsyncAnalyticsClient client} is closed or not.
     */
    boolean isClosed();

    /**
     * Returns how many gRPC calls that this {@link AsyncAnalyticsClient client} has executed.
     */
    long calls();

    /**
     * Refer to {@link AnalyticsClient#connectAck()} for the full documentation. This just returns a
     * {@link CompletableFuture}.
     */
    CompletableFuture<ResponseHandler<ConnectionAckResponse>> connectionAck();

    /**
     * Refer to {@link AnalyticsClient#receiveStats()} for the full documentation. This method is just
     * asynchronous rather than blocking.
     */
    CompletableFuture<ResponseHandler<ReceiveStatsResponse>> receiveStats();
}
