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

import com.google.protobuf.Value;
import io.grpc.Server;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.noelware.analytics.protobufs.v1.BuildFlavour;
import org.noelware.analytics.server.AnalyticsServer;
import org.noelware.analytics.server.plugins.PluginRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalyticsServerImpl implements AnalyticsServer {
  private final PluginRegistry registry = new PluginRegistryImpl();
  private final AtomicBoolean closed = new AtomicBoolean(false);
  private final AtomicBoolean started = new AtomicBoolean(false);
  private final Logger log = LoggerFactory.getLogger(AnalyticsServerImpl.class);
  private Server serverImpl;

  private final BuildFlavour flavour;
  private final UUID instanceUUID;
  private final String buildDate;
  private final String commitSha;
  private final String product;
  private final String version;

  public AnalyticsServerImpl(
      String product,
      String version,
      String commitSha,
      String buildDate,
      BuildFlavour flavour,
      UUID instance,
      Server server) {
    this.instanceUUID = instance;
    this.buildDate = buildDate;
    this.commitSha = commitSha;
    this.serverImpl = server;
    this.flavour = flavour;
    this.product = product;
    this.version = version;
  }

  @Override
  public PluginRegistry getPluginRegistry() {
    return registry;
  }

  @Override
  public void start() throws IOException {
    // If it was already started, we shouldn't do nothing.
    if (started.get()) return;

    log.info("Starting gRPC server...");
    started.set(true);
    serverImpl.start();
  }

  @Override
  public Map<String, Value> getStatistics() {
    return null;
  }

  @Override
  public BuildFlavour getBuildFlavour() {
    return flavour;
  }

  @Override
  public UUID getInstanceUUID() {
    return instanceUUID;
  }

  @Override
  public String getCommitSha() {
    return commitSha;
  }

  @Override
  public String getBuildDate() {
    return buildDate;
  }

  @Override
  public String getProduct() {
    return product;
  }

  @Override
  public String getVersion() {
    return version;
  }

  @Override
  public void close() {
    if (closed.compareAndSet(false, true)) {
      started.set(false);

      log.warn("Shutting off server...");
      try {
        serverImpl.shutdownNow();
        serverImpl.awaitTermination();
        log.warn("Done!");
      } catch (InterruptedException e) {
        log.error("Unable to shutdown server:", e);
      }
    }
  }

  public void setServerImpl(Server server) {
    if (serverImpl != null) return;
    this.serverImpl = server;
  }
}
