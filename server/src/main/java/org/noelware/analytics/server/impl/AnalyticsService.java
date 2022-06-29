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

package org.noelware.analytics.server.impl;

import com.google.protobuf.Struct;
import com.google.protobuf.Timestamp;
import com.google.protobuf.Value;
import io.grpc.stub.StreamObserver;
import java.time.Instant;
import java.util.HashMap;
import org.noelware.analytics.protobufs.v1.*;
import org.noelware.analytics.server.AnalyticsServer;

public class AnalyticsService extends AnalyticsGrpc.AnalyticsImplBase {
  private final AnalyticsServer server;

  public AnalyticsService(AnalyticsServer server) {
    this.server = server;
  }

  @Override
  public void connectionAck(
      ConnectionAckRequest request, StreamObserver<ConnectionAckResponse> responseObserver) {
    var response =
        ConnectionAckResponse.newBuilder()
            .setConnected(true)
            .setInstanceUUID(server.getInstanceUUID().toString())
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void retrieveStats(
      ReceiveStatsRequest request, StreamObserver<ReceiveStatsResponse> responseObserver) {
    var version = server.getVersion();
    var product = server.getProduct();
    var commitSha = server.getCommitSha();
    var buildDate = server.getBuildDate();
    var now = Instant.now();
    var snapshotDate =
        Timestamp.newBuilder().setNanos(now.getNano()).setSeconds(now.getEpochSecond()).build();
    var data = new HashMap<>(server.getStatistics());

    server
        .getPluginRegistry()
        .onEachPlugin(
            (plugin) -> {
              var stats = plugin.getStatistics();
              if (stats == null) {
                return null;
              }

              var struct = Struct.newBuilder();
              struct.getFieldsMap().putAll(stats.second());

              data.put(stats.first(), Value.newBuilder().setStructValue(struct.build()).build());
              return null;
            });

    var dataStruct = Struct.newBuilder();
    dataStruct.getFieldsMap().putAll(data);

    var response =
        ReceiveStatsResponse.newBuilder()
            .setBuildDate(buildDate)
            .setBuildFlavour(server.getBuildFlavour())
            .setCommitSha(commitSha)
            .setSnapshotDate(snapshotDate)
            .setProduct(product)
            .setVersion(version)
            .setData(dataStruct.build())
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
