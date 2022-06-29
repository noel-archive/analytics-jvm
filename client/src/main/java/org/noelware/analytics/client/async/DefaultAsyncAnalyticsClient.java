/*
 * 🌂 analytics-jvm: Client and server implementation of Noelware Analytics in Java, supported for both Java and Kotlin
 * Copyright (c) 2022 Noelware
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

package org.noelware.analytics.client.async;

import io.grpc.ManagedChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.noelware.analytics.client.data.ConnectionAckResponse;
import org.noelware.analytics.client.data.Response;
import org.noelware.analytics.client.data.RetrieveStatsResponse;
import org.noelware.analytics.protobufs.v1.AnalyticsGrpc;
import org.noelware.analytics.protobufs.v1.ConnectionAckRequest;
import org.noelware.analytics.protobufs.v1.ReceiveStatsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAsyncAnalyticsClient implements AsyncAnalyticsClient {
  private final AnalyticsGrpc.AnalyticsFutureStub stub;
  private final AtomicBoolean closed = new AtomicBoolean(false);
  private final AtomicLong calls = new AtomicLong(0L);
  private final ManagedChannel channel;
  private final Logger log = LoggerFactory.getLogger(DefaultAsyncAnalyticsClient.class);

  public DefaultAsyncAnalyticsClient(ManagedChannel channel) {
    this.channel = channel;
    this.stub = AnalyticsGrpc.newFutureStub(channel);
  }

  @Override
  public Long getCalls() {
    return calls.get();
  }

  @Override
  public boolean getClosed() {
    return closed.get();
  }

  @Override
  public CompletableFuture<Response<ConnectionAckResponse>> connectionAck() {
    log.debug("Now requesting to `connectionAck` RPC call...");

    var request = ConnectionAckRequest.newBuilder().build();
    var future = new CompletableFuture<Response<ConnectionAckResponse>>();
    try {
      log.debug("Trying request...");
      var result = this.stub.connectionAck(request).get();

      log.debug("Success! Transforming response...");
      var response = new ConnectionAckResponse();
      response.setConnected(result.getConnected());
      response.setInstanceUUID(result.getInstanceUUID());

      log.debug("Response was transformed. RPC call is a success!");
      future.complete(new Response<>(response));
    } catch (ExecutionException | InterruptedException e) {
      log.error("RPC call threw an execution exception:", e);
      future.complete(new Response<>(e));
    }

    return future;
  }

  public CompletableFuture<Response<RetrieveStatsResponse>> retrieveStats() {
    log.debug("Now calling RPC call `retrieveStats` on server...");

    var request = ReceiveStatsRequest.newBuilder().build();
    var future = new CompletableFuture<Response<RetrieveStatsResponse>>();

    try {
      log.debug("Trying RPC call...");
      var result = this.stub.retrieveStats(request).get();

      log.debug("Success! Transforming response...");
      var response = new RetrieveStatsResponse();
      response.setBuildFlavour(response.getBuildFlavour());
      response.setSnapshotDate(result.getSnapshotDate());
      response.setBuildDate(result.getBuildDate());
      response.setCommitSha(result.getCommitSha());
      response.setProduct(result.getProduct());
      response.setVersion(result.getVersion());
      response.setData(result.getData());

      log.debug("Response was transformed. RPC call is a success!");

      calls.incrementAndGet();
      future.complete(new Response<>(response));
    } catch (ExecutionException | InterruptedException e) {
      log.error("RPC call threw an execution exception:", e);
      future.complete(new Response<>(e));
    }

    return future;
  }

  @Override
  public void close() {
    var wasClosed = closed.get();
    if (wasClosed) {
      log.warn("This analytics client was already closed!");
      return;
    }

    if (closed.compareAndSet(false, true)) {
      log.warn("Closing client...");

      this.channel.shutdownNow();
      try {
        this.channel.awaitTermination(10, TimeUnit.SECONDS);
        log.warn("gRPC channel should be shutting down at any second.");
      } catch (InterruptedException e) {
        log.error("Received interrupted exception signal, channel was not closed down cleanly!");
      }
    }
  }
}
