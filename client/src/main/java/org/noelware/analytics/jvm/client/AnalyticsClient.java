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

package org.noelware.analytics.jvm.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.io.Closeable;
import org.noelware.analytics.jvm.client.handlers.ResponseHandler;
import org.noelware.analytics.jvm.client.internal.blocking.DefaultBlockingAnalyticsClient;
import org.noelware.analytics.protobufs.v1.ConnectionAckResponse;
import org.noelware.analytics.protobufs.v1.ReceiveStatsResponse;

/**
 * Represents a client implementation of the Noelware Analytics protocol. This is mainly used to block
 * the thread that the gRPC request is being executed in, use the {@link org.noelware.analytics.jvm.client.async.AsyncAnalyticsClient asynchronous client}
 * for asynchronous usage.
 */
public interface AnalyticsClient extends Closeable {
    /**
     * Creates a new {@link AnalyticsClient analytics client} with the given target string.
     * @param target The target string to connect to the Analytics server.
     */
    static AnalyticsClient create(String target) {
        return create(ManagedChannelBuilder.forTarget(target).build());
    }

    static AnalyticsClient create(String host, int port) {
        return create(ManagedChannelBuilder.forAddress(host, port).build());
    }

    static AnalyticsClient create(ManagedChannel channel) {
        return new DefaultBlockingAnalyticsClient(channel);
    }

    /**
     * Determines whether this {@link AnalyticsClient client} is closed or not.
     */
    boolean isClosed();

    /**
     * Returns how many gRPC calls that this {@link AnalyticsClient client} has executed.
     */
    long calls();

    /**
     * Sends the {@link org.noelware.analytics.protobufs.v1.ConnectionAckRequest connection ack request} to the gRPC server to see
     * if the server is alive and well. This is meant as a heartbeat packet.
     *
     * @return {@link ResponseHandler<ConnectionAckResponse> response handler} of the given result of the request.
     */
    ResponseHandler<ConnectionAckResponse> connectAck();

    /**
     * Sends the {@link org.noelware.analytics.protobufs.v1.ReceiveStatsRequest receive stats request} to the gRPC server to collect
     * all the statistics that we can ingest.
     *
     * @return {@link ResponseHandler<ReceiveStatsResponse> response handler} of the given request
     */
    ResponseHandler<ReceiveStatsResponse> receiveStats();
}
