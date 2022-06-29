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

package org.noelware.analytics.client;

import io.grpc.*;
import java.util.List;
import java.util.concurrent.Executor;
import org.jetbrains.annotations.NotNull;
import org.noelware.analytics.client.async.AsyncAnalyticsClient;
import org.noelware.analytics.client.async.DefaultAsyncAnalyticsClient;
import org.noelware.analytics.client.blocking.BlockingAnalyticsClient;
import org.noelware.analytics.client.blocking.DefaultBlockingAnalyticsClient;

/**
 * Represents the main builder class to constructing the blocking or asynchronous clients.
 *
 * @since 28.06.22
 * @author Noel <cutie@floofy.dev>
 */
public class AnalyticsClientBuilder {
  /** Creates a new {@link AnalyticsClientBuilder builder class}. */
  @NotNull
  public static AnalyticsClientBuilder newBuilder(String target) {
    return new AnalyticsClientBuilder(target);
  }

  public static AnalyticsClientBuilder newBuilder(String name, int port) {
    return new AnalyticsClientBuilder(name, port);
  }

  private ManagedChannelBuilder<?> channelBuilder;

  private AnalyticsClientBuilder(String target) {
    this.channelBuilder = ManagedChannelBuilder.forTarget(target);
  }

  private AnalyticsClientBuilder(String name, int port) {
    this.channelBuilder = ManagedChannelBuilder.forAddress(name, port);
  }

  /**
   * Custom operation block to modify a {@link ManagedChannelBuilder} instance.
   *
   * @since 1.0-beta (28.06.22)
   * @return this
   */
  public AnalyticsClientBuilder modifyChannelBuilder(
      GenericModifyOperation<ManagedChannelBuilder<?>> operation) {
    operation.modify(channelBuilder);
    return this;
  }

  /**
   * Provides a custom Java executor.
   *
   * @since 1.0-beta (28.06.22)
   * @return this
   */
  public AnalyticsClientBuilder setChannelExecutor(Executor executor) {
    this.channelBuilder.executor(executor);
    return this;
  }

  /**
   * Provides a custom executor that will be used for operations that block or that are expensive in
   * calculation.
   *
   * @since 1.0-beta (28.06.22)
   * @return this
   */
  public AnalyticsClientBuilder setChannelOffloadExecutor(Executor executor) {
    this.channelBuilder.offloadExecutor(executor);
    return this;
  }

  public AnalyticsClientBuilder intercept(ClientInterceptor interceptor) {
    this.channelBuilder.intercept(interceptor);
    return this;
  }

  public AnalyticsClientBuilder intercept(ClientInterceptor... interceptors) {
    this.channelBuilder.intercept(interceptors);
    return this;
  }

  public AnalyticsClientBuilder intercept(List<ClientInterceptor> interceptors) {
    this.channelBuilder.intercept(interceptors);
    return this;
  }

  public AnalyticsClientBuilder userAgent(String userAgent) {
    this.channelBuilder.userAgent(userAgent);
    return this;
  }

  public AnalyticsClientBuilder usePlaintext() {
    this.channelBuilder.usePlaintext();
    return this;
  }

  public AsyncAnalyticsClient buildAsyncClient() {
    var channel = this.channelBuilder.build();
    return new DefaultAsyncAnalyticsClient(channel);
  }

  public BlockingAnalyticsClient buildBlockingClient() {
    var channel = this.channelBuilder.build();
    return new DefaultBlockingAnalyticsClient(channel);
  }
}
