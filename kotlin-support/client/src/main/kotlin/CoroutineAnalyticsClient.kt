package org.noelware.analytics.jvm.client.coroutines

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.noelware.analytics.jvm.client.handlers.ResponseHandler
import org.noelware.analytics.protobufs.v1.ConnectionAckResponse
import org.noelware.analytics.protobufs.v1.ReceiveStatsResponse
import org.noelware.analytics.jvm.client.AnalyticsClient
import org.noelware.analytics.jvm.client.async.AsyncAnalyticsClient

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
    AsyncAnalyticsClient.create(channel))
