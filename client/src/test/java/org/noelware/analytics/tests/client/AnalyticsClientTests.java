package org.noelware.analytics.tests.client;

import static org.junit.jupiter.api.Assertions.*;

import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
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

import java.io.IOException;
import java.time.Instant;

public class AnalyticsClientTests {
    private final static Instant NOW = Instant.now();
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
        try (final AnalyticsClient client = AnalyticsClientBuilder
                .create("localhost", 10234)
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
