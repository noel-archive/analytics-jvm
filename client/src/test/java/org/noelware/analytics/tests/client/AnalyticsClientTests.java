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

package org.noelware.analytics.tests.client;

import static org.junit.jupiter.api.Assertions.*;

import io.grpc.ManagedChannelBuilder;
import java.io.IOException;
import java.time.Instant;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.noelware.analytics.jvm.client.AnalyticsClient;
import org.noelware.analytics.jvm.client.AnalyticsClientBuilder;
import org.noelware.analytics.jvm.client.handlers.ResponseHandler;
import org.noelware.analytics.jvm.server.AnalyticsServer;
import org.noelware.analytics.jvm.server.AnalyticsServerBuilder;
import org.noelware.analytics.protobufs.v1.BuildFlavour;
import org.noelware.analytics.protobufs.v1.ConnectionAckResponse;

public class AnalyticsClientTests {
    private static final Instant NOW = Instant.now();
    private static AnalyticsServer server;
    private static Thread serverThread;

    @BeforeAll
    public static void setupServer() {
        server = new AnalyticsServerBuilder()
                .withServiceToken("MGUyYTc0NTEtMmI1MC00NmMzLTg0ODEtOGM3YmQyNmRlN2NjOmJsYWhibGFoYmxhaA==")
                .withServerMetadata(metadata -> {
                    metadata.setDistributionType(BuildFlavour.DEB);
                    metadata.setProductName("analytics-server");
                    metadata.setCommitHash("noeluwu5");
                    metadata.setBuildDate(NOW);
                    metadata.setVersion("v0.0.0-devel.0");
                    metadata.setVendor("Noelware");
                })
                .build();

        serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        serverThread.start();
    }

    @AfterAll
    public static void destroyServer() throws IOException {
        serverThread.interrupt();
        server.close();
    }

    @Test
    public void test_canWeConnectOrNot() throws IOException {
        try (final AnalyticsClient client = AnalyticsClientBuilder.create("localhost", 10234)
                .withServiceToken("MGUyYTc0NTEtMmI1MC00NmMzLTg0ODEtOGM3YmQyNmRlN2NjOmJsYWhibGFoYmxhaA==")
                .withManagedChannel(ManagedChannelBuilder::usePlaintext)
                .build()) {
            final ResponseHandler<ConnectionAckResponse> resp = client.connectAck();
            assertTrue(resp.isSuccessful());

            final ConnectionAckResponse r = resp.getOrNull();
            assertNotNull(r);
            assertTrue(r.getConnected());
            assertEquals("0e2a7451-2b50-46c3-8481-8c7bd26de7cc", r.getInstanceUUID());
        }
    }
}
