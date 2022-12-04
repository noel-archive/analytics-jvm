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

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerAuthenticationHandler implements ServerInterceptor {
    private final Metadata.Key<String> AUTHORIZATION_META_KEY =
            Metadata.Key.of("Authorization", ASCII_STRING_MARSHALLER);

    private final Logger LOG = LoggerFactory.getLogger(ServerAuthenticationHandler.class);
    private final DefaultAnalyticsServer server;

    ServerAuthenticationHandler(DefaultAnalyticsServer server) {
        this.server = server;
    }

    /**
     * Intercept {@link ServerCall} dispatch by the {@code next} {@link ServerCallHandler}. General
     * semantics of {@link ServerCallHandler#startCall} apply and the returned
     * {@link ServerCall.Listener} must not be {@code null}.
     *
     * <p>If the implementation throws an exception, {@code call} will be closed with an error.
     * Implementations must not throw an exception if they started processing that may use {@code
     * call} on another thread.
     *
     * @param call    object to receive response messages
     * @param headers which can contain extra call metadata from {@link ClientCall#start},
     *                e.g. authentication credentials.
     * @param next    next processor in the interceptor chain
     * @return listener for processing incoming messages for {@code call}, never {@code null}.
     */
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        LOG.info("Now intercepting call [{}] to check if it is a valid request...", call.getMethodDescriptor());

        final String token = server.token();
        final String auth = headers.get(AUTHORIZATION_META_KEY);
        Status status;

        if (auth == null) {
            status = Status.UNAUTHENTICATED.withDescription("Service token is missing in request");
        } else if (!auth.startsWith("Bearer")) {
            status = Status.UNAUTHENTICATED.withDescription("Service token didn't start with \"Bearer\"");
        } else {
            final String[] split = auth.split(" ", 2);
            if (split.length != 2) {
                status = Status.FAILED_PRECONDITION.withDescription("Token was not split as \"TokenType Token\"");
            } else {
                final String actual = split[1];
                if (!actual.equals(token)) {
                    status = Status.UNAUTHENTICATED.withDescription("Invalid token provided");
                } else {
                    return Contexts.interceptCall(Context.current(), call, headers, next);
                }
            }
        }

        call.close(status, headers);
        return new ServerCall.Listener<>() {};
    }
}
