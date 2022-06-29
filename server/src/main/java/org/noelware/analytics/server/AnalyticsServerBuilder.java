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

package org.noelware.analytics.server;

import io.grpc.ServerBuilder;
import java.util.UUID;
import org.noelware.analytics.protobufs.v1.BuildFlavour;
import org.noelware.analytics.server.impl.AnalyticsServerImpl;
import org.noelware.analytics.server.impl.AnalyticsService;

public class AnalyticsServerBuilder {
  public static AnalyticsServerBuilder newBuilder() {
    return newBuilder(55132);
  }

  public static AnalyticsServerBuilder newBuilder(int port) {
    return new AnalyticsServerBuilder(port);
  }

  private final ServerBuilder<?> serverBuilder;
  private BuildFlavour flavour;
  private UUID instanceUUID;
  private String buildDate;
  private String commitSha;
  private String product;
  private String version;

  public AnalyticsServerBuilder(int port) {
    this.serverBuilder = ServerBuilder.forPort(port);
  }

  public AnalyticsServerBuilder setVersion(String version) {
    this.version = version;
    return this;
  }

  public AnalyticsServerBuilder setProduct(String product) {
    this.product = product;
    return this;
  }

  public AnalyticsServerBuilder setCommitSha(String commitSha) {
    this.commitSha = commitSha;
    return this;
  }

  public AnalyticsServerBuilder setBuildDate(String buildDate) {
    this.buildDate = buildDate;
    return this;
  }

  public AnalyticsServerBuilder setInstanceUUID(UUID instanceUUID) {
    this.instanceUUID = instanceUUID;
    return this;
  }

  public AnalyticsServerBuilder setFlavour(BuildFlavour flavour) {
    this.flavour = flavour;
    return this;
  }

  public AnalyticsServerBuilder modifyServer(
      GenericModifyOperation<ServerBuilder<?>> modifyOperation) {
    modifyOperation.modify(this.serverBuilder);
    return this;
  }

  public AnalyticsServer build() {
    var server =
        new AnalyticsServerImpl(
            product, version, commitSha, buildDate, flavour, instanceUUID, null);
    modifyServer(
        (builder) -> {
          builder.addService(new AnalyticsService(server));
        });

    server.setServerImpl(serverBuilder.build());
    return server;
  }
}
