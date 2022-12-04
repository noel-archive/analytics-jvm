package org.noelware.analytics.jvm.client.internal;

import io.grpc.CallCredentials;
import io.grpc.Metadata;

import java.util.concurrent.Executor;
import static java.lang.String.format;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

public class ClientAuthorizationCredentials extends CallCredentials {
    private final Metadata.Key<String> AUTHORIZATION_META_KEY =
            Metadata.Key.of("Authorization", ASCII_STRING_MARSHALLER);

    private final String token;
    public ClientAuthorizationCredentials(String token) {
        this.token = token;
    }

    @Override
    public void applyRequestMetadata(RequestInfo requestInfo, Executor appExecutor, MetadataApplier applier) {
        final Metadata headers = new Metadata();
        headers.put(AUTHORIZATION_META_KEY, format("Bearer %s", token));
        applier.apply(headers);
    }

    /**
     * Should be a noop but never called; tries to make it clearer to implementors that they may break
     * in the future.
     */
    @Override
    public void thisUsesUnstableApi() {}
}
