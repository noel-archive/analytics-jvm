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

package org.noelware.analytics.jvm.client.handlers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.noelware.analytics.protobufs.v1.ConnectionAckResponse;

/**
 * Represents a {@link ResponseHandler<ConnectionAckResponse> response handler} for the {@link ConnectionAckResponse}
 * response object.
 */
public class ConnectionAckResponseHandler implements ResponseHandler<ConnectionAckResponse> {
    private final ConnectionAckResponse data;
    private final Throwable exception;
    private final boolean success;

    /**
     * Constructs a new {@link ConnectionAckResponseHandler} with the given {@link ConnectionAckResponse response}, which
     * indicates that the gRPC request was a success.
     *
     * @param data The {@link ConnectionAckResponse response} object.
     */
    public ConnectionAckResponseHandler(ConnectionAckResponse data) {
        this.data = data;
        exception = null;
        success = true;
    }

    /**
     * Constructs a new {@link ConnectionAckResponseHandler} with the given {@link Throwable exception}, which
     * indicates that the gRPC request had failed.
     *
     * @param exception {@link Throwable exception} that was thrown during the gRPC request
     */
    public ConnectionAckResponseHandler(Throwable exception) {
        this.exception = exception;
        this.data = null;
        success = false;
    }

    /**
     * Returns the thrown {@link Throwable exception} if the response has failed.
     */
    @Override
    @Nullable
    public Throwable getException() {
        return exception;
    }

    /**
     * Returns if the response was successful or not.
     */
    @Override
    public boolean isSuccessful() {
        return success;
    }

    /**
     * Returns the data that was returned from the server as {@link ConnectionAckResponse}, or a {@link Exception} thrown
     * if the response has failed.
     */
    @Override
    public @NotNull ConnectionAckResponse get() throws Throwable {
        if (exception != null) {
            throw exception;
        }

        assert data != null;
        return data;
    }

    /**
     * Returns the data that was returned from the server as {@link ConnectionAckResponse}, or <code>null</code>
     * if the response has failed.
     */
    @Override
    public @Nullable ConnectionAckResponse getOrNull() {
        return data;
    }
}
