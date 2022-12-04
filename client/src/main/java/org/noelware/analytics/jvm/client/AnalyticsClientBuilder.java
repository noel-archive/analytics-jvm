package org.noelware.analytics.jvm.client;

import io.grpc.ManagedChannelBuilder;
import org.noelware.analytics.jvm.client.internal.blocking.DefaultBlockingAnalyticsClient;

import java.util.function.Consumer;

/**
 * Represents a builder for constructing {@link AnalyticsClient} instances.
 */
public class AnalyticsClientBuilder {
    private final ManagedChannelBuilder<?> channelBuilder;
    private String serviceToken;

    AnalyticsClientBuilder(ManagedChannelBuilder<?> builder) {
        this.channelBuilder = builder;
    }

    public static AnalyticsClientBuilder create(String host) {
        return new AnalyticsClientBuilder(ManagedChannelBuilder.forTarget(host));
    }

    public static AnalyticsClientBuilder create(String host, int port) {
        return new AnalyticsClientBuilder(ManagedChannelBuilder.forAddress(host, port));
    }

    public AnalyticsClientBuilder withManagedChannel(Consumer<ManagedChannelBuilder<?>> consumer) {
        consumer.accept(channelBuilder);
        return this;
    }

    public AnalyticsClientBuilder withServiceToken(String token) {
        this.serviceToken = token;
        return this;
    }

    public AnalyticsClient build() {
        if (serviceToken == null) throw new IllegalStateException("Missing service token to use");
        return new DefaultBlockingAnalyticsClient(serviceToken, channelBuilder);
    }
}
