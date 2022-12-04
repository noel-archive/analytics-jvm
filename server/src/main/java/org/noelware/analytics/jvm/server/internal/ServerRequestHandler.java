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

package org.noelware.analytics.jvm.server.internal;

import com.google.protobuf.NullValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import io.grpc.stub.StreamObserver;
import java.util.List;
import org.noelware.analytics.jvm.server.AnalyticsServer;
import org.noelware.analytics.jvm.server.extensions.Extension;
import org.noelware.analytics.jvm.server.util.GrpcValueUtil;
import org.noelware.analytics.protobufs.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerRequestHandler extends AnalyticsGrpc.AnalyticsImplBase {
    private final Logger LOG = LoggerFactory.getLogger(ServerRequestHandler.class);
    private final AnalyticsServer server;

    ServerRequestHandler(AnalyticsServer server) {
        this.server = server;
    }

    @Override
    public void connectionAck(ConnectionAckRequest request, StreamObserver<ConnectionAckResponse> observer) {
        LOG.info("Processing CONNECTION_ACK request...");

        final ConnectionAckResponse.Builder response = ConnectionAckResponse.newBuilder();
        response.setConnected(true);
        response.setInstanceUUID(server.instanceUUID());

        observer.onNext(response.build());
        observer.onCompleted();
    }

    @Override
    public void retrieveStats(ReceiveStatsRequest request, StreamObserver<ReceiveStatsResponse> observer) {
        final List<Extension<?>> extensions = server.extensions().extensions();
        LOG.info("Ingesting data from {} extensions...", extensions.size());

        final ReceiveStatsResponse.Builder resp = ReceiveStatsResponse.newBuilder();
        resp.setBuildFlavour(server.metadata().distributionType());
        resp.setCommitSha(server.metadata().commitHash());
        resp.setBuildDate(server.metadata().buildDate());
        resp.setProduct(server.metadata().product());
        resp.setVersion(server.metadata().version());

        final Struct.Builder data = Struct.newBuilder();
        for (Extension<?> extension : extensions) {
            final Object payload = extension.supply();
            if (payload == null) {
                data.putFields(
                        extension.name(),
                        Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build());
                continue;
            }

            data.putFields(extension.name(), GrpcValueUtil.toValue(payload));
        }

        observer.onNext(resp.build());
        observer.onCompleted();
    }
}
