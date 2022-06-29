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

import org.jetbrains.annotations.NotNull;
import org.noelware.analytics.client.async.AsyncAnalyticsClient;
import org.noelware.analytics.client.blocking.BlockingAnalyticsClient;

/**
 * Represents the main abstraction for using the Analytics client. This stub is empty, so you can
 * choose over the {@link org.noelware.analytics.client.blocking.BlockingAnalyticsClient Blocking
 * client} or the {@link org.noelware.analytics.client.async.AsyncAnalyticsClient asynchronous
 * client}.
 *
 * @since 28.06.22
 * @author Noel <cutie@floofy.dev>
 */
public class AnalyticsClient {
  /** Returns a new builder object to construct the blocking or asynchronous client. */
  @NotNull
  static AnalyticsClientBuilder newBuilder(String target) {
    return AnalyticsClientBuilder.newBuilder(target);
  }

  @NotNull
  static AsyncAnalyticsClient newAsyncClient(String target) {
    return newBuilder(target).buildAsyncClient();
  }

  @NotNull
  static BlockingAnalyticsClient newBlockingClient(String target) {
    return newBuilder(target).buildBlockingClient();
  }
}
