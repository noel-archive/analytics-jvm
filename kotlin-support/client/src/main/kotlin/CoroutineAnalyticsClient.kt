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

package org.noelware.analytics.jvm.client.coroutines

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.noelware.analytics.jvm.client.AnalyticsClient
import org.noelware.analytics.jvm.client.async.AsyncAnalyticsClient
import org.noelware.analytics.jvm.client.handlers.ResponseHandler
import org.noelware.analytics.protobufs.v1.ConnectionAckResponse
import org.noelware.analytics.protobufs.v1.ReceiveStatsResponse

interface CoroutineAnalyticsClient {
    /**
     * Determines whether this [client][CoroutineAnalyticsClient] is closed or not.
     */
    val isClosed: Boolean

    /**
     * Returns how many gRPC calls that this [client][CoroutineAnalyticsClient] has executed.
     */
    val calls: Long

    /**
     * Refer to the [AnalyticsClient.connectAck] method for more information.
     */
    suspend fun connectionAck(): ResponseHandler<ConnectionAckResponse>

    /**
     * Refer to the [AnalyticsClient.receiveStats] method for more information.
     */
    suspend fun receiveStats(): ResponseHandler<ReceiveStatsResponse>
}

/**
 * Creates a new [CoroutineAnalyticsClient] with the given `target` to connect to
 * @param target The target host:port to connect to
 */
fun CoroutineAnalyticsClient.create(target: String): CoroutineAnalyticsClient = create(ManagedChannelBuilder.forTarget(target).build())

/**
 * Creates a new [CoroutineAnalyticsClient] with the given `target` and `port` to connect to
 * @param target target host
 * @param port   target port
 */
fun CoroutineAnalyticsClient.create(target: String, port: Int): CoroutineAnalyticsClient = create(ManagedChannelBuilder.forAddress(target, port).build())

/**
 * Creates a new [CoroutineAnalyticsClient] with the given `target` to connect to
 * @param target The target host:port to connect to
 * @param builder builder lambda to configure the [ManagedChannel]
 */
fun CoroutineAnalyticsClient.create(target: String, builder: ManagedChannelBuilder<*>.() -> Unit = {}): CoroutineAnalyticsClient = create(ManagedChannelBuilder.forTarget(target).apply(builder).build())

/**
 * Creates a new [CoroutineAnalyticsClient] with the given `target` and `port` to connect to
 * @param target  target host
 * @param port    target port
 * @param builder builder lambda to configure the [ManagedChannel]
 */
fun CoroutineAnalyticsClient.create(target: String, port: Int, builder: ManagedChannelBuilder<*>.() -> Unit = {}): CoroutineAnalyticsClient = create(ManagedChannelBuilder.forAddress(target, port).apply(builder).build())

/**
 * Creates a new [CoroutineAnalyticsClient] with the given [ManagedChannel].
 * @param channel [ManagedChannel] to connect to the gRPC server.
 */
fun CoroutineAnalyticsClient.create(channel: ManagedChannel): CoroutineAnalyticsClient = DefaultCoroutineAnalyticsClient(
    AsyncAnalyticsClient.create(channel)
)
