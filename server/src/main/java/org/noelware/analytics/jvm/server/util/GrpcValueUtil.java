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

package org.noelware.analytics.jvm.server.util;

import com.google.protobuf.*;
import java.util.List;
import org.noelware.analytics.jvm.server.serialization.Serializable;

/**
 * Auxiliary class for {@link Value} transformations.
 */
public class GrpcValueUtil {
    private GrpcValueUtil() {}

    /**
     * Transforms a given <code>payload</code> and tries to transform it to {@link Value a serializable gRPC value}.
     * @param payload object payload to transform
     * @throws IllegalStateException if the given <code>payload</code> couldn't be transformed
     * @return {@link Value serializable gRPC value}
     */
    public static Value toValue(Object payload) {
        final Value.Builder valueBuilder = Value.newBuilder();
        if (payload == null)
            return valueBuilder.setNullValue(NullValue.NULL_VALUE).build();
        else if (payload instanceof String)
            return valueBuilder.setStringValue((String) payload).build();
        else if (payload instanceof Number)
            return valueBuilder.setNumberValue(((Number) payload).doubleValue()).build();
        else if (payload instanceof Boolean)
            return valueBuilder.setBoolValue((Boolean) payload).build();
        else if (payload instanceof Serializable) return ((Serializable) payload).toGrpcValue();
        else if (payload instanceof List<?>)
            return valueBuilder
                    .setListValue(ListValue.newBuilder()
                            .addAllValues(((List<?>) payload)
                                    .stream().map(GrpcValueUtil::toValue).toList())
                            .build())
                    .build();
        else if (payload instanceof Value) return (Value) payload;
        else if (payload instanceof ListValue)
            return valueBuilder.setListValue((ListValue) payload).build();
        else if (payload instanceof Struct)
            return valueBuilder.setStructValue((Struct) payload).build();
        else
            throw new IllegalStateException(
                    "Payload [%s] is not supported in #toValue(Object), maybe implement Serializable?"
                            .formatted(payload));
    }
}
