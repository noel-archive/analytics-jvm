package org.noelware.analytics.jvm.client.async;

import io.grpc.ManagedChannelBuilder;
import org.noelware.analytics.jvm.client.internal.async.DefaultAsyncAnalyticsClient;

import java.util.function.Consumer;

public class AsyncAnalyticsClientBuilder {
    private final ManagedChannelBuilder<?> channelBuilder;
    private String serviceToken;

    AsyncAnalyticsClientBuilder(ManagedChannelBuilder<?> builder) {
        this.channelBuilder = builder;
    }

    public static AsyncAnalyticsClientBuilder create(String host) {
        return new AsyncAnalyticsClientBuilder(ManagedChannelBuilder.forTarget(host));
    }

    public static AsyncAnalyticsClientBuilder create(String host, int port) {
        return new AsyncAnalyticsClientBuilder(ManagedChannelBuilder.forAddress(host, port));
    }

    public AsyncAnalyticsClientBuilder withManagedChannel(Consumer<ManagedChannelBuilder<?>> consumer) {
        consumer.accept(channelBuilder);
        return this;
    }

    public AsyncAnalyticsClientBuilder withServiceToken(String token) {
        this.serviceToken = token;
        return this;
    }

    public AsyncAnalyticsClient build() {
        if (serviceToken == null) throw new IllegalStateException("Missing service token to use");
        return new DefaultAsyncAnalyticsClient(serviceToken, channelBuilder);
    }
}
