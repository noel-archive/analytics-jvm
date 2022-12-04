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

package org.noelware.analytics.jvm.client.internal.blocking;

import io.grpc.ManagedChannel;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.noelware.analytics.jvm.client.AnalyticsClient;
import org.noelware.analytics.jvm.client.handlers.ConnectionAckResponseHandler;
import org.noelware.analytics.jvm.client.handlers.ResponseHandler;
import org.noelware.analytics.jvm.client.handlers.RetrieveStatsResponseHandler;
import org.noelware.analytics.protobufs.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultBlockingAnalyticsClient implements AnalyticsClient {
    private final AnalyticsGrpc.AnalyticsBlockingStub stub;
    private final ManagedChannel channel;

    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final AtomicLong calls = new AtomicLong(0L);
    private final Logger LOG = LoggerFactory.getLogger(DefaultBlockingAnalyticsClient.class);

    public DefaultBlockingAnalyticsClient(ManagedChannel channel) {
        this.channel = channel;
        this.stub = AnalyticsGrpc.newBlockingStub(channel);
    }

    /**
     * Determines whether this {@link AnalyticsClient client} is closed or not.
     */
    @Override
    public boolean isClosed() {
        return closed.get();
    }

    /**
     * Returns how many gRPC calls that this {@link AnalyticsClient client} has executed.
     */
    @Override
    public long calls() {
        return calls.get();
    }

    /**
     * Sends the {@link ConnectionAckRequest connection ack request} to the gRPC server to see
     * if the server is alive and well. This is meant as a heartbeat packet.
     *
     * @return {@link ResponseHandler<ConnectionAckResponse> response handler} of the given result of the request.
     */
    @Override
    public ResponseHandler<ConnectionAckResponse> connectAck() {
        LOG.debug("Sending `connectionAck` RPC call...");

        final ConnectionAckRequest request = ConnectionAckRequest.newBuilder().build();
        try {
            final ConnectionAckResponse resp = stub.connectionAck(request);
            return new ConnectionAckResponseHandler(resp);
        } catch (Exception e) {
            return new ConnectionAckResponseHandler(e);
        }
    }

    /**
     * Sends the {@link ReceiveStatsRequest receive stats request} to the gRPC server to collect
     * all the statistics that we can ingest.
     *
     * @return {@link ResponseHandler< ReceiveStatsResponse > response handler} of the given request
     */
    @Override
    public ResponseHandler<ReceiveStatsResponse> receiveStats() {
        LOG.debug("Sending the `receiveStats` RPC call...");

        try {
            final ReceiveStatsResponse resp =
                    stub.retrieveStats(ReceiveStatsRequest.newBuilder().build());
            return new RetrieveStatsResponseHandler(resp);
        } catch (Exception e) {
            LOG.error("RPC call threw an execution exception:", e);
            return new RetrieveStatsResponseHandler(e);
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
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        if (closed.compareAndSet(false, true)) {
            LOG.warn("Shutting down analytics client connection!");

            channel.shutdownNow();
            try {
                channel.awaitTermination(10, TimeUnit.SECONDS);
                LOG.info("Client connection has shut down!");
            } catch (InterruptedException e) {
                LOG.error("Received thread interruption, client connection was not closed down cleanly.", e);
            }
        }
    }
}
