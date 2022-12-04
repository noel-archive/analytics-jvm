package org.noelware.analytics.jvm.client.coroutines

import kotlinx.coroutines.future.await
import org.noelware.analytics.jvm.client.async.AsyncAnalyticsClient
import org.noelware.analytics.jvm.client.handlers.ResponseHandler
import org.noelware.analytics.protobufs.v1.ConnectionAckResponse
import org.noelware.analytics.protobufs.v1.ReceiveStatsResponse

internal class DefaultCoroutineAnalyticsClient(private val analyticsClient: AsyncAnalyticsClient): CoroutineAnalyticsClient {
    override val isClosed: Boolean
        get() = analyticsClient.isClosed

    override val calls: Long
        get() = analyticsClient.calls()

    override suspend fun connectionAck(): ResponseHandler<ConnectionAckResponse> = analyticsClient.connectionAck().await()
    override suspend fun receiveStats(): ResponseHandler<ReceiveStatsResponse> = analyticsClient.receiveStats().await()
}
