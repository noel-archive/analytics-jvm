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

package org.noelware.analytics.jvm.client.internal.async;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.jetbrains.annotations.NotNull;
import org.noelware.analytics.jvm.client.AnalyticsClient;
import org.noelware.analytics.jvm.client.async.AsyncAnalyticsClient;
import org.noelware.analytics.jvm.client.handlers.ConnectionAckResponseHandler;
import org.noelware.analytics.jvm.client.handlers.ResponseHandler;
import org.noelware.analytics.jvm.client.handlers.RetrieveStatsResponseHandler;
import org.noelware.analytics.jvm.client.internal.ClientAuthorizationCredentials;
import org.noelware.analytics.protobufs.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAsyncAnalyticsClient implements AsyncAnalyticsClient {
    private final AnalyticsGrpc.AnalyticsFutureStub stub;
    private final ManagedChannel channel;
    private final String instanceUUID;
    private final String rawToken;

    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final AtomicLong calls = new AtomicLong(0L);
    private final Logger LOG = LoggerFactory.getLogger(DefaultAsyncAnalyticsClient.class);

    public DefaultAsyncAnalyticsClient(String serviceToken, ManagedChannelBuilder<?> channel) {
        final String finalResult = new String(Base64.getDecoder().decode(serviceToken));
        final String[] split = finalResult.split(":", 2);
        if (split.length != 2) throw new IllegalStateException("Service token was not split as 'instanceUUID:token'");

        this.instanceUUID = split[0];
        this.rawToken = split[1];
        this.channel = channel.build();
        stub = AnalyticsGrpc.newFutureStub(this.channel)
                .withCallCredentials(new ClientAuthorizationCredentials(rawToken));
    }

    /**
     * Determines whether this {@link AsyncAnalyticsClient client} is closed or not.
     */
    @Override
    public boolean isClosed() {
        return closed.get();
    }

    /**
     * Returns how many gRPC calls that this {@link AsyncAnalyticsClient client} has executed.
     */
    @Override
    public long calls() {
        return calls.get();
    }

    /**
     * Returns the instance UUID that the analytics client is connecting to.
     */
    @Override
    public @NotNull String instanceUUID() {
        return instanceUUID;
    }

    /**
     * Refer to {@link AnalyticsClient#connectAck()} for the full documentation. This just returns a
     * {@link CompletableFuture}.
     */
    @Override
    public CompletableFuture<ResponseHandler<ConnectionAckResponse>> connectionAck() {
        LOG.debug("Sending `connectionAck` RPC call...");

        final CompletableFuture<ResponseHandler<ConnectionAckResponse>> fut = new CompletableFuture<>();
        try {
            final ConnectionAckResponse resp = stub.connectionAck(
                            ConnectionAckRequest.newBuilder().build())
                    .get();
            fut.complete(new ConnectionAckResponseHandler(resp));
        } catch (ExecutionException | InterruptedException e) {
            LOG.error("RPC call threw an execution exception:", e);
            fut.complete(new ConnectionAckResponseHandler(e));
        } catch (Exception e) {
            fut.completeExceptionally(e);
        }

        return fut;
    }

    /**
     * Refer to {@link AnalyticsClient#receiveStats()} for the full documentation. This method is just
     * asynchronous rather than blocking.
     */
    @Override
    public CompletableFuture<ResponseHandler<ReceiveStatsResponse>> receiveStats() {
        LOG.debug("Sending the `receiveStats` RPC call...");

        final CompletableFuture<ResponseHandler<ReceiveStatsResponse>> fut = new CompletableFuture<>();
        try {
            final ReceiveStatsResponse resp =
                    stub.retrieveStats(ReceiveStatsRequest.newBuilder().build()).get();
            fut.complete(new RetrieveStatsResponseHandler(resp));
        } catch (ExecutionException | InterruptedException e) {
            LOG.error("RPC call threw an execution exception:", e);
            fut.complete(new RetrieveStatsResponseHandler(e));
        } catch (Exception e) {
            fut.completeExceptionally(e);
        }

        return fut;
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
